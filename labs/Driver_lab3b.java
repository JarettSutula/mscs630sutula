/**
 * file: Driver_lab3a.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 3 - Matrices in Modular Arithmetic
 * due date: February 6th, 2022
 * version: 1.0
 *
 * This file contains code to provide a padded matrix for
 * a given plaintext.
 */

import java.util.Scanner;

public class Driver_lab3b {

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    char substitution = input.nextLine().charAt(0);
    String plaintext = input.nextLine();

    input.close();
    System.out.println(plaintext.length());

    int counter = plaintext.length() / 16;
    if (plaintext.length() % 16 != 0) counter++;

    int end_index = plaintext.length() - 1;

    for (int i = 0; i < counter; i++) {
      String partitionedP = plaintext.substring(i * 16);
    }



    // add substitution characters to the appropriate string.

  }

  public static int[][] getHexMatP(char s, String p) {
    return new int [0][0];
  }
}
