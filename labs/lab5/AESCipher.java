/**
 * file: AESCipher.java
 * author: Jarett Sutula
 * course: MSCS 630L
 * assignment: lab 5 - AES Basic functions
 * due date: March 6th, 2022
 * version: 1.2
 *
 * This file contains code to encrypt plaintext using AES
 * with a given plaintext string and key.
 */

/**
 * AESCipher
 *
 * This class contains the tables necessary for AES key encryption.
 * It also contains helper functions to populate the key matrix (k)
 * and the key-round holding matrix (w), as well as code to translate
 * integer values to hex strings and vice versa. It also contains code
 * for the 4 AES basic functions - Add Round Key, Nibble Substitution,
 * Shift Row, and Mix Columns.
 */
public class AESCipher {

  // initialize tables for calculations
  public static final int[][] S_BOX = {
    {0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76},
    {0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0},
    {0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15},
    {0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75},
    {0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84},
    {0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF},
    {0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8},
    {0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2},
    {0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73},
    {0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB},
    {0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79},
    {0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08},
    {0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A},
    {0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E},
    {0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF},
    {0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16}
  };

  public static final int[] RCON = {
    0x8D, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A,
    0x2F, 0x5E, 0xBC, 0x63, 0xC6, 0x97, 0x35, 0x6A, 0xD4, 0xB3, 0x7D, 0xFA, 0xEF, 0xC5, 0x91, 0x39,
    0x72, 0xE4, 0xD3, 0xBD, 0x61, 0xC2, 0x9F, 0x25, 0x4A, 0x94, 0x33, 0x66, 0xCC, 0x83, 0x1D, 0x3A,
    0x74, 0xE8, 0xCB, 0x8D, 0x01, 0x02, 0x14, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36, 0x6C, 0xD8,
    0xAB, 0x4D, 0x9A, 0x2F, 0x5E, 0xBC, 0x63, 0xC6, 0x97, 0x35, 0x6A, 0xD4, 0xB3, 0x7D, 0xFA, 0xEF,
    0xC5, 0x91, 0x39, 0x72, 0xE4, 0xD3, 0xBD, 0x61, 0xC2, 0x9F, 0x25, 0x4A, 0x94, 0x33, 0x66, 0xCC,
    0x83, 0x1D, 0x3A, 0x74, 0xE8, 0xCB, 0x8D, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B,
    0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A, 0x2F, 0x5E, 0xBC, 0x63, 0xC6, 0x97, 0x35, 0x6A, 0xD4, 0xB3,
    0x7D, 0xFA, 0xEF, 0xC5, 0x91, 0x39, 0x72, 0xE4, 0xD3, 0xBD, 0x61, 0xc2, 0x9F, 0x25, 0x4A, 0x94,
    0x33, 0x66, 0xCC, 0x83, 0x1D, 0x3A, 0x74, 0xE8, 0xCB, 0x8D, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
    0x40, 0x80, 0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A, 0x2F, 0x5E, 0xBC, 0x63, 0xC6, 0x97, 0x35,
    0x6A, 0xD4, 0xB3, 0x7D, 0xFA, 0xEF, 0xC5, 0x91, 0x39, 0x72, 0xE4, 0xD3, 0xBD, 0x61, 0xC2, 0x9F,
    0x25, 0x4A, 0x94, 0x33, 0x66, 0xCC, 0x83, 0x1D, 0x3A, 0x74, 0xE8, 0xCB, 0x8D, 0x01, 0x02, 0x04,
    0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A, 0x2F, 0x5E, 0xBC, 0x63,
    0xC6, 0x97, 0x35, 0x6A, 0xD4, 0xB3, 0x7D, 0xFA, 0xEF, 0xC5, 0x91, 0x39, 0x72, 0xE4, 0xD3, 0xBD,
    0x61, 0xC2, 0x9F, 0x25, 0x4A, 0x94, 0x33, 0x66, 0xCC, 0x83, 0x1D, 0x3A, 0x74, 0xE8, 0xCB, 0x8D
  };

