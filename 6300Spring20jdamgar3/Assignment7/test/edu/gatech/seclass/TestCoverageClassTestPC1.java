package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCoverageClassTestPC1 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% path coverage of testCoverageMethod1 while not
     *         exposing the fault.
     */

    // Purpose: A simple test case that down not expose the testCoverageMethod1()'s
    //   divide-by-0 fault while entering the first "if" statement path since
    //   a>0.
    @Test
    public void testCoverageClassTestPC1Test1()  {

        int a = 10;
        int b = 10;
        double r = 0.5;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod1(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }

    // Purpose: A simple test case that down not expose the testCoverageMethod1()'s
    //   divide-by-0 fault while entering the first "else" statement path since
    //   a<=0.
    @Test
    public void testCoverageClassTestPC1Test2()  {

        int a = -10;
        int b = -10;
        double r = 0.5;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod1(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }
}
