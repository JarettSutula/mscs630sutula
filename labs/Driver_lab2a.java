/**
 * file: Driver_lab2a.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 2 - Divisibility and Modular Arithmetic
 * due date: January 30th, 2022
 * version: 1.0
 *
 * This file contains code to run the Euclidean Algorithm.
 */

import java.util.Scanner;

/**
 * Driver_lab2a
 *
 * This class employs the Euclidean Algorithm to find
 * the greatest common divisor between two integers.
 */
public class Driver_lab2a {
	/**
	 * main
	 *
	 * This function takes input line by line from a file
	 * and runs it through euclidAlg().
	 * @param args: Array of Strings argument list to be
   *            passed in from the command line.
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner (System.in);
		while(input.hasNextLine()) {
		  long firstValue = input.nextLong();
		  long secondValue = input.nextLong();

		  if (firstValue >= secondValue) {
		  	euclidAlg(firstValue, secondValue);
		  } else {
		  	euclidAlg(secondValue, firstValue);
		  }
		}
		input.close();
	}

  /**
   * euclidAlg
   *
   * This function takes in two longs and uses the
   * Euclidean Algorithm to find the greatest common divisor
   * between them. It continuously divides the remainders into
   * previous divisors until the remainder is 0.
   *
   * @param a: A long that is of higher or equal value to b.
   * @param b: A long that is of lower or equal value to a.
   * @return either the most recent set of a,b to put in the
   *          Euclidean Algorithm recursively or the greatest
   *          common divisor
   */
  public static long euclidAlg(long a, long b) {
    if (b == 0) {
      System.out.println(a);
      return a;
    }
    return euclidAlg(b, a % b);
  }
}
