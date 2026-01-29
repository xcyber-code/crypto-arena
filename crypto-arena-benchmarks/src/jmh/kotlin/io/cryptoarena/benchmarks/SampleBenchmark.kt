package io.cryptoarena.benchmarks

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Sample JMH benchmark for crypto-arena.
 *
 * Run with: ./gradlew :crypto-arena-benchmarks:jmh
 */
@BenchmarkMode(Mode.Throughput, Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
open class SampleBenchmark {

    private lateinit var data: List<String>

    @Setup
    fun setup() {
        data = (1..1000).map { "item-$it" }
    }

    @Benchmark
    fun listIteration(): Int {
        var sum = 0
        for (item in data) {
            sum += item.length
        }
        return sum
    }

    @Benchmark
    fun streamSum(): Int {
        return data.sumOf { it.length }
    }

    @Benchmark
    fun parallelStreamSum(): Int {
        return data.parallelStream()
            .mapToInt { it.length }
            .sum()
    }
}
