package es.mediatechsolutions;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = CommandLine.populateCommand(new Arguments(), args);

        Server server = new Server(arguments);
        server.run();
    }
}
