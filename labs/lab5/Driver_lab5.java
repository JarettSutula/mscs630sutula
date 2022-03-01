/**
 * file: Driver_lab5.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 5 - AES Basic Functions
 *
 * due date: March 6th, 2022
 * version: 1.1
 *
 * This code takes an inputted plaintext and key string
 * and encrypts the plaintext using AES to generate secure
 * round keys and AES basic functions to encrypt the text.
 */
import java.util.Scanner;

/**
 * Driver_lab5
 *
 * This class contains code to take inputs from a file
 * and run AES functions on them.
 */
public class Driver_lab5 {

  /**
   * main
   *
   * This function gets the plaintext string from the input
   * and calls functions from AESCipher.java to encrypt them.
   *
   * @param args Array of Strings argument list to be
   *             passed in from the command line.
   */
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    String keyHexText = input.nextLine();
    String plainText = input.nextLine();
    input.close();
    String[] cTextHex = AESCipher.AES(plainText, keyHexText);
  }
}

