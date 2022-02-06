/**
 * file: Driver_lab3a.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 3 - Matrices in Modular Arithmetic
 * due date: February 6th, 2022
 * version: 1.1
 *
 * This file contains code to provide a padded matrix for
 * a given plaintext.
 */

import java.util.Scanner;

/**
 * Driver_lab3b
 *
 * This class takes a plaintext and substitution character
 * and places the plaintext in a 4x4 matrix from top-to-bottom.
 * Any additional empty spaces in the matrix are filled with the
 * provided substitution character.
 */
public class Driver_lab3b {

  /**
   * main
   *
   * This function will split the plaintext string into strings of length 16
   * and call getHexMatP() on them to get a matrix of integer values. Those
   * matrices are returned, swapped to Hex strings, and printed out accordingly.
   *
   * @param args Array of Strings argument list to be
   *             passed in from the command line.
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    char substitution = input.nextLine().charAt(0);
    String plaintext = input.nextLine();
    input.close();

    // Counter represents how many matrices we need to represent our plaintext.
    // If length is 31, = 1. 32, = 2. 33, = 3. Use integer division for floor.
    int counter = plaintext.length() / 16;
    // We need to add one since we are getting the floor... unless the length
    // is exactly divisible by 16 in which the floor is the correct amount.
    if (plaintext.length() % 16 != 0) counter++;

    // Keep track of what index to stop trying to grab substring at.
    int end_index_limit = plaintext.length();

    // For every matrix needed, partition the original plaintext and pass it
    // into getHexMatP.
    for (int i = 0; i < counter; i++) {
      // First matrix = plaintext[0] to [15]. substring needs (0, 16), etc.
      int start_index = i * 16;
      int end_index = (i + 1) * 16;

      // make sure to change end_index for substring if we are passing in
      // a string that has less than 16 length.
      if (end_index > end_index_limit) end_index = end_index_limit;

      // make a partitioned string from plaintext with specified indices.
      String partitionedP = plaintext.substring(start_index, end_index);
      // get the matrix's values from getHexMatP before printing out.
      int[][] matrix = getHexMatP(substitution, partitionedP);

      // for each value in the matrix, print out each. they are currently
      // integers but decimal - convert to hexString.
      for (int j = 0; j < 4; j++) {
        for (int k = 0; k < 4; k++) {
          // convert the integer decimal values into uppercase hex strings.
          System.out.print(Integer.toHexString(matrix[j][k]).toUpperCase());
          // if we aren't at the end of row, print a space between values.
          if (k != 3) {
            System.out.print(" ");
          } else {
            System.out.println();
          }
        }
      }
      // Now that the matrix is printed out, if we still have more matrices to
      // print out, print out a full line between them like the examples.
      if (i + 1 != counter) System.out.println();
    }
  }

  /**
   * getHexMatP
   *
   * This function takes a plaintext string of length <= 16 and places it
   * top-to-bottom in a 4x4 matrix, filling in any empty spaces with the
   * provided substitution character.
   *
   * @param s: A char that will substitute for any empty space in the plaintext
   * @param p: A plaintext String with length <= 16 to be placed into a
   *         2D matrix of Integers.
   * @return a 2D array of Integers decimal values that represent the plaintext
   */
  public static int[][] getHexMatP(char s, String p) {
    // keep track of where to stop if p's length is > 16.
    int end_index = p.length()-1;
    // update every increment to use charAt nicely.
    int counter = 0;
    int[][] tempMatrix = new int[4][4];

    // instead of matrix[i][j] order, we can use matrix[j][i] order.
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        // if we aren't at the end of the string p, get the charAt that counter
        // and convert it into an integer.
        if (counter <= end_index) {
          tempMatrix[j][i] = (int) p.charAt(counter);
        } else {
          // otherwise, if we are past the string p's length, just set that
          // value to the substitution char (as an int)
          tempMatrix[j][i] = (int) s;
        }
        counter++;
      }
    }
    return tempMatrix;
  }
}
