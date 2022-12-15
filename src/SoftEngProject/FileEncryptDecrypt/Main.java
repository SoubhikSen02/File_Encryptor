package SoftEngProject.FileEncryptDecrypt;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.security.InvalidKeyException;

/**
 * The main class which is the starting point of the app and also provides static methods to act as an interface(the use
 * of the word interface here does not refer to a java interface, it just refers to the meaning of the word interface as
 * in basic english) between the swing app classes and the backend classes which perform several file operations
 */
public class Main
{
    /**
     * Main method which starts the app
     * @param args command line arguments passed. These are not used.
     */
    public static void main(String[] args)
    {
        //queue the app window creation to the swing main thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //set the look and feel to the operating system's look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch(Exception error){

                }
                //call constructor of the app window to initialise and create it
                new AppWindow();
            }
        });
    }

    /**
     * Static interface method used for encrypting a file with a random key
     * @param fileName The fully qualified address of the file to be encrypted
     * @return The random encryption key generated and used for encrypting the file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     */
    public static String encrypt(String fileName)
            throws FileNotFoundException, IOException, NullPointerException, SecurityException
    {
        //create encryptor object and start encrypting the file
        Encryptor encryptObject = new Encryptor(fileName);
        encryptObject.startEncrypting();

        //save the key used for encryption
        KeyManager.storeKey(encryptObject.getEncryptionKey());

        //add new entry to the history
        RecentHistoryManager.addRecentHistory(fileName, RecentHistoryManager.ENCRYPTION);

        //return the random key used
        return encryptObject.getEncryptionKey();
    }

    /**
     * Static interface method used for encrypting a file with a user given key
     * @param fileName The fully qualified address of the file to be encrypted
     * @param key The key with which the file is to be encrypted
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     */
    public static void encrypt(String fileName, String key)
            throws FileNotFoundException, IOException, NullPointerException, SecurityException
    {
        //create encryptor object and start encrypting the file
        Encryptor encryptObject = new Encryptor(fileName, key);
        encryptObject.startEncrypting();

        //save the key used for encryption
        KeyManager.storeKey(encryptObject.getEncryptionKey());

        //add new entry to the history
        RecentHistoryManager.addRecentHistory(fileName, RecentHistoryManager.ENCRYPTION);
    }

    /**
     * Static interface method used for decrypting a file with a user given key
     * @param fileName The fully qualified address of the file to be decrypted
     * @param key The key with which the file is to be decrypted
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws InvalidKeyException if the user given key is not valid for decryption of the given file
     */
    public static void decrypt(String fileName, String key)
            throws FileNotFoundException, IOException, NullPointerException, SecurityException, InvalidKeyException
    {
        //create decryptor object and start decrypting
        Decryptor decryptObject = new Decryptor(fileName, key);
        decryptObject.startDecrypting();

        //add new entry to the history
        RecentHistoryManager.addRecentHistory(fileName, RecentHistoryManager.DECRYPTION);
    }

    /**
     * Static interface method used for encrypting a file with a random key and storing it into the application files
     * folder
     * @param optionParent Reference to the app window frame. It is used to show option dialogs centered on the frame.
     * @param fileName The fully qualified address of the file to be encrypted
     * @return The random encryption key generated and used for encrypting the file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws InvalidPathException if file with same name already exists in the application files folder and user
     *                              chooses not to overwrite it, so encryption process is stopped.
     */
    public static String encryptAndStore(JFrame optionParent, String fileName)
            throws FileNotFoundException, IOException, NullPointerException, SecurityException, InvalidPathException
    {
        //create encryptor object and start encrypting the file
        Encryptor encryptObject = new Encryptor(fileName);
        encryptObject.startEncrypting();

        try
        {
            //try to store the file in application files folder without overwriting anything
            FileManager.storeFile(fileName, false);
        }
        //if file with the same name already exists in the application files folder
        catch(FileAlreadyExistsException overwriteError)
        {
            //create an option dialog asking user whether to overwrite or not
            int response = JOptionPane.showConfirmDialog(optionParent, "A file with the same name has already " +
                            "been stored previously.\nDo you want to overwrite it?", "File already exists",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            //if user chooses to overwrite
            if(response == JOptionPane.YES_OPTION)
            {
                try
                {
                    //try to store the store in the application files folder with overwrite mode set to on
                    FileManager.storeFile(fileName, true);
                }
                catch(FileAlreadyExistsException unknownOverwriteError)
                {
                    throw new IOException();
                }
            }
            //if user chooses not to overwrite
            else if(response == JOptionPane.NO_OPTION)
            {
                try
                {
                    //restore the file back to original format by decrypting it
                    Decryptor decryptObject = new Decryptor(fileName, encryptObject.getEncryptionKey());
                    decryptObject.startDecrypting();

                    //throw exception to signify that encryption was stopped
                    throw new InvalidPathException("", "");
                }
                catch(InvalidKeyException wrongKey)
                {
                    throw new IOException();
                }
            }
        }

        //save the key used for encryption
        KeyManager.storeKey(encryptObject.getEncryptionKey());

        //extract only the file name from the fully qualified file name passed to the method
        //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
        int lastForwardSlashIndex = fileName.lastIndexOf("/");
        int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
        int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
        String onlyFileName = fileName.substring(fileNameIndex + 1);

        //add new entry to the history file
        RecentHistoryManager.addRecentHistory(FileManager.getEncryptedFilesFolderPath() + "\\" + onlyFileName,
                RecentHistoryManager.ENCRYPTION);

        //return the random key generated
        return encryptObject.getEncryptionKey();
    }

    /**
     * Static interface method used for encrypting a file with a user given key and storing it into the application
     * files folder
     * @param optionParent Reference to the app window frame. It is used to show option dialogs centered on the frame.
     * @param fileName The fully qualified address of the file to be encrypted
     * @param key The key with which the file is to be encrypted.
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws InvalidPathException if file with same name already exists in the application files folder and user
     *                              chooses not to overwrite it, so encryption process is stopped.
     */
    public static void encryptAndStore(JFrame optionParent, String fileName, String key)
            throws FileNotFoundException, IOException, NullPointerException, SecurityException, InvalidPathException
    {
        //create encryptor object and start encrypting the file
        Encryptor encryptObject = new Encryptor(fileName, key);
        encryptObject.startEncrypting();

        try
        {
            //try to store the file in application files folder without overwriting anything
            FileManager.storeFile(fileName, false);
        }
        //if file with the same name already exists in the application files folder
        catch(FileAlreadyExistsException overwriteError)
        {
            //create an option dialog asking user whether to overwrite or not
            int response = JOptionPane.showConfirmDialog(optionParent, "A file with the same name has already " +
                            "been stored previously.\nDo you want to overwrite it?", "File already exists",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            //if user chooses to overwrite
            if(response == JOptionPane.YES_OPTION)
            {
                try
                {
                    //try to store the store in the application files folder with overwrite mode set to on
                    FileManager.storeFile(fileName, true);
                }
                catch(FileAlreadyExistsException unknownOverwriteError)
                {
                    throw new IOException();
                }
            }
            //if user chooses not to overwrite
            else if(response == JOptionPane.NO_OPTION)
            {
                try
                {
                    //restore the file back to original format by decrypting it
                    Decryptor decryptObject = new Decryptor(fileName, key);
                    decryptObject.startDecrypting();

                    //throw exception to signify that encryption was stopped
                    throw new InvalidPathException("", "");
                }
                catch(InvalidKeyException wrongKey)
                {
                    throw new IOException();
                }
            }
        }

        //save the key used for encryption
        KeyManager.storeKey(encryptObject.getEncryptionKey());

        //extract only the file name from the fully qualified file name passed to the method
        //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
        int lastForwardSlashIndex = fileName.lastIndexOf("/");
        int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
        int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
        String onlyFileName = fileName.substring(fileNameIndex + 1);

        //add new entry to the history file
        RecentHistoryManager.addRecentHistory(FileManager.getEncryptedFilesFolderPath() + "\\" + onlyFileName,
                RecentHistoryManager.ENCRYPTION);
    }

    /**
     * Static interface method used for decrypting a file stored in the application files folder with a user given key
     * @param optionParent Reference to the app window frame. It is used to show option dialogs centered on the frame.
     * @param fileName The fully qualified address of the file to be decrypted
     * @param folderName The fully qualified address of the folder where the decrypted file will be put in
     * @param key The key to be used for decrypting the file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if some error occurs while reading or writing
     * @throws NullPointerException if file objects fail to be created properly
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws InvalidKeyException if the user given key is not valid for decryption of the given file
     * @throws InvalidPathException if file with the same name already exists in the given folder and user chooses not
     *                              to overwrite it, so decryption process is stopped.
     */
    public static void decryptStored(JFrame optionParent, String fileName, String folderName, String key) throws
            FileNotFoundException, IOException, NullPointerException, SecurityException, InvalidKeyException,
            InvalidPathException
    {
        //create decryptor object and start decrypting
        Decryptor decryptObject = new Decryptor(fileName, key);
        decryptObject.startDecrypting();

        try
        {
            //try to put the file in the given folder without overwriting anything
            FileManager.retrieveFile(fileName, folderName, false);
        }
        //if file with the same name already exists in the given folder
        catch(FileAlreadyExistsException overwriteError)
        {
            //create an option dialog asking the user whether to overwrite or not
            int response = JOptionPane.showConfirmDialog(optionParent, "A file with the same name already " +
                            "exists in the given folder.\nDo you want to overwrite it?", "File already exists",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            //if user chooses to overwrite
            if(response == JOptionPane.YES_OPTION)
            {
                try
                {
                    //try to put the file in the given folder with overwrite mode set to on
                    FileManager.retrieveFile(fileName, folderName, true);
                }
                catch(FileAlreadyExistsException unknownOverwriteError)
                {
                    throw new IOException();
                }
            }
            //if user chooses not to overwrite
            else if(response == JOptionPane.NO_OPTION)
            {
                //re-encrypt the file to get it back to its initial form
                Encryptor encryptObject = new Encryptor(fileName, key);
                encryptObject.startEncrypting();

                //throw exception to signify that decryption was stopped
                throw new InvalidPathException("", "");
            }
        }

        //extract only the file name from the fully qualified file name passed to the method
        //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
        int lastForwardSlashIndex = fileName.lastIndexOf("/");
        int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
        int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
        String onlyFileName = fileName.substring(fileNameIndex + 1);

        //create the new file location address
        String newFileName;
        //if the given folder name contains either / or \ at the end
        if(folderName.charAt(folderName.length() - 1) == '\\' || folderName.charAt(folderName.length() - 1) == '/')
        {
            //create the new file name with just the folder name and extracted file name
            newFileName = folderName + onlyFileName;
        }
        //if the given folder name does not contain either / or \ at the end
        else
        {
            //create the new file name with the folder name, a slash and the extracted file name
            newFileName = folderName + "\\" + onlyFileName;
        }

        //add new entry to history file
        RecentHistoryManager.addRecentHistory(newFileName, RecentHistoryManager.DECRYPTION);
    }
}
