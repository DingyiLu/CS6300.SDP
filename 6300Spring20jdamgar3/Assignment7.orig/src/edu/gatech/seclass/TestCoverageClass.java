package edu.gatech.seclass;


/**
 * This class represents an implementation of tasks specified for Assignment 7 of the Spring 2020
 * OMSCS 6300 Software Engineering course at Georgia Tech.
 *
 * @author James Damgar (jdamgar3@gatech.edu)
 */
public class TestCoverageClass {

    /**
     * Method that contains a division by zero fault such that:
     *   (1) It is possible to create a test suite with less than 100% statement coverage
     *       that reveals the fault, and
     *   (2) It is possible to create a test quite that achieves 100% path coverage and does
     *       not reveal the fault.
     */
    public double testCoverageMethod1(int a, int b) {

        // Division by 0 fault occurs when |a| == |b| and a,b are of opposite signs.
        double result = ( a * 1.0 ) / (a + b);
        if ( a > 0 ) {
            // Only executed if a > 0 in the test suite
            System.out.println("A: "+a+", B: "+b+", Result: "+result);
        }
        return result;
    }
}