  /**
   * aesRoundKeys
   *
   * This function takes the input key string and encrypts it. The input
   * is placed into a 4x4 matrix 'k', and then the corresponding encrypted
   * 4x44 matrix 'w' is filled. The ending result is that 'w' represents the
   * 11 keys strung together in 11 4x4 matrices that will be returned.
   * @param KeyHex: the 16-hex String representation of the system key
   * @return a 11-length array of Strings that represent the round keys
   */
  public static String[] AESRoundKeys(String KeyHex) {
    // take 32-char string and transform into 4x4, 2 hex digit matrix (k)
    int[][] k = new int[4][4];
    // implement the 4x44 matrix (w) that holds the 11 keys for output.
    int[][] w = new int[4][44];
    int keyIndex = 0;
    for (int i = 0; i < k.length; i++) {
      for (int j = 0; j < k.length; j++) {
        // for each value of k[][], grab the next 2 digits of the hex string.
        String two_digit_hex = "0x" + KeyHex.substring(keyIndex, keyIndex+2);
        k[j][i] = Integer.decode(two_digit_hex);
        // fill in the first 4 columns of w as well (first key)
        w[j][i] = k[j][i];
        keyIndex +=2;
      }
    }

    // Now populate the other 40 columns of W, starting at 4 and going to 44.
    for (int col = w.length; col < w[0].length; col++) {
      // if col != multiple of 4, XOR the fourth past and last column.
      if (col % 4 != 0) {
        int[] fourthPastColumn = getWColumn(w, col-4);
        int[] lastColumn = getWColumn(w, col-1);
        int[] replacementColumn = twoColumnXOR(fourthPastColumn, lastColumn);
        setWColumn(w, replacementColumn, col);

        // otherwise, we are starting a new round.
      } else {
        // get previous column and store in temporary vector.
        int[] tempVector = getWColumn(w, col - 1);
        // shift all elements to the left 1.
        int tempWValue = tempVector[0];
        for (int i = 0; i < tempVector.length - 1; i++) {
          tempVector[i] = tempVector[i + 1];
        }
        tempVector[3] = tempWValue;

        // transform each element in temp vector using S-BOX function.
        for (int i = 0; i < tempVector.length; i++) {
          tempVector[i] = aesSBox(tempVector[i]);
        }
        // Get the Rcon(i) constant for the round "i".
        int rconConstant = aesRcon(col / 4);

        // Perform XOR on said constant and the first element in the vector.
        int newFirstValue = (rconConstant ^ tempVector[0]);
        tempVector[0] = newFirstValue;

        // Finally, w(col) can be defined as:
        // w(col) = w(col - 4) XOR tempVector.
        int[] newColumn = twoColumnXOR(getWColumn(w, col - 4), tempVector);

        // set w(col) to the values in newColumn
        setWColumn(w, newColumn, col);
      }
    }

    // now that w is filled out correctly, place "w" into string array
    // breaking a new line every 'round' (every 4 columns)
    String[] result = new String[11];
    String line = "";
    int roundCounter = 0;
    for (int i = 0; i < w[0].length; i++) {
      for (int row = 0; row < w.length; row++) {
        String hexDigit = Integer.toHexString(w[row][i]).toUpperCase();
        // if hex string doesn't have padded 0, put it in.
        if (hexDigit.length() == 1) {
          String paddedHex = "0";
          hexDigit = paddedHex + hexDigit;
        }
        line += hexDigit;
        //System.out.print(hexDigit);
      }
      // if we are at the end of a round, put the line in the array
      // and reset the line and increment the counter.
      if ((i+1) % 4 == 0) {
        //System.out.println();
        result[roundCounter] = line;
        line = "";
        roundCounter++;
      }
    }
    return result;
  }

  /**
   * aesSBox
   *
   * This function takes a 2-digit hex value, splits it into two
   * separate values, and uses them as row/column indices to get
   * the corresponding 2-digit hex value from the SBOX table.
   *
   * @param inHex: the integer 2-digit hex value to use for indices
   * @return the resulting SBOX 2-digit integer value
   */
  public static int aesSBox(int inHex) {
    // split hex into two strings, convert those back to ints and get the
    // values from sbox
    String hex = Integer.toHexString(inHex);
    // Integer.toHexString turns "03" into just "3". If length == 1, pad 0.
    if (hex.length() == 1) {
      String paddedHex = "0" + hex;
      hex = paddedHex;
    }
    String hexRow = hex.substring(0,1);
    String hexColumn = hex.substring(1,2);
    int row = Integer.decode("0x" + hexRow);
    int column = Integer.decode("0x" + hexColumn);

    // for example, 8B --> row 8, column B
    return S_BOX[row][column];
  }

  /**
   * aesRcon
   *
   * This function gets a given rounds' constant from the RCON table.
   * @param round: the integer index to get from RCON
   * @return the resulting integer constant
   */
  public static int aesRcon(int round) {
    // retrieve the i-th round constant Rcon(i). In this case, "round" = i.
    return RCON[round];
  }

