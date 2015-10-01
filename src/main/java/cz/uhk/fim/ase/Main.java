package cz.uhk.fim.ase;

import cz.uhk.fim.ase.network.Benchmark;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Specify please command as first argument");
        } else {
            String command = args[0];
            switch (command) {
                case "network-benchmark":
                    Benchmark benchmark = new Benchmark();
                    benchmark.run();
                    break;

                default:
                    System.out.println("Unknown command '" + command + "'");
            }
        }
    }
}
