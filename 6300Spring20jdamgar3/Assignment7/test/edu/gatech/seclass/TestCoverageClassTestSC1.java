package edu.gatech.seclass;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCoverageClassTestSC1 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve less than 100% statement coverage while exposing the fault.
     */

    // Purpose: A simple test case that exposes the testCoverageMethod1()'s
    //   divide-by-0 fault while not achieving 100% statement (line) coverage.
    //   Don't enter the first "if" statement since a<=0 but hit the fault
    //   since a == -1 * b.
    @Test
    public void testCoverageClassTestSC1Test1()  {

        int a = -10;
        int b = 10;
        double r = -0.5;

        tcc = new TestCoverageClass();
        double result = tcc.testCoverageMethod1(a, b);
        assertEquals("Unexpected calculation result!", r, result, 0.0);
    }

}