  /**
   * getWColumn
   *
   * This function takes the 'w' matrix and returns a column
   * given the index at which to get.
   * @param wMatrix: a 2D array of integers containing the
   *               key rounds for AES key generation.
   * @param column: the integer index to get the column from.
   * @return the vector of the column values from the matrix.
   */
  public static int[] getWColumn(int[][] wMatrix, int column) {
    int[] result = new int[4];
    for (int i = 0; i < 4; i++) {
      result[i] = wMatrix[i][column];
    }
    return result;
  }

  /**
   * setWColumn
   *
   * This function takes the 'w' matrix and sets values in a column
   * given the new column values and the index at which they will be placed.
   * @param wMatrix: a 2D array of integers to populate a column in.
   * @param newColumn: an array of integers to place into the matrix.
   * @param col: the integer index to place the new column into the matrix at.
   */
  public static void setWColumn(int[][] wMatrix, int[] newColumn, int col) {
    for (int i = 0; i < wMatrix.length; i++) {
      wMatrix[i][col] = newColumn[i];
    }
  }

  /**
   * twoColumnXOR
   *
   * This function takes two columns from w (in this case, vectors)
   * and runs bitwise XOR between all of their values. The resulting values
   * are stored in a temporary 'vector' and returned.
   * @param first: the first array of ints (column) to be XOR'd
   *             with the second array.
   * @param second: the second array of ints (column) to be XOR'd
   *              with the first array.
   * @return an array of ints that have the XOR results between the
   * every value of first and second.
   */
  public static int[] twoColumnXOR(int[] first, int[] second) {
    int[] temp = new int[first.length];
    for (int i = 0; i < temp.length; i++) {
      temp[i] = (first[i] ^ second[i]);
    }
    return temp;
  }

