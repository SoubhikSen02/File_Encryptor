package SoftEngProject.FileEncryptDecrypt;

import java.io.File;

/**
 * This class is a static utility class which provides functionality for checking whether a file or folder is valid
 */
public class FileNameValidator
{
    /**
     * Verifies whether a file exits and whether it is currently accessible or not
     * @param fileName Fully qualified name of the file to be checked.
     * @return True if file exists and can be accessed and modified, otherwise false
     */
    public static boolean isFileValid(String fileName)
    {
        File inputFile;

        try
        {
            inputFile = new File(fileName);

            //if the given file does not exist or refers to a folder and not a file
            if(!inputFile.isFile())
            {
                return false;
            }

            //if the given file cannot be accessed or modified currently
            if(!inputFile.renameTo(inputFile))
            {
                return false;
            }
        }
        catch(NullPointerException | SecurityException fileNotAvailableError)
        {
            //if any issue occurs while trying to do something with the file, return false as it will result in error
            //regardless of the underlying issue
            return false;
        }

        //as all tests have been passed, return true
        return true;
    }

    /**
     * Verifies whether a folder exists or not
     * @param folderName Fully qualified name of the folder to be checked.
     * @return True if the folder exists, otherwise false
     */
    public static boolean isFolderValid(String folderName)
    {
        File outputFolder;

        try
        {
            outputFolder = new File(folderName);

            //if the given address does not refer to a folder or directory
            if(!outputFolder.isDirectory())
            {
                return false;
            }
        }
        catch(NullPointerException | SecurityException folderError)
        {
            //if any error occurs while dealing with this folder, return false as an error will occur regardless of the
            //underlying issue
            return false;
        }

        //as all tests have been passed, return true
        return true;
    }
}
