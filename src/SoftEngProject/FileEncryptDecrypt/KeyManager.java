package SoftEngProject.FileEncryptDecrypt;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.security.InvalidKeyException;

/**
 * This class provides static methods used to save and retrieve keys used for various operations
 */
public class KeyManager
{
    //fully qualified address of the operating system's current user's documents folder
    private static final String DOCUMENTS_FOLDER_PATH;

    //fully qualified address of the app folder path inside documents folder
    private static final String APP_FOLDER_PATH;

    //fully qualified address of the file path containing saved keys inside app folder
    private static final String ENCRYPTED_KEYS_FILE_PATH;

    //application password used for accessing protected features
    private static final String SECRET_PASSWORD = "FileEncDec";

    //initialise class constants
    static
    {
        DOCUMENTS_FOLDER_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        APP_FOLDER_PATH = DOCUMENTS_FOLDER_PATH + "\\File Encryptor";
        ENCRYPTED_KEYS_FILE_PATH = APP_FOLDER_PATH + "\\encryption keys.txt";
    }

    /**
     * Accessor for the application password
     * @return The current application password
     */
    public static String getSecretPassword()
    {
        return SECRET_PASSWORD;
    }

    /**
     * Saves a given key to encrypted keys text file for later retrieval
     * @param key the key to be saved
     * @throws FileNotFoundException if encrypted keys text file is not found and cannot be created
     * @throws IOException if there is any error while reading or writing the files
     * @throws SecurityException if some host security feature prevents file access or modification
     * @throws NullPointerException if file objects are not properly created
     */
    public static void storeKey(String key) throws FileNotFoundException, IOException, SecurityException, NullPointerException
    {
        //retrieve the stored keys
        String[] storedKeys = getStoredKeys();
        for(String storedKey : storedKeys)
        {
            //if the key is found to have been already stored before
            if(storedKey.equals(key))
            {
                return;
            }
        }

        //create app folder file object
        File applicationFolder = new File(APP_FOLDER_PATH);

        //if app folder does not exist
        if(!applicationFolder.exists())
        {
            //create app folder and check status of the operation
            boolean folderCreationSuccess = applicationFolder.mkdir();
            if(!folderCreationSuccess)
            {
                throw new IOException();
            }
        }

        //create encrypted keys file object
        File encryptedKeysFile = new File(ENCRYPTED_KEYS_FILE_PATH);

        //if the file exists
        if(encryptedKeysFile.exists())
        {
            try
            {
                //decrypt it before accessing it
                Decryptor decryptObject = new Decryptor(ENCRYPTED_KEYS_FILE_PATH, SECRET_PASSWORD);
                decryptObject.startDecrypting();
            }
            catch (InvalidKeyException decryptionError)
            {
                throw new IOException();
            }
        }

        FileOutputStream outputFile = null;
        try
        {
            //append to encrypted file
            outputFile = new FileOutputStream(ENCRYPTED_KEYS_FILE_PATH, true);

            //create the array to store the bytes to be appended to the file
            byte[] keyChars = new byte[key.length() + 2];

            //add the key characters to the array
            int i;
            for (i = 0; i < key.length(); i++) {
                keyChars[i] = (byte) (key.charAt(i));
            }

            //add \r\n for carriage return to the array to generate a new line in the file
            keyChars[i] = (byte) '\r';
            i++;
            keyChars[i] = (byte) '\n';

            //write the bytes array
            outputFile.write(keyChars);
        }
        finally
        {
            //if file was opened
            if(outputFile != null)
            {
                outputFile.close();
            }
        }

        //encrypt the file back again
        Encryptor encryptObject = new Encryptor(ENCRYPTED_KEYS_FILE_PATH, SECRET_PASSWORD);
        encryptObject.startEncrypting();
    }

    /**
     * Retrieves keys that have been saved before
     * @return an array of strings consisting of the individual keys, or an empty array if no keys are found or any other error occurs
     */
    public static String[] getStoredKeys()
    {
        try
        {
            //create required file objects
            File applicationFolder = new File(APP_FOLDER_PATH);
            File encryptedKeysFile = new File(ENCRYPTED_KEYS_FILE_PATH);

            //if application folder or encrypted keys file does not exist
            if (!applicationFolder.exists() || !encryptedKeysFile.exists())
            {
                return new String[0];
            }

            //decrypt the file before reading from it
            Decryptor decryptObject = new Decryptor(ENCRYPTED_KEYS_FILE_PATH, SECRET_PASSWORD);
            decryptObject.startDecrypting();
        }
        catch(Exception error)
        {
            return new String[0];
        }

        FileInputStream inputFile = null;
        String[] storedKeys;
        try
        {
            //read from encrypted keys file
            inputFile = new FileInputStream(ENCRYPTED_KEYS_FILE_PATH);

            StringBuilder keysData = new StringBuilder();

            //read initial byte
            byte readByte = (byte)inputFile.read();

            //while bytes are available
            while(readByte != -1)
            {
                //store the byte
                keysData.append((char)readByte);

                //read next byte
                readByte = (byte)inputFile.read();
            }

            //split the read data containing the keys using the newline between each of them and store them into an array
            storedKeys = keysData.toString().split("\r\n");
        }
        catch(Exception error)
        {
            return new String[0];
        }
        finally
        {
            //if file was opened
            try
            {
                if (inputFile != null)
                {
                    inputFile.close();
                }
            }
            catch(Exception error)
            {
                return new String[0];
            }
        }

        try
        {
            //re-encrypt the encrypted keys file
            Encryptor encryptObject = new Encryptor(ENCRYPTED_KEYS_FILE_PATH, SECRET_PASSWORD);
            encryptObject.startEncrypting();
        }
        catch(Exception error)
        {
            return new String[0];
        }

        //return the array containing the retrieved keys
        return storedKeys;
    }
}
