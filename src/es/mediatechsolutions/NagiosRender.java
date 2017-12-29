package es.mediatechsolutions;

import java.lang.management.*;


public class NagiosRender implements Render {
    private final String endpoint;
    private String output = "|";

    public NagiosRender(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void manage(ThreadMXBean proxy) {
        write("threads.daemon", proxy.getDaemonThreadCount());
        write("threads.peak", proxy.getPeakThreadCount());
        write("threads.count", proxy.getThreadCount());
        write("threads.count", proxy.getTotalStartedThreadCount());
    }

    @Override
    public void manage(RuntimeMXBean proxy) {
        write("runtime.uptime", proxy.getUptime());
    }

    @Override
    public void manage(MemoryMXBean proxy) {
        manage("memory.heap", proxy.getHeapMemoryUsage());
        manage("memory.nonheap", proxy.getNonHeapMemoryUsage());
        write("memory.object.pending.finalization", proxy.getObjectPendingFinalizationCount());
    }

    private void manage(String prefix, MemoryUsage mem) {
        write(prefix + ".committed", mem.getCommitted());
        write(prefix + ".init", mem.getInit());
        write(prefix + ".max", mem.getMax());
        write(prefix + ".used", mem.getUsed());
    }

    @Override
    public void manage(GarbageCollectorMXBean proxy) {
        write("gc.collection.count", proxy.getCollectionCount());
        write("gc.collection.time", proxy.getCollectionTime());
    }

    @Override
    public void startMetric() {
        output = "|";
    }

    @Override
    public void endMetric() {
        System.out.println(output);
    }

    @Override
    public void manageError(String x) {
        output = x + "; " + output;
    }

    public void write(String label, long value) {
        write(label, String.format("%d", value));
    }

    public void write(String label, int value) {
        write(label, String.format("%d", value));
    }

    public void write(String label, String value) {
        output += label + '=' + value + ' ';
    }
}
