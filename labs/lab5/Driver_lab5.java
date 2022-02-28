/**
 * file: Driver_lab5.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 5 - AES Basic Functions
 *
 * due date: March 6th, 2022
 * version: 1.0
 *
 */
import java.util.Scanner;

/**
 * Driver_lab5
 *
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
//    String plainText = "54776F204F6E65204E696E652054776F";
//    String keyHexText = "5468617473206D79204B756E67204675";
    String[] cTextHex = AESCipher.AES(plainText, keyHexText);
    System.out.println(cTextHex[0]);
  }
}

