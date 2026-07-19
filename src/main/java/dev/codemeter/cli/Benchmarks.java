package dev.codemeter.cli;

import dev.codemeter.core.model.ScanResult;
import java.util.Map;
import java.util.TreeMap;

public class Benchmarks {

    // Tree map sorted by LOC (Lines of Code) keys
    private static final TreeMap<Long, String> BENCHMARKS = new TreeMap<>();

    static {
        BENCHMARKS.put(500L, "a typical computer science homework assignment");
        BENCHMARKS.put(2000L, "Apollo 11's Lunar Module code");
        BENCHMARKS.put(10000L, "the original UNIX v1 kernel");
        BENCHMARKS.put(50000L, "Space Shuttle's Primary Avionics System");
        BENCHMARKS.put(120000L, "Redis");
        BENCHMARKS.put(250000L, "React.js");
        BENCHMARKS.put(500000L, "SQLite");
        BENCHMARKS.put(1000000L, "Minecraft (Java Edition)");
        BENCHMARKS.put(3500000L, "the Curiosity Mars Rover");
        BENCHMARKS.put(15000000L, "the Android OS Kernel");
        BENCHMARKS.put(30000000L, "the Linux Kernel");
        BENCHMARKS.put(50000000L, "the Large Hadron Collider software");
        BENCHMARKS.put(100000000L, "a modern high-end car's software");
        BENCHMARKS.put(2000000000L, "Google's monolithic repository");
    }

    public static String getClosestBenchmark(ScanResult result) {
        long loc = result.totalCodeLines();
        Map.Entry<Long, String> lowerEntry = BENCHMARKS.floorEntry(loc);
        
        if (lowerEntry == null) {
            return "a blank slate";
        }
        
        return lowerEntry.getValue();
    }
}
