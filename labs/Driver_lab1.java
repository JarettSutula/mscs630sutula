/**
 * file: Driver_lab1.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 1 - intro to cryptography
 * due date: January 23rd, 2022
 * version: 1.2
 *
 * This file contains code to 'encrypt' a plaintext message into
 * an array of integers.
 */

import java.util.Scanner;
import java.util.HashMap;

/**
 * Driver_lab1
 *
 * This class does the 'encryption' of our plaintext.
 * It contains a function that transforms the text (String)
 * to an array of Integers.
 */
public class Driver_lab1 {
  /**
   * main
   *
   * This function takes input line by line from
   * a file and runs it through the 'str2int'
   * function as per lab instructions.
   * @param args: Array of Strings argument list to be
   *            passed in from the command line.
   */
  public static void main(String[] args) {
    Scanner input = new Scanner (System.in);
    while (input.hasNextLine()) {
      str2int(input.nextLine());
    }
    input.close();
  }

  /**
   * str2int
   *
   * This function is given a plaintext string and converts
   * it into an array of Integers.
   * A-Z = 0-25, and a space is mapped to 26.
   * The same applies to lowercase letters.
   * a = 0, A = 0, ... z = 25, Z = 25.
   *
   * @param plainText: the input String to be encrypted
   * @return encryptedArray: the resulting array of Integers which
   * has had the encryption applied.
   */
  public static int[] str2int(String plainText) {
    int [] encryptedArray = new int[plainText.length()];
    int letterIndex = 0;
    int SPACE_ALPHABET_MAPPING_VALUE = 26;
    // Create an 'alphabet' and populate it with the mappings of 0-26.
    HashMap<Character, Integer> alphabet = new HashMap<>();

    // Starting from 0 and 'a', go through and add each lowercase and uppercase
    // letter to the hashmap so it can be searched later.
    for (char letter = 'a'; letter <= 'z'; letter++) {
      alphabet.put(letter, letterIndex);
      alphabet.put(Character.toUpperCase(letter), letterIndex);
      letterIndex++;
    }

    // Space has to be manually mapped to 26.
    alphabet.put(' ', SPACE_ALPHABET_MAPPING_VALUE);

    // Loop through the plaintext and add the correct integer to the array.
    for (int i = 0; i < plainText.length(); i++) {
      int encrypted_value = alphabet.get(plainText.charAt(i));
      encryptedArray[i] = encrypted_value;
    }

    // Place the integer array into a string with no "[", "]", or ",".
    StringBuilder result = new StringBuilder();
    for (int j = 0; j < encryptedArray.length; j++) {
      result.append(encryptedArray[j]);
      // Only print a space after the number if we are not at the end of a loop
      if (j != encryptedArray.length-1) {
        result.append(" ");
      }
    }

    // Print out the result. Not necessary for test cases but helpful.
    System.out.println(result);
    return encryptedArray;
  }
}