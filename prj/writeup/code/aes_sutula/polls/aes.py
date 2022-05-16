# AES Encryption and Decryption of plaintext
# author: Jarett Sutula
# version: 1.13

from .constants import *

def matrix_to_hex_string(matrix):
    """Returns a 4x4 matrix of hex digits as a single string."""
    # create a temp list to store 4x4 matrices
    # this works both for the standard 4x4 but also the 4x44 key set.
    temp = ["" for i in range(len(matrix[0]) // 4)]
    # if there is more than one 4x4 matrix in our input, keep track of index.
    for key_index in range(len(matrix[0]) // 4):
        key = ""
        for i in range(len(matrix)):
            for j in range(len(matrix)):
                # get value at j, i of the matrix, ignore '0x', and fill in zero if needed.
                key += (hex(matrix[j][i+(key_index*4)])[2:].zfill(2))
        # at the end of each key, uppercase the hex letters for visibility.
        temp[key_index] = key.upper()
    
    return temp

def round_keys(key_hex):
    """Given a 32 char key string, generate the 11 round keys for AES encryption."""
    # turn 32-char string key_hex into 4x4 2 digit hex matrix k
    k = [[0 for i in range(4)] for j in range(4)]
    # 4x44 matrix to hold the 11 keys
    w = [[0 for i in range(44)] for j in range(4)]
    key_index = 0

    # fill in k and w with given key hex string
    for i in range(4):
        for j in range(4):
            two_digit_hex = key_hex[0][key_index:key_index+2]
            k[j][i] = int(two_digit_hex, 16)
            w[j][i] = k[j][i]
            # move forward two spots to next hex-pair.
            key_index +=2

    # fill in w columns 3-43.
    for col in range(len(w), len(w[0])):
        # print(col)
        if col % 4 != 0:
            # XOR the fourth past col and the last col
            fourth_past = get_w_col(w, col-4)
            last = get_w_col(w, col-1)
            replacement_col = two_col_xor(fourth_past, last)
            # place the XOR result into the current 'col' of w
            set_w_col(w, replacement_col, col)

        else:
            # we need to start a new round
            # shift all elements to the left 1
            temp = get_w_col(w, col-1)
            temp_w_value = temp[0]
            for i in range(len(temp)-1):
                temp[i] = temp[i+1]
            temp[-1] = temp_w_value

            # transform each value with sbox
            for i in range(len(temp)):
                temp[i] = aes_sbox(temp[i])
            
            # get rcon constant
            r_con_value = aes_rcon(col // 4)

            # perform XOR on constant and first element in temp
            temp_w_value = (r_con_value ^ temp[0])
            temp[0] = temp_w_value

            # column now XORed with fourth past column
            fourth_past_col = get_w_col(w, col - 4)
            new_col = two_col_xor(fourth_past_col, temp)

            # set newly created col
            set_w_col(w, new_col, col)

    result = matrix_to_hex_string(w)
    return result


def aes_sbox(hex_value):
    """Take 2-digit hex value, split it, and search SBOX for corresponding value."""
    our_hex = hex(hex_value)[2:].zfill(2)
    row = our_hex[0:1]
    col = our_hex[1:2]
    return s_box[int(row, 16)][int(col, 16)]

def aes_inv_sbox(hex_value):
    """Take 2-digit hex value, split it, and use inverse SBOX for value."""
    our_hex = hex(hex_value)[2:].zfill(2)
    row = our_hex[0:1]
    col = our_hex[1:2]
    return inv_s_box[int(row, 16)][int(col, 16)]

def aes_rcon(round):
    """Return a constant from a given round using RCON table."""
    return rcon[round]

def get_w_col(matrix, col):
    """Get a column from a given matrix to be used by AES functions."""
    result = [0 for i in range(4)]
    for i in range(4):
        result[i] = matrix[i][col]
    return result

def set_w_col(matrix, new_col, col):
    """Replace a given column of a matrix with a specified new column."""
    for i in range(4):
        matrix[i][col] = new_col[i]

def two_col_xor(first_col, second_col):
    """Takes two columns and returns the XOR values between them."""
    temp = [0 for i in range(len(first_col))]
    for i in range(len(temp)):
        temp[i] = (first_col[i]) ^ (second_col[i])
    return temp
    
def aes_encryption(p_text_hex, key_hex):
    """
    Takes a given plaintext hex strings and key hex string and encrypts 
    the plaintext using the AES functions nibble substitution, add round key,
    mix columns, and shift row.
    """
    # list of hex strings to return at the end.
    temp = ["" for i in range(len(p_text_hex))]

    # for each hex string, run AES encryption
    for i in range(len(p_text_hex)):
        aes_round_keys = round_keys(key_hex)
        pt_hex = change_to_hex_matrix(p_text_hex[i])
        # have to use add round key for the first key before we start the loop.
        out_hex = aes_state_xor(pt_hex, change_to_hex_matrix(aes_round_keys[0]))

        # for keys 1-9, do sub -> shift -> mix -> add key
        for j in range(1, 10):
            out_hex = aes_nibble_sub(out_hex)
            out_hex = aes_shift_row(out_hex)
            out_hex = aes_mix_column(out_hex)
            out_hex = aes_state_xor(out_hex, change_to_hex_matrix(aes_round_keys[j]))
        
        # for the final key, we don't mix columns.
        out_hex = aes_nibble_sub(out_hex)
        out_hex = aes_shift_row(out_hex)
        final = aes_state_xor(out_hex, change_to_hex_matrix(aes_round_keys[10]))

        # use matrix_to_hex_string to generate the hex string to return.
        final_hex = matrix_to_hex_string(final)
        temp[i] = final_hex[0]
        
    return temp

def aes_decryption(c_text_hex, key_hex):
    """
    Given ciphertext hex strings and the key hex they were encrypted with,
    use inverse AES functions to decrypt the message.
    """
    temp = ["" for i in range(len(c_text_hex))]
    # apply decryption to all of the strings in cipher text
    for i in range(len(c_text_hex)):
        # generate round keys
        aes_round_keys = round_keys(key_hex)
        # change current ciphertext string into hex matrix
        ct_hex = change_to_hex_matrix(c_text_hex[i])
        # decrypt with the last key before going through the rest (no mix columun)
        out_hex = aes_state_xor(ct_hex, change_to_hex_matrix(aes_round_keys[10]))
        out_hex = aes_inv_shift_row(out_hex)
        out_hex = aes_inv_nibble_sub(out_hex)

        # for the rest of the rounds, use
        # inverse functions: add round key -> mix column -> shift row -> nibble sub
        for j in range(9, 0, -1):
            out_hex = aes_state_xor(out_hex, change_to_hex_matrix(aes_round_keys[j]))
            out_hex = aes_inv_mix_column(out_hex)
            out_hex = aes_inv_shift_row(out_hex)
            out_hex = aes_inv_nibble_sub(out_hex)
        
        # final XOR with first round key.
        out_hex = aes_state_xor(out_hex, change_to_hex_matrix(aes_round_keys[0]))
        # change hex matrix to hex string
        final_hex = matrix_to_hex_string(out_hex)
        temp[i] = final_hex[0]

    return temp


def aes_state_xor(pt_hex, key_hex):
    """
    Get the XOR values between the matrices of the plaintext and the key.
    This works for both encryption and decryption as the inverse of this
    gives the same result (as a product of XOR's properties).
    """
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            temp[i][j] = pt_hex[i][j] ^ key_hex[i][j]
    return temp

def aes_nibble_sub(in_state_hex):
    """
    Copy the values of a given array with their SBOX table equivalents
    and return the temporary matrix with the new values.
    """
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            temp[i][j] = aes_sbox(in_state_hex[i][j])
    return temp

def aes_inv_nibble_sub(in_state_hex):
    """
    Copy the values of a given array with their inverse SBOX table 
    equivalents and return the temporary matrix with the new values.
    """
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            temp[i][j] = aes_inv_sbox(in_state_hex[i][j])
    return temp

def aes_shift_row(in_state_hex):
    """Shift first row 0, 2nd row 1, 3rd row 2, 4th row 3 places to the left."""
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            new_index = (j + i) % 4
            temp[i][j] = in_state_hex[i][new_index]
    return temp

def aes_inv_shift_row(in_state_hex):
    """Shift first row 0, 2nd row 1, 3rd row 2, 4th row 3 places to the right."""
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            new_index = (j - i) % 4
            temp[i][j] = in_state_hex[i][new_index]
    return temp

def aes_mix_column(in_state_hex):
    """Perform mix column operations on the 4x4 matrix using the galois field."""
    temp = [[0 for i in range(4)] for j in range(4)]
    a = [[0 for i in range(4)] for j in range(4)]
    b = [[0 for i in range(4)] for j in range(4)]

    # for each column of the input, multiply against matrix
    for i in range(len(temp)):
        for j in range(len(temp)):
            # copy array a
            a[j][i] = in_state_hex[j][i]
            # right shift in either zeroes or ones
            h = (in_state_hex[j][i] >> 7) & 1
            # remove high bit of b[j][i]
            b[j][i] = in_state_hex[j][i] << 1
            # apply galois field
            b[j][i] ^= h * 0x1B
            # mask any additional bits by masking (11111111)
            b[j][i] &= 0xFF
        # galois field multiplication according to wikipedia
        temp[0][i] = b[0][i] ^ a[3][i] ^ a[2][i] ^ b[1][i] ^ a[1][i]
        temp[1][i] = b[1][i] ^ a[0][i] ^ a[3][i] ^ b[2][i] ^ a[2][i]
        temp[2][i] = b[2][i] ^ a[1][i] ^ a[0][i] ^ b[3][i] ^ a[3][i]
        temp[3][i] = b[3][i] ^ a[2][i] ^ a[1][i] ^ b[0][i] ^ a[0][i]
    return temp

def aes_inv_mix_column(in_state_hex):
    """
    Perform inverse mix column operations on 4x4 matrix using inverse galois field.
    This uses the galois field and specific lookup tables for 9, 11, 13, 14 since
    they are significantly more complicated to many multiples of 1, 2, 3 like 
    the normal (non-inverse) galois multiplication in aes_mix_column().
    """
    temp = [[0 for i in range(4)] for j in range(4)]
    for i in range(len(temp)):
        for j in range(len(temp)):
            # keep track of this byte
            value = 0
            for k in range(len(temp)):
                # look up the inverse galois field
                # 14 11 13 9
                # 9 14 11 13
                # 13 9 14 11
                # 11 13 9 14
                galois_value = inv_galois[i][k]
                state = in_state_hex[k][j]

                # run mini-switch that uses the bitwise XOR on
                # the appropriate galois lookup table (for 9, 11, 13, or 14)
                if galois_value == 1:
                    value = value ^ state
                elif galois_value == 9:
                    value = value ^ galois_9[state // 16][state % 16]
                elif galois_value == 11:
                    value = value ^ galois_11[state // 16][state % 16]
                elif galois_value == 13:
                    value = value ^ galois_13[state // 16][state % 16]
                elif galois_value == 14:
                    value = value ^ galois_14[state // 16][state % 16]
                else:
                    value ^= 0
            # set the value in the temp list after full galois multiplication
            temp[i][j] = value
    return temp

def change_to_hex_matrix(hex_string):
    """
    Given a hex string, take pairs of two hex digits, translate them to an integer,
    then place them in the 4x4 hex matrix.
    """
    hex = [[0 for i in range(4)] for j in range(4)]
    key_index = 0
    for i in range(len(hex)):
        for j in range(len(hex)):
            two_digit_hex = hex_string[key_index:key_index+2]
            hex[j][i] = int(two_digit_hex, 16)
            key_index +=2
    return hex

def plaintext_to_hex(ptext):
    """
    Given a plaintext string, change it into as many 16-byte hex strings
    are required to cover the plaintext.
    """
    amt = len(ptext) // 16
    if len(ptext) % 16 != 0:
        amt += 1

    # get one large hex string
    hex_string = ""
    for i in range(1, len(ptext)+1):
        hex_string += "{0:02X}".format(ord(ptext[i-1:i]))

    # when hex string is done, pad the end according to PKCS5 padding
    padding_amount = amt*16 - (len(hex_string) // 2)
    # represent 5 bytes as 0x05, 14 bytes as 0x14, etc. use zfill to add 0 if needed.
    pad = str(padding_amount).zfill(2)
    for i in range(padding_amount+1):
        hex_string += pad

    # split large hex strings into list of hex strings of size 32.
    hex_strings = ["" for i in range(amt)]
    for i in range(amt):
        # grab from index 0:32, 32:64, etc...
        hex_strings[i] = hex_string[i*32:(i+1)*32]

    return hex_strings

def hex_to_plaintext(hex_strings):
    """Given a hex string, decode it into plaintext and remove padding."""
    hex_combined = ""
    # add up every hex string except for the last one which may include padding.
    for i in range(len(hex_strings)-1):
        hex_combined += hex_strings[i]

    # get final string and how many hex characters to take off of it.
    pad_string = hex_strings[-1]
    pad = pad_string[(len(pad_string)-2):len(pad_string)]
    # pad amount will be 1/2 of actual characters since each char = 2 hex digits.
    pad_amount = int(pad) * 2

    # only add the non-padding characters to the hex combined string.
    hex_combined += pad_string[0:(32 - pad_amount)]
    
    # now that hex_combined is complete without padding, print it out.
    bytes_obj = bytes.fromhex(hex_combined)
    pt_string = bytes_obj.decode("ASCII")
    return pt_string
