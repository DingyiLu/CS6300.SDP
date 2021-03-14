package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoverageClassTestSC3 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% statement coverage of testCoverageMethod3() and not
     *         reveal the fault therein.
     */

    // Purpose: Expose every statement in testCoverageMethod3(), including the first
    //   "if" block, while not revealing the fault.
    @Test
    public void testCoverageClassTestSC3Test1()  {

        int a = 10;
        int b = 10;
        double r = 10.0;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod3(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }
}
