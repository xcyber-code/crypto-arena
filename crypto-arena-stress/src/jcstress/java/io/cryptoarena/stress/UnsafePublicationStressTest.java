package io.cryptoarena.stress;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

/**
 * Example of a data race detection test.
 * This demonstrates how JCStress can detect unsafe publication.
 */
@JCStressTest
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "Both see default values")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Both see initialized values")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Partial publication - field order visible")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Partial publication - field order visible")
@State
public class UnsafePublicationStressTest {

    int x;
    int y;

    @Actor
    public void writer() {
        x = 1;
        y = 1;
    }

    @Actor
    public void reader(II_Result r) {
        r.r1 = y;
        r.r2 = x;
    }
}