  /**
   * AESStateXOR
   *
   * This function returns a 4x4 matrix of pairs of hex digits that
   * represents the result of AES' "Add Round Key". The entries of
   * the output matrix are just the XOR results between the same
   * entries of the two input matrices.
   * @param sHex: the 4x4 int matrix of 'plaintext' in hex values
   * @param keyHex: the 4x4 int matrix representing a round key
   * @return the resulting 4x4 int matrix from 'Add Round Key' operation.
   */
  public static int[][] AESStateXOR(int[][] sHex, int[][] keyHex) {
    int[][] tempMatrix = new int[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        // take the resulting XOR values at [i][j] and set in output matrix
        tempMatrix[i][j] = sHex[i][j] ^ keyHex[i][j];
      }
    }
    return tempMatrix;
  }

  /**
   * AESNibbleSub
   *
   * This function takes a single input 4x4 matrix of ints and runs
   * the 'AES Nibble Substitution' operation on it, returning an equally
   * sized matrix using SBOX and its correlating function (aesSBox).
   * @param inStateHex: a 4x4 matrix of ints to be put through
   *                  Nibble Substitution
   * @return a 4x4 int matrix of the resulting SBOX values
   */
  public static int[][] AESNibbleSub(int[][] inStateHex) {
    int[][] tempMatrix = new int[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        tempMatrix[i][j] = aesSBox(inStateHex[i][j]);
      }
    }
    return tempMatrix;
  }

  /**
   * AESShiftRow
   *
   * This functions takes an input matrix and shifts the first row
   * 0 spaces to the left, the 2nd row 1 space to the left, the third
   * row 2 spaces to the left, and the 4th row 3 spaces to the left,
   * all according to AES' operation "Shift Rows".
   * @param inStateHex: a 4x4 matrix of ints to be put through Shift Rows
   * @return a 4x4 int matrix representing the shifted input matrix.
   */
  public static int[][] AESShiftRow(int[][] inStateHex) {
    // row 0 = 0, row 1 = 1 left, row 2 = 2 left, row 3 = 3 left
    int[][] tempMatrix = new int[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        // doing j + i mod 4 guarantees that we are shifting 1 extra value left
        // every time we increment i (the row)
        // this means for 0 (no change), for 1 (move all indices to the right 1
        // but last value (3 + 1) mod 4 = 0, which was the first value, etc.
        int newIndex = (j + i) % 4;
        tempMatrix[i][j] = inStateHex[i][newIndex];
      }
    }
    return tempMatrix;
  }

  /**
   * AESMixColumn
   *
   * This function takes an input matrix and performs the "Mix Columns"
   * AES operation. This is done by mapping each column of the input matrix
   * against a 4x4 galois field to calculate the column values of the output
   * matrix.
   * @param inStateHex: a 4x4 matrix of ints to be put through Mix Columns
   * @return a 4x4 int matrix representing the mixed column results
   */
  public static int[][] AESMixColumn(int[][] inStateHex) {
    int[][] tempMatrix = new int[4][4];
    int[][] a = new int[4][4];
    int[][] b = new int[4][4];

    // for each column of the input...
    // do matrix multiplication against galois field matrix.
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        // fill in copy array (a)
        a[j][i] = inStateHex[j][i];
        // right shift in either zeroes or ones
        int h = (inStateHex[j][i] >> 7) & 1;
        // remove high bit of b[j][i]!
        b[j][i] = inStateHex[j][i] << 1;
        // Galois field application
        b[j][i] ^= h * 0x1B;
        // need to mask any additional bits - java integers
        // work differently than C code in given article.
        // masking off by (11111111) ensures we only get last 8 bits.
        b[j][i] &= 0xFF;
      }

      // for each column, set their values according to galois.
      // Galois field
      // 2 3 1 1
      // 1 2 3 1
      // 1 1 2 3
      // 3 1 1 2
      tempMatrix[0][i] = b[0][i] ^ a[3][i] ^ a[2][i] ^ b[1][i] ^ a[1][i];
      tempMatrix[1][i] = b[1][i] ^ a[0][i] ^ a[3][i] ^ b[2][i] ^ a[2][i];
      tempMatrix[2][i] = b[2][i] ^ a[1][i] ^ a[0][i] ^ b[3][i] ^ a[3][i];
      tempMatrix[3][i] = b[3][i] ^ a[2][i] ^ a[1][i] ^ b[0][i] ^ a[0][i];
    }
    return tempMatrix;
  }

  /**
   * AES
   *
   * This function combines the independent AES function into a single function
   * that, given a plaintext string and key string, returns the AES encrypted
   * ciphertext.
   * @param pTextHex: the 16 digit hex plaintext as a String
   * @param keyHex: the 16 digit hex AES secure round key as a String
   * @return the 16 length String of the resulting encrypted ciphertext in
   * an array of length 1
   */
  public static String[] AES(String pTextHex, String keyHex) {
    // get the round keys from the keyHex string
    String[] roundKeys = AESRoundKeys(keyHex);
    // turn the pTextHex string into a 4x4 int[][].
    int[][] ptHex = changeToHex(pTextHex);

    // add key 0 before looping through AES steps.
    int[][] outHex = AESStateXOR(ptHex, changeToHex(roundKeys[0]));
    // for roundKeys[1] to roundKeys[9], follow this order -
    // nibble substitution -> shift rows -> mix columns -> add next key
    for (int i = 1; i < 10; i++) {
      outHex = AESNibbleSub(outHex);
      outHex = AESShiftRow(outHex);
      outHex = AESMixColumn(outHex);
      outHex = AESStateXOR(outHex, changeToHex(roundKeys[i]));
    }
    // for the final round, we follow this order -
    // nibble substitution -> shift rows -> add key
    outHex = AESNibbleSub(outHex);
    outHex = AESShiftRow(outHex);
    int[][] finalHex = AESStateXOR(outHex, changeToHex(roundKeys[10]));

    String result = "";
    // turn final hex matrix into a String of hex characters.
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        String hexDigit = Integer.toHexString(finalHex[j][i]).toUpperCase();
        if (hexDigit.length() == 1) hexDigit = "0" + hexDigit;
        result += hexDigit;
      }
    }

    // Unit tests want it back in an array.
    String[] arr = { result };
    System.out.println(arr[0]);
    return arr;
  }

  /**
   * changeToHex
   *
   * This function takes a 16-digit hex String and translates it into
   * the common 4x4 matrix used in AES basic functions.
   * @param hexString: the 16 digit hex String to be converted
   * @return a 4x4 matrix of ints representing the hex String
   */
  public static int[][] changeToHex(String hexString) {
    int[][] hex = new int[4][4];
    int keyIndex = 0;
    for (int i = 0; i < hex.length; i++) {
      for (int j = 0; j < hex.length; j++) {
        // for each value of outHex[][], grab the next 2 digits of the hex string.
        String two_digit_hex = "0x" + hexString.substring(keyIndex, keyIndex+2);
        hex[j][i] = Integer.decode(two_digit_hex);
        keyIndex +=2;
      }
    }
    return hex;
  }
}
