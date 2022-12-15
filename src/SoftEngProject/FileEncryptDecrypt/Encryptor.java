package SoftEngProject.FileEncryptDecrypt;

import java.io.*;
import java.util.Random;

/**
 * This class describes an encryptor object which can perform encryption based on the given information.
 */
public class Encryptor
{
    //Constant length with which random key is generated if selected
    final private int RANDOM_KEY_LENGTH = 16;

    //The lower and upper bound for characters to be given in a random key. Refer to unicode character list.
    final private int CHAR_LOWER_BOUND = 33;
    final private int CHAR_UPPER_BOUND = 126;

    //Buffer size for holding read data from a file. This amount of data is read at each go. The amount is given in bytes.
    final private int FILE_READ_BUFFER_SIZE = 50;

    //Name of file to be encrypted. The file name is assumed to be fully qualified address of the file.
    private String fileName;

    //Encryption key which is to be used for encryption.
    private String encryptionKey;

    /**
     * Constructor for encryption with random key.
     * @param file Fully qualified name of file to be encrypted.
     */
    public Encryptor(String file)
    {
        fileName = file;

        //generate random key and store it.
        encryptionKey = randomKeyGenerator();
    }

    /**
     * Constructor for encryption with user given key.
     * @param file Fully qualified name of file to be encrypted.
     * @param key Encryption key to be used for encryption of the file.
     */
    public Encryptor(String file, String key)
    {
        fileName = file;
        encryptionKey = key;
    }

    /**
     * Accessor for the encryption key being used.
     * @return Encryption key that is being used.
     */
    public String getEncryptionKey()
    {
        return encryptionKey;
    }

    /**
     * Generates a random key for encryption according to given constant fields.
     * @return The random key that has been generated.
     */
    private String randomKeyGenerator()
    {
        Random rand = new Random();

        //mutable string to store partially generated random key
        StringBuilder randomKey = new StringBuilder();

        //loop for generating each character of the random key
        for(int i = 0; i < RANDOM_KEY_LENGTH; i++)
        {
            //generate random character between the given lower and upper bounds.
            char randomChar = (char)(rand.nextInt(CHAR_UPPER_BOUND - CHAR_LOWER_BOUND + 1) + CHAR_LOWER_BOUND);

            //add it to the partial generated key
            randomKey.append(randomChar);
        }

        return randomKey.toString();
    }

    /**
     * Encrypts the file with the encryption key and replaces the original version with the encrypted version
     * @throws FileNotFoundException if file does not exist
     * @throws IOException if there is an error while reading or writing files
     * @throws NullPointerException if file object is not properly created
     * @throws SecurityException if some host security feature prevents the method from accessing or modifying a file
     */
    public void startEncrypting() throws FileNotFoundException, IOException, NullPointerException, SecurityException
    {
        FileInputStream inputFile = null;

        //temporary output file name for the encrypted version of the original file
        String outputFileName = fileName + "enc";

        FileOutputStream outputFile = null;

        try
        {
            //open both input and output files
            inputFile = new FileInputStream(fileName);
            outputFile = new FileOutputStream(outputFileName);

            //find and store encryption key value
            long keyValue = calculateKeyValue();

            //write the encrypted length of the encryption key for later decryption checking
            outputFile.write((byte) (encryptionKey.length() + keyValue));

            //write the encrypted version of the encryption key for later decryption checking
            for (int i = 0; i < encryptionKey.length(); i++)
            {
                outputFile.write((byte) (encryptionKey.charAt(i) + keyValue));
            }

            //initialise read buffer
            byte[] inputBytes = new byte[FILE_READ_BUFFER_SIZE];

            //get an initial read of data and store the amount of bytes read
            int bytesRead = inputFile.read(inputBytes);

            //continue while data is available and some bytes has been read and stored
            while (bytesRead != -1)
            {
                //encrypt the read bytes and write them
                for (int i = 0; i < bytesRead; i++)
                {
                    inputBytes[i] = (byte) (inputBytes[i] + keyValue);
                }
                outputFile.write(inputBytes, 0, bytesRead);

                //read the next set of bytes
                bytesRead = inputFile.read(inputBytes);
            }
        }
        finally
        {
            //if output file has been opened
            if(outputFile != null)
            {
                outputFile.close();
            }

            //if input file has been opened
            if(inputFile != null)
            {
                inputFile.close();
            }
        }

        //create file objects to replace original file with encrypted one
        File encryptedFile = new File(outputFileName);
        File originalFile = new File(fileName);

        //delete original file and get the status of operation
        boolean deleteSuccess = originalFile.delete();
        if(!deleteSuccess)
        {
            throw new IOException();
        }

        //rename encrypted file to the same file name as original and get the status of the operation
        boolean renameSuccess = encryptedFile.renameTo(originalFile);
        if(!renameSuccess)
        {
            throw new IOException();
        }
    }

    /**
     * Calculates the value of the encryption key. The algorithm used here is simple.
     * If the encryption key is c1c2c3...cn
     * Then value of key = (c1 * 10^(n-1)) + (c2 * 10^(n-2)) + (c3 * 10^(n-3)) + ... + (cn * 10^(n-n))
     * The above equation is implemented by the loop inside the method.
     * @return The value of the encryption key
     */
    private long calculateKeyValue()
    {
        //initialise key value
        long keyValue = 0;

        //implement the given formula
        for(int i = 0; i < encryptionKey.length(); i++)
        {
            keyValue = (keyValue * 10) + encryptionKey.charAt(i);
        }

        return keyValue;
    }
}
