package edu.gatech.seclass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCoverageClassTestSC5 {

    private TestCoverageClass tcc;

    /*
     *   TEST CASES
     *
     *   Goal: Achieve 100% statement coverage of testCoverageMethod3() and not
     *         reveal the fault therein.
     */

    @Test
    public void testCoverageClassTestSC5Test1()  {
        tcc = new TestCoverageClass();
        boolean result = tcc.testCoverageMethod5(true, true);
        System.out.println(result);
        assertTrue(result);
    }

    @Test
    public void testCoverageClassTestSC5Test2()  {
        tcc = new TestCoverageClass();
        boolean result = tcc.testCoverageMethod5(true, false);
        System.out.println(result);
        assertTrue(result);
    }

    @Test
    public void testCoverageClassTestSC5Test3()  {
        tcc = new TestCoverageClass();
        boolean result = tcc.testCoverageMethod5(false, true);
        System.out.println(result);
        assertTrue(result);
    }

    @Test
    public void testCoverageClassTestSC5Test4()  {
        tcc = new TestCoverageClass();
        boolean result = tcc.testCoverageMethod5(false, false);
        System.out.println(result);
        assertTrue(result);
    }
}
