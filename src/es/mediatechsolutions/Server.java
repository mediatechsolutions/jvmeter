package es.mediatechsolutions;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private final Arguments arguments;
    private List<Service> processes = new ArrayList<>();

    public Server(Arguments arguments) {
        this.arguments = arguments;
    }

    public void run() {
        createAll();
        startAll();
        waitAll();
        shutdownAll();
    }

    private void shutdownAll() {
        processes.forEach(this::shutdown);
    }

    private void shutdown(Service service) {
        service.shutdown();
    }

    private void createAll() {
        arguments.getServices().forEach(this::create);
    }

    private void create(String s) {
        try {
            Service service = new Service(s, arguments.getRenders());
            service.setDelaySeconds(arguments.getDelaySeconds());
            processes.add(service);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void startAll() {
        processes.forEach(this::start);
    }

    private void start(Service s) {
        if (arguments.isDaemon()) {
            s.start();
        } else {
            s.runOnce();
        }
    }

    private void waitAll() {
        if (arguments.isDaemon()) {
            processes.forEach(this::wait);
        }
    }

    private void wait(Service service) {
        try {
            service.join();
        } catch (InterruptedException e) {
            debug("Stopping");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void debug(String message) {
        if (arguments.isVerbose()) {
            print(message);
        }
    }

    private void print(String message) {
        System.err.println(message);
    }
}
