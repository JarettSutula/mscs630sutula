/**
 * file: Driver_lab3a.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 3 - Matrices in Modular Arithmetic
 * due date: February 6th, 2022
 * version: 1.4
 *
 * This file contains code to find the determinant of
 * an evenly-sized matrix.
 */

import java.util.Scanner;

/**
 * Driver_lab3a
 *
 * This class finds the determinant of an evenly-sized matrix
 * (1x1, 2x2, 3x3, ... etc).
 */
public class Driver_lab3a {
  /**
   * main
   *
   * This function takes input line by line from a file
   * and runs it through the cofactor modulo determinant function
   * cofModDet().
   * @param args Array of Strings argument list to be
   *             passed in from the command line.
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int modulo = input.nextInt();
    int matrixSize = input.nextInt();
    int[][] matrix = new int[matrixSize][matrixSize];
    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        matrix[i][j] = input.nextInt();
      }
    }
    input.close();
    int determinant = cofModDet(modulo, matrix);
    System.out.println(determinant);
  }

  /**
   * cofModDet
   *
   * This function takes in a modulo number and an evenly-sized matrix.
   * From
   * @param m: An Integer that represents the modulo number the calculations
   *         will be performed with.
   * @param A: An evenly-sized Integer matrix represented by a 2D Integer
   *         array.
   * @return the Integer determinant resulting from the calculations.
   */
  public static int cofModDet(int m, int[][] A) {
    int determinant = 0;
    int current_size = A.length;

    // if there is just one number, just get the determinant of that.
    if (current_size == 1) {
      determinant = A[0][0];
      determinant = negativeCheck(determinant, m);
      return determinant;
    }

    // if there are two numbers, use the A^-1 = (ab - cd) method if
    // [a  b]
    // [c  d]
    // Also need to use modulo on every integer calculation, as per
    // the lab pdf instructions.
    else if (current_size == 2) {
      determinant = (((A[0][0] % m) * (A[1][1] % m)) % m) -
                    (((A[0][1] % m) * (A[1][0] % m)) % m);
      determinant = negativeCheck(determinant, m);
      return determinant;
    }

    // we need to down-size the matrix.
    // a 3x3 matrix you have to downsize to 3 2x2s.
    // example input:
    // 9 3
    // [7 5 2]
    // [0 6 4]
    // [8 2 5]
    // you have to ignore the row and column for the first 3.
    // for 7... ignore row 0 and column 0. for 5... ignore row 0, column 1. etc
    // 7 * [6 4] -  5 * [0 4] +  2 * [0 6]
    //     [2 5]        [8 5],       [8 2]
    //
    else if (current_size > 2) {
      determinant = findDeterminant(A, current_size, m);
      determinant = negativeCheck(determinant, m);
      return determinant;
    }

    // just in case we get past here, return determinant
    return determinant;
  }

  /**
   * negativeCheck
   *
   * This function runs before returning the final determinant.
   * If the determinant is below 0, we need to get the first
   * positive integer. Do so by returning d % m + m.
   * @param d: An Integer that represents the Determinant.
   * @param modulo: An Integer that represents the modulo to be applied
   *              to the Determinant.
   * @return the resulting positive determinant % modulo Integer.
   */
  public static int negativeCheck(int d, int modulo) {
    if (d % modulo < 0) {
      return d % modulo + modulo;
    }
    // if we aren't negative, just return normally.
    return d % modulo;
  }

  /**
   * findDeterminant
   *
   * This function finds the determinant of 3x3 and larger matrices.
   * It works recursively by splitting larger matrices into manageable
   * smaller matrices.
   * @param matrix: The 2-D Integer array representing the Integer matrix.
   * @param size: The size of the matrix passed in.
   * @param modulo: The modulo to be performing Integer calculations with.
   * @return The determinant value of the modular matrix.
   */
  public static int findDeterminant(int[][] matrix, int size, int modulo) {
    int result = 0;

    // if we are down to 1...
    // just return the cofactor immediately.
    if (size == 1) {
      return matrix[0][0];
    }

    // initialize the new sub-matrix to be filled.
    int[][] tempMatrix = new int[matrix.length][matrix.length];

    // Loop through every integer in the first row
    for (int i = 0; i < size; i++) {
      // Find the cofactor of the original matrix [0][i]
      // aka, from previous 3x3 example, they will be 7, 5, and 2.
      // Do this to fill up the temporary matrix.

      // counters that serve as the indices for new matrix: j = row, k = col
      int j = 0;
      int k = 0;

      for(int row = 0; row < size; row++) {
        for(int column = 0; column < size; column++) {
          // only copy into temporary matrix if the row and column DO NOT
          // match the cofactor placement (matrix[0][i])!
          if (row != 0 && column != i) {
            tempMatrix[j][k++] = matrix[row][column];

            // increase row index and reset column index now that
            // the row of temp is filled.
            if (k == size - 1) {
              k = 0;
              j++;
            }
          }
        }
      }

      // Now, add the cofactors * their smaller matrices. Signs go +, -, +, ...
      // so flip sign for every i, starting at positive when i = 0.
      int sign = i % 2 == 0 ? 1:-1;
      // the resulting value should be the sign * cofactor * sub-matrix.
      result += sign *
        (matrix[0][i] % modulo) *
        (findDeterminant(tempMatrix, size - 1, modulo) % modulo);
    }

    // return the modular determinant.
    return result % modulo;
  }
}


