package edu.gatech.seclass;


/**
 * This class represents an implementation of tasks specified for Assignment 7 of the Spring 2020
 * OMSCS 6300 Software Engineering course at Georgia Tech.
 *
 * @author James Damgar (jdamgar3@gatech.edu)
 */
public class TestCoverageClass {

    /**
     * Task 1
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
            System.out.println("a is positive!");
        }
        else {
            System.out.println("a is non-positive!");
        }
        return result;
    }

    /**
     * Task 2
     * Method that contains a division by zero fault such that:
     *   (1) It is possible to create a test suite that achieves 100% statement coverage
     *       and does not reveal the fault and
     *   (2) Every test suite that achieves 100% branch coverage reveals the fault.
     */
    public double testCoverageMethod2(int a, int b) {

        // Division by 0 fault occurs when a<=0.
        // Any test suite with 100% branch coverage must have a test case that
        // exposes the false branch of the "if" statement, meaning that "bad"
        // will remain 0 and expose that fault in that case (and suite).
        int bad = 0;
        if(a > 0) {
            bad += 1;
        }
        return (100.0 * a * b) / bad;
    }

    /**
     * Task 3
     * Method that contains a division by zero fault such that:
     *   (1) Every test suite that reveals the fault has 100% branch coverage, and
     *   (2) It is possible to create a test suite that achieves 100% statement
     *       coverage and does not reveal the fault.
     *
     * Criteria (1) implies that a test suite must have 100% branch coverage in
     * order to reveal the fault. If a method has no conditional statements, then
     * it has no other "branches" per-se besides 1. This implies that any test suite must
     * implicitly have 100% branch coverage and so any that exposes the fault
     * will also have 100% branch coverage.
     */
    public double testCoverageMethod3(int a, int b) {

        // Division by 0 fault occurs when b==0
        double result = (a * 10) / b;
        System.out.println("A: "+a+", B: "+b+", Result: "+result);
        return result;
    }

    /**
     * Task 4
     * Method that contains a division by zero fault such that:
     *   (1) It is possible to create a test suite that achieves 100% branch coverage
     *       and does not reveal the fault, and
     *   (2) Every test suite that achieves 100% statement coverage reveals the fault.
     *
     */
    public void testCoverageMethod4(int a, int b) {
        /**
         * It is not possible to create a method which satisfies both of the criteria above.
         * Criteria (2) implies that *any* test suite with 100% statement coverage will
         * reveal the fault. Any test suite that possesses 100% branch coverage will
         * also possess 100% statement coverage since branch coverage subsumes statement
         * coverage. This means that any test suite with 100% branch coverage will expose
         * the fault. So it is impossible to satisfy criteria (1).
         */
    }

    /**
     * Task 5
     */
    public boolean testCoverageMethod5 (boolean a, boolean b) {
        int x = 3;
        int y = 1;
        if(a)
            x += y;
        else
            y = y*x;
        if(b)
            y -= x;
        else
            y -= 1;
        return ((x/y)>= 0);
    }
    // ================
    //
    // Fill in column “output” with T, F, or E:
    //
    // | a | b |output|
    // ================
    // | T | T |  F   |
    // | T | F |  E   |
    // | F | T |  E   |
    // | F | F |  T   |a
    // ================
    //
    // Fill in the blanks in the following sentences with
    // “NEVER”, “SOMETIMES” or “ALWAYS”:
    //
    // Test suites with 100% statement coverage SOMETIMES reveal the fault in this method.
    // Test suites with 100% branch coverage SOMETIMES reveal the fault in this method.
    // Test suites with 100% path coverage ALWAYS reveal the fault in this method.
    // ================
}
