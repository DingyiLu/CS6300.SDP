package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoverageClassTestBC2 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% branch coverage of testCoverageMethod2() and
     *         reveal the fault therein.
     */

    // Purpose: Expose the true branch case for the "if" statement.
    //   This test avoids the fault.
    @Test
    public void testCoverageClassTestBC2Test1()  {

        int a = 5;
        int b = 5;
        double r = 2500.0;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod2(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }

    // Purpose: Expose the false branch case for the "if" statement.
    //   This test exposes the fault since the "bad" variable remains 0.
    @Test
    public void testCoverageClassTestBC2Test2()  {

        int a = -1;
        int b = -1;
        double r = 0.0;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod2(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }
}
