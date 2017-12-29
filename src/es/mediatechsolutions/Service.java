package es.mediatechsolutions;

import javafx.beans.property.SimpleSetProperty;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;


public class Service extends Thread {
    private final List<Render> renders;
    private final JMXServiceURL url;
    private JMXConnector jmxconnector;
    private MBeanServerConnection mbeanServerConnection;
    private Set<ObjectName> gcNames = new SimpleSetProperty<>();
    private int delaySeconds = 10;

    public Service(String url, List<Render> renders) throws MalformedURLException {
        super();
        this.renders = renders;
        this.url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
        connect();
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    private void connect() {
        try {
            jmxconnector = JMXConnectorFactory.connect(url);
        } catch (IOException e) {
            manageError("Cannot stablish connectivity");
            return;
        }
        try {
            mbeanServerConnection = jmxconnector.getMBeanServerConnection();
            debug("Connected");
        } catch (IOException e) {
            manageError("Cannot connect");
        }
    }

    private void reconnect() {
        try {
            if (jmxconnector == null) {
                connect();
                return;
            }
            mbeanServerConnection = jmxconnector.getMBeanServerConnection();
            debug("Connected");
        } catch (IOException e) {
            connect();
        }
    }

    public void shutdown() {
        try {
            if (jmxconnector != null) {
                jmxconnector.close();
            }
        } catch (IOException e) {
        }
        debug("Disconnected");
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            if (mbeanServerConnection == null) {
                reconnect();
            }
            try {
                runOnceWithExceptions();
                sleep(Math.max(delaySeconds, 10) * 1000);
            } catch (IOException e) {
                reconnect();
            } catch (InterruptedException e) {
                currentThread().interrupt();
                break;
            }
        }
    }

    public void runOnce() {
        try {
            runOnceWithExceptions();
        } catch (IOException e) {
            debug("Disconnected");
            mbeanServerConnection = null;
        }
    }

    private void runOnceWithExceptions() throws IOException {
        if (mbeanServerConnection == null) {
            return;
        }
        try {
            processMetricStart();
            processMemoryBeans();
            processRuntimeBeans();
            processThreadBeans();
            processGCBeans();
        } catch (IOException e) {
            manageError("Disconnected");
            mbeanServerConnection = null;
        } finally {
            processMetricEnd();
        }
    }

    private void processMetricStart() {
        renders.forEach((r) -> r.startMetric());
    }

    private void processMetricEnd() {
        renders.forEach((r) -> r.endMetric());
    }

    private void processThreadBeans() throws IOException {
        ThreadMXBean threadproxy =
                ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
                        ManagementFactory.THREAD_MXBEAN_NAME,
                        ThreadMXBean.class);
        renders.forEach((r) -> {
            r.manage(threadproxy);
        });
    }

    private void processRuntimeBeans() throws IOException {
        RuntimeMXBean runtimeproxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
                ManagementFactory.RUNTIME_MXBEAN_NAME,
                RuntimeMXBean.class);
        renders.forEach((r) -> {
            r.manage(runtimeproxy);
        });
    }

    private void processMemoryBeans() throws IOException {
        MemoryMXBean memoryproxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
                ManagementFactory.MEMORY_MXBEAN_NAME,
                MemoryMXBean.class);
        renders.forEach((r) -> {
            r.manage(memoryproxy);
        });
    }

    private void processGCBeans() throws IOException {
        for (ObjectName gc : getGCs()) {
            GarbageCollectorMXBean gcproxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
                    gc.getCanonicalName(),
                    GarbageCollectorMXBean.class);
            renders.forEach((r) -> {
                r.manage(gcproxy);
            });
        }
    }

    private Set<ObjectName> getGCs() throws IOException {
        if (gcNames == null || gcNames.isEmpty()) {
            try {
                ObjectName names = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
                gcNames = mbeanServerConnection.queryNames(names, null);
            } catch (MalformedObjectNameException e) {
            }
        }
        return gcNames;
    }

    private void manageError(String x) {
        System.err.println(x);
        renders.forEach((r) -> {
            r.manageError(x);
        });
    }

    private void debug(String x) {
        System.err.println(x);
    }
}
