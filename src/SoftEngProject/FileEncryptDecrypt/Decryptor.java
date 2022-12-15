package SoftEngProject.FileEncryptDecrypt;

import java.io.*;
import java.security.InvalidKeyException;

/**
 * This class describes a decryptor object which can perform decryption based on the given information.
 */
public class Decryptor
{
    //Buffer size for holding read data from a file. This amount of data is read at each go. The amount is given in bytes.
    private int FILE_READ_BUFFER_SIZE = 50;

    //Name of file to be decrypted. The file name is assumed to be fully qualified address of the file.
    private String fileName;

    //The decryption key to be used for decrypting.
    private String decryptionKey;

    /**
     * Constructor for decryption object.
     * @param name Fully qualified name of the file to be decrypted.
     * @param key The decryption key which will be used to attempt decryption of the given file.
     */
    public Decryptor(String name, String key)
    {
        fileName = name;
        decryptionKey = key;
    }

    /**
     * Decrypts the file with the decryption key and replaces the encrypted version with the decrypted original version
     * @throws FileNotFoundException if file to be decrypted does not exist
     * @throws IOException if there is any error while reading or writing a file
     * @throws NullPointerException if file object is not properly created
     * @throws SecurityException if some host security feature prevents the method from accessing or modifying a file
     * @throws InvalidKeyException if given key for decryption is invalid
     */
    public void startDecrypting()
            throws FileNotFoundException, IOException, NullPointerException, SecurityException, InvalidKeyException
    {
        //find and store decryption key value
        long keyValue = calculateKeyValue();

        //check validity of the given key
        boolean keyValid = isKeyValid(keyValue);

        //if key is not valid, throw exception to signify that
        if(!keyValid)
        {
            throw new InvalidKeyException();
        }

        FileInputStream inputFile = null;
        FileOutputStream outputFile = null;

        //temporary output file name for the decrypted version of the file
        String outputFileName = fileName + "dec";

        try
        {
            //open files for reading and writing
            inputFile = new FileInputStream(fileName);
            outputFile = new FileOutputStream(outputFileName);

            //read the key length and the key itself from the encrypted file to advance the read pointer to the actual content
            byte[] keyInfo = new byte[decryptionKey.length() + 1];
            int bytesRead = inputFile.read(keyInfo);

            //initialise read buffer
            byte[] inputBytes = new byte[FILE_READ_BUFFER_SIZE];

            //get an initial read of data and store the amount of bytes read
            bytesRead = inputFile.read(inputBytes);

            //continue while data is available and some bytes has been read and stored
            while(bytesRead != -1)
            {
                //decrypt the read bytes and write them
                for(int i = 0; i < bytesRead; i++)
                {
                    inputBytes[i] = (byte)(inputBytes[i] - keyValue);
                }
                outputFile.write(inputBytes, 0, bytesRead);

                //read the next set of bytes
                bytesRead = inputFile.read(inputBytes);
            }
        }
        finally
        {
            //if input file has been opened
            if(inputFile != null)
            {
                inputFile.close();
            }

            //if output file has been opened
            if(outputFile != null)
            {
                outputFile.close();
            }
        }

        //create file objects to replace encrypted file with decrypted one
        File decryptedFile = new File(outputFileName);
        File originalFile = new File(fileName);

        //delete encrypted file and get the status of operation
        boolean deleteSuccess = originalFile.delete();
        if(!deleteSuccess)
        {
            throw new IOException();
        }

        //rename decrypted file to the same file name as encrypted and get the status of the operation
        boolean renameSuccess = decryptedFile.renameTo(originalFile);
        if(!renameSuccess)
        {
            throw new IOException();
        }
    }

    /**
     * Verifies whether the decryption key given is valid for the given file or not.
     * @param keyValue The value of the decryption key
     * @return True if key is valid and the file can be correctly decrypted and restored to original form, otherwise it returns false
     * @throws FileNotFoundException if the file to be decrypted does not exist
     * @throws IOException if there is some read error while reading from the encrypted file
     */
    private boolean isKeyValid(long keyValue) throws FileNotFoundException, IOException
    {
        FileInputStream encryptedFile = null;

        try
        {
            //open encrypted file
            encryptedFile = new FileInputStream(fileName);

            //read and decrypt the first byte to get the length of the key which was used to encrypt this file
            byte keyLength = (byte)(encryptedFile.read() - keyValue);

            //if length of the key used to encrypt does not match the length of the key given for decryption
            if(keyLength != decryptionKey.length())
            {
                return false;
            }

            //read the key used to encrypt the file
            byte[] encryptedKey = new byte[decryptionKey.length()];
            int bytesRead = encryptedFile.read(encryptedKey);

            //if the length of key read from encrypted file does not match the length of the key given for decryption
            if(bytesRead != decryptionKey.length())
            {
                return false;
            }

            //decrypt the key read from the encrypted file
            StringBuilder decryptedKey = new StringBuilder();
            for(int i = 0; i < encryptedKey.length; i++)
            {
                decryptedKey.append((char)(byte)(encryptedKey[i] - keyValue));
            }

            //if the key given for decryption does not match with the key used to encrypt the file
            if(!decryptionKey.equals(decryptedKey.toString()))
            {
                return false;
            }
        }
        finally
        {
            //if file was opened for reading
            if(encryptedFile != null)
            {
                encryptedFile.close();
            }
        }

        //as all the previous tests completed successfully, return true as key is valid
        return true;
    }

    /**
     * Calculates the value of the decryption key. The algorithm used here is simple.
     * If the decryption key is c1c2c3...cn
     * Then value of key = (c1 * 10^(n-1)) + (c2 * 10^(n-2)) + (c3 * 10^(n-3)) + ... + (cn * 10^(n-n))
     * The above equation is implemented by the loop inside the method.
     * @return The value of the decryption key
     */
    private long calculateKeyValue()
    {
        //initialise key value
        long keyValue = 0;

        //implement the given formula
        for(int i = 0; i < decryptionKey.length(); i++)
        {
            keyValue = (keyValue * 10) + decryptionKey.charAt(i);
        }

        return keyValue;
    }
}
