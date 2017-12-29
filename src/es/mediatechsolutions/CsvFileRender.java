package es.mediatechsolutions;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;


public class CsvFileRender implements Render {
    public CsvFileRender(String filename) {
        super();
    }

    @Override
    public void manage(ThreadMXBean proxy) {

    }

    @Override
    public void manage(RuntimeMXBean proxy) {

    }

    @Override
    public void manage(MemoryMXBean proxy) {

    }

    @Override
    public void manage(GarbageCollectorMXBean gcproxy) {

    }

    @Override
    public void startMetric() {

    }

    @Override
    public void endMetric() {

    }

    @Override
    public void manageError(String x) {

    }
}
