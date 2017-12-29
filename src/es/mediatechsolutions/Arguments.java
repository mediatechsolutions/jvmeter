package es.mediatechsolutions;

import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;


public class Arguments {
    public static final int MIN_DELAY_SECONDS = 10;
    @Option(names = { "-v", "--verbose" }, description = "Be verbose.")
    private Boolean verbose = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
    private boolean helpRequested = false;

    @Option(names = {"-d", "--daemon"}, description = "Daemonize")
    private boolean daemon = false;

    @Option(names = {"--delay"}, description = "Delay between checks for daemon mode")
    private int delaySeconds = 0;

    @Option(names = {"-s", "--service"}, description = "service to watch. You can select it multiple times.")
    private List<String> services;

    @Option(names = {"--csvfile"}, description = "Writes a CSV file with results")
    private List<String> csvfiles;

    @Option(names = {"--nagios"}, description = "Write results to Nagios or Icinga")
    private List<String> nagios;

    @Option(names = {"--stdout"}, description = "Write results to STDOUT")
    private boolean useStdout = false;


    public Boolean isVerbose() {
        return verbose;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public int getDelaySeconds() { return delaySeconds; }

    public List<String> getServices() {
        return services;
    }

    public List<Render> getRenders() {
        List<Render> result = new ArrayList<>();

        if (useStdout) {
            result.add(new StdoutRender());
        }

        if (csvfiles != null) {
            csvfiles.forEach((f) -> {
                result.add(new CsvFileRender(f));
            });
        }

        if (nagios != null) {
            nagios.forEach((f) -> {
                result.add(new NagiosRender(f));
            });
        }

        return result;
    }
}
