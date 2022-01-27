/**
 * file: Driver_lab2b.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 2 - Divisibility and Modular Arithmetic
 * due date: January 30th, 2022
 * version: 1.0
 *
 * This file contains code to run the Extended Euclidean Algorithm.
 */

import java.util.Scanner;

/**
 * Driver_lab2a
 *
 * This class employs the Extended Euclidean Algorithm to find
 * the greatest common divisor between two integers as well as
 */
public class Driver_lab2b {
  /**
   * main
   *
   * This function takes input line by line from a file
   * and runs it through extendedEuclidAlg().
   * @param args Array of Strings argument list to be
   *            passed in from the command line.
   */
  public static void main(String[] args) {
    Scanner input = new Scanner (System.in);
    while(input.hasNextLine()) {
      long firstValue = input.nextLong();
      long secondValue = input.nextLong();

      if (firstValue >= secondValue) {
        euclidAlgExt(firstValue, secondValue);
      } else {
        euclidAlgExt(secondValue, firstValue);
      }
    }
    input.close();
  }

  /**
   * euclidAlgExt
   *
   * This function takes in two longs and uses the extended
   * Euclidean Algorithm to find the greatest common divisor
   * between them. It continuously divides the remainders into
   * previous divisors until the remainder is 0, all while keeping
   * track of the quotient (q), the two values a and b, the remainder,
   * and the values of x (=x1 -x2 * q) and y (=y1 - y2 * q).
   *
   * @param a: A long that is of higher or equal value to b.
   * @param b: A long that is of lower or equal value to a.
   * @return the Array of longs that contains the greatest common divisor (d),
   *         x, and y in the order [d, x, y] in reference to d = ax + by.
   */
  public static long[] euclidAlgExt(long a, long b) {
    // An example of what gcd(148, 75) looks like d = ax + by
    //  q   a    b    r   x1   x2   x    y1   y2   y
    //  1   148  75   73  1    0    1    0    1    -1
    //  1   75   73   2   0    1    -1   1    -1   2
    //  36  73   2    1   1    -1   37   -1   2    -73
    //  2   2    1    0   -1   37   73   2    -73  148
    //  -   1    0    -   37   73   -    -73  148  -
    // The final answer is d = 1, x = the most recent x1 (37) and y
    // y = the most recent y1 (-73). 1 = 148(37) + 75(-73).

    // initialize the variables of the table for first loop.
    long q = a / b;
    long r = a % b;
    // x1, x2, y1, y2 are just temporary placeholders for x/y values.
    // See table for example.
    long x1 = 1;
    long x2 = 0;
    long x = x1 - x2*q;
    long y1 = 0;
    long y2 = 1;
    long y = y1 - y2*q;

    System.out.printf("%-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s\n",
      "q", "a", "b", "r", "x1", "x2", "x", "y1", "y2", "y");
    // for each loop after, as long as we haven't hit b = 0...
    // a = previous b, b = previous r, q = a/b, r = a % b
    // x1 = previous x2, x2 = previous x, x = x1 - x2*q,
    // y1 = previous y2, y2 = previous y, y = y1 - y2*q.
    while (b!=0) {
      System.out.printf("%-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d\n",
        q, a, b, r, x1, x2, x, y1, y2, y);
      a = b;
      b = r;
      // make sure updated b != 0. If it is, q = a / b will cause an error.
      if (b!=0) {
        // do standard loop procedure
        q = a / b;
        r = a % b;
        x1 = x2;
        x2 = x;
        x = x1 - x2 * q;
        y1 = y2;
        y2 = y;
        y = y1 - y2 * q;
      } else {
        // if b is 0 now, just update the values that matter (a, b, x1, y1).
        x1 = x2;
        y1 = y2;
        System.out.printf("%-5s %-5d %-5d %-5s %-5d %-5s %-5s %-5d %-5s %-5s\n",
          "-", a, b, "-", x1, "-", "-", y1, "-", "-");
      }
    }

    System.out.println(a + " " + x1 + " " + y1);
    return new long[] {a, x1, y1};
  }

}
