/**
 * file: Driver_lab4.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 4 - Generating Secure Keys
 *
 * due date: February 20th, 2022
 * version: 1.0
 *
 * This code takes input and calls aesRoundKeys() from
 * AESCipher to generate a secure AES key.
 */
import java.util.Scanner;

/**
 * Driver_lab4
 *
 * This class contains code to take in input and return an
 * array of Strings containing the encrypted keys.
 */
public class Driver_lab4 {

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
    String keyHex = input.nextLine();
    input.close();
    String[] roundKeysHex = AESCipher.AESRoundKeys(keyHex);
  }

}
