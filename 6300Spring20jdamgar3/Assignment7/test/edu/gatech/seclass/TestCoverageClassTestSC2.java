package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoverageClassTestSC2 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% statement coverage of testCoverageMethod2 and not
     *         reveal the fault therein.
     */

    // Purpose: A simple test case that provides 100% statement coverage by using
    //   a value of a>0. This will exercise all statements and avoid the fault.
    @Test
    public void testCoverageClassTestSC2Test1()  {

        int a = 2;
        int b = 2;
        double r = 400.0;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod2(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }

}
