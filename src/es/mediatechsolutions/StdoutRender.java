package es.mediatechsolutions;

import java.lang.management.*;
import java.util.Date;

public class StdoutRender implements Render {
    @Override
    public void manage(ThreadMXBean proxy) {
        print("===== Thread ======");
        print("Daemon Thread Count", proxy.getDaemonThreadCount());
        print("Peak Thread Count", proxy.getPeakThreadCount());
        print("Thread Count", proxy.getThreadCount());
        print("Total Started Thread Count", proxy.getTotalStartedThreadCount());
    }

    @Override
    public void manage(RuntimeMXBean proxy) {
        print("===== Runtime ======");
        print("Uptime", proxy.getUptime());
    }

    @Override
    public void manage(MemoryMXBean proxy) {
        print("===== Memory HEAP ======");
        manage(proxy.getHeapMemoryUsage());
        print("===== Memory NON HEAP ======");
        manage(proxy.getNonHeapMemoryUsage());
        print("===== Memory OTHER ======");
        print("Object Pending Finalization Count", proxy.getObjectPendingFinalizationCount());
    }

    @Override
    public void manage(GarbageCollectorMXBean proxy) {
        print("===== Garbage Collector " + proxy.getName() + " ======");
        print("Collection Count", proxy.getCollectionCount());
        print("Collection Time", proxy.getCollectionTime());
    }

    @Override
    public void startMetric() {
        Date now = new Date();
        print("------------------------------- " + now.toString() + " ----------------------------");
    }

    @Override
    public void endMetric() {

    }

    @Override
    public void manageError(String x) {

    }

    private void manage(MemoryUsage mem) {
        print("Committed", mem.getCommitted());
        print("Init", mem.getInit());
        print("Max", mem.getMax());
        print("Used", mem.getUsed());
    }

    private void print(String k, Object v) {
        print("  " + k + ": " + v.toString());

    }

    private void print(String message) {
        System.out.println(message);
    }
}
