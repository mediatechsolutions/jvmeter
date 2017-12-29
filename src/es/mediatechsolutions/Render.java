package es.mediatechsolutions;


import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

public interface Render {
    void manage(ThreadMXBean proxy);

    void manage(RuntimeMXBean proxy);

    void manage(MemoryMXBean proxy);

    void manage(GarbageCollectorMXBean gcproxy);

    void startMetric();

    void endMetric();

    void manageError(String x);
}
