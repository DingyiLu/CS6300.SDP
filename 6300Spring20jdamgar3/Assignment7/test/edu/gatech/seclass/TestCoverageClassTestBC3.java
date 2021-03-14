package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoverageClassTestBC3 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% branch coverage of testCoverageMethod3() while
     *         exposing the fault.
     */

    // Purpose: A simple test case that exposes the testCoverageMethod3()'s
    //   divide-by-0 fault and contains 100% branch coverage since there
    //   are no branches.
    @Test
    public void testCoverageClassTestBC3Test1()  {

        int a = 10;
        int b = 0;
        double r = 0.0;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod3(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }
}
