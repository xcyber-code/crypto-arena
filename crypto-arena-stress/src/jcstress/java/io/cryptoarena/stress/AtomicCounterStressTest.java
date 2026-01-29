package io.cryptoarena.stress;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample JCStress test for crypto-arena.
 *
 * Run with: ./gradlew :crypto-arena-stress:jcstress
 * Quick run: ./gradlew :crypto-arena-stress:jcstressQuick
 */
@JCStressTest
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Both increments visible")
@Outcome(id = "1, 2", expect = Expect.ACCEPTABLE, desc = "T1 before T2")
@Outcome(id = "2, 1", expect = Expect.ACCEPTABLE, desc = "T2 before T1")
@Outcome(id = "2, 2", expect = Expect.ACCEPTABLE, desc = "Both see final value")
@State
public class AtomicCounterStressTest {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Actor
    public void actor1(II_Result r) {
        counter.incrementAndGet();
        r.r1 = counter.get();
    }

    @Actor
    public void actor2(II_Result r) {
        counter.incrementAndGet();
        r.r2 = counter.get();
    }
}
