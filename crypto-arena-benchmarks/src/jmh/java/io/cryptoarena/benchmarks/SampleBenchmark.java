package io.cryptoarena.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sample JMH benchmark for crypto-arena.
 * <p>
 * Run with: ./gradlew :crypto-arena-benchmarks:jmh
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
public class SampleBenchmark {

    private List<String> data;

    @Setup
    public void setup() {
        data = new ArrayList<>(1000);
        for (int i = 1; i <= 1000; i++) {
            data.add("item-" + i);
        }
    }

    @Benchmark
    public int listIteration() {
        int sum = 0;
        for (String item : data) {
            sum += item.length();
        }
        return sum;
    }

    @Benchmark
    public int streamSum() {
        return data.stream()
            .mapToInt(String::length)
            .sum();
    }

    @Benchmark
    public int parallelStreamSum() {
        return data.parallelStream()
            .mapToInt(String::length)
            .sum();
    }
}
