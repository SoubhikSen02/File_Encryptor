package SoftEngProject.FileEncryptDecrypt;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

/**
 * This class provides static methods to store a file in application folder, retrieve a list of such stored files, and
 * take a stored file out of the application folder to somewhere else
 */
public class FileManager
{
    //fully qualified address of the operating system's current user's documents folder
    private static final String DOCUMENTS_FOLDER_PATH;

    //fully qualified address of the app folder path inside documents folder
    private static final String APP_FOLDER_PATH;

    //fully qualified address of the files folder path where the files are stored
    private static final String ENCRYPTED_FILES_FOLDER_PATH;

    //initialise class constants
    static
    {
        DOCUMENTS_FOLDER_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        APP_FOLDER_PATH = DOCUMENTS_FOLDER_PATH + "\\File Encryptor";
        ENCRYPTED_FILES_FOLDER_PATH = APP_FOLDER_PATH + "\\.files";
    }

    /**
     * Accessor for the folder path where the files are stored
     * @return Fully qualified address of the folder where files are stored
     */
    public static String getEncryptedFilesFolderPath()
    {
        return ENCRYPTED_FILES_FOLDER_PATH;
    }

    /**
     * Stores a given file inside the application files folder
     * @param fileName Fully qualified name of the file to be stored
     * @param overwrite specifies whether overwrite should be done if a file with the same name already exists in the
     *                  application files folder. If true, the new file will overwrite the old one.
     * @throws IOException if some error occurs while moving the files
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws NullPointerException if file objects are not created properly
     * @throws FileAlreadyExistsException if a file with the same name already exists in the application files folder
     *                                    and overwrite is given as false
     */
    public static void storeFile(String fileName, boolean overwrite)
            throws IOException, SecurityException, NullPointerException, FileAlreadyExistsException
    {
        File applicationFolder = new File(APP_FOLDER_PATH);

        //if application folder does not exist
        if(!applicationFolder.exists())
        {
            //create folder and check status of operation
            boolean folderCreationSuccess = applicationFolder.mkdir();
            if(!folderCreationSuccess)
            {
                throw new IOException();
            }
        }

        File filesFolder = new File(ENCRYPTED_FILES_FOLDER_PATH);

        //if stored files folder does not exist
        if(!filesFolder.exists())
        {
            //create folder and check status of operation
            boolean folderCreationSuccess = filesFolder.mkdir();
            if(!folderCreationSuccess)
            {
                throw new IOException();
            }
        }

        //extract only the file name from the fully qualified file name passed to the method
        //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
        int lastForwardSlashIndex = fileName.lastIndexOf("/");
        int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
        int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
        String originalFileName = fileName.substring(fileNameIndex + 1);

        //create file objects for the old file location and the new location
        File originalFile = new File(fileName);
        File newFile = new File(ENCRYPTED_FILES_FOLDER_PATH + "\\" + originalFileName);

        //if file with the same name already exists in the files folder and overwrite is false
        if(newFile.exists() && !overwrite)
        {
            throw new FileAlreadyExistsException(null);
        }
        //if file with the same name already exists in the files folder and overwrite is true
        else if(newFile.exists() && overwrite)
        {
            //delete old file that was in the folder and check the status of the operation
            boolean deleteSuccess = newFile.delete();
            if(!deleteSuccess)
            {
                throw new IOException();
            }
        }

        //move the new file into the files folder and check the status of the operation
        boolean renameSuccess = originalFile.renameTo(newFile);
        if(!renameSuccess)
        {
            throw new IOException();
        }
    }

    /**
     * Retrieves a list of files that have been stored inside the files folder
     * @return a string array consisting of the file names stored, or an empty array if none is present or any error occurs
     */
    public static String[] retrieveStoredFiles()
    {
        File applicationFolder = new File(APP_FOLDER_PATH);

        //if application folder does not exist
        if(!applicationFolder.exists())
        {
            return new String[0];
        }

        File filesFolder = new File(ENCRYPTED_FILES_FOLDER_PATH);

        //if stored files folder does not exist
        if(!filesFolder.exists())
        {
            return new String[0];
        }

        File[] files;
        try
        {
            //get file object array consisting of file objects for each file present in the files folder
            files = filesFolder.listFiles();
        }
        catch(Exception error)
        {
            return new String[0];
        }

        //if file object array is not valid or is empty
        if(files == null || files.length == 0)
        {
            return new String[0];
        }

        String[] filesList = new String[files.length];
        try
        {
            //for each file object that has been found
            for (int i = 0; i < files.length; i++)
            {
                //retrieve the fully qualified path of the file
                filesList[i] = files[i].getAbsolutePath();

                //extract the file name from the fully qualified file name and store it
                //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
                int lastForwardSlashIndex = filesList[i].lastIndexOf("/");
                int lastBackwardSlashIndex = filesList[i].lastIndexOf("\\");
                int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
                filesList[i] = filesList[i].substring(fileNameIndex + 1);
            }
        }
        catch(Exception error)
        {
            return new String[0];
        }

        //return the string array consisting of the file names
        return filesList;
    }

    /**
     * Moves a file from the application files folder to the specified folder
     * @param fileName The fully qualified file name of the file to be moved out of the application files folder
     * @param folderName The fully qualified folder name into which the file is to be moved
     * @param overwrite specifies whether overwrite should be done if a file with the same name already exists in the
     *                  given folder. If true, the file from the application files folder will overwrite the file
     *                  present in the user given folder.
     * @throws IOException if some error occurs while moving the files
     * @throws SecurityException if some host security feature prevents access to the files
     * @throws NullPointerException if file objects are not created properly
     * @throws FileAlreadyExistsException if a file with the same name already exists in the user given folder
     */
    public static void retrieveFile(String fileName, String folderName, boolean overwrite)
            throws IOException, SecurityException, NullPointerException, FileAlreadyExistsException
    {
        //extract only the file name from the fully qualified file name passed to the method
        //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
        int lastForwardSlashIndex = fileName.lastIndexOf("/");
        int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
        int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
        String originalFileName = fileName.substring(fileNameIndex + 1);

        //create old file object
        File originalFile = new File(fileName);
        File newFile;

        //if the fully qualified folder name given contains either \ or / at the end
        if(folderName.charAt(folderName.length() - 1) == '\\' || folderName.charAt(folderName.length() - 1) == '/')
        {
            //create new file object by appending only the file name
            newFile = new File(folderName + originalFileName);
        }
        //if the fully qualified folder name given does not contain either \ or / at the end
        else
        {
            //create new file object by appending a slash and the file name
            newFile = new File(folderName + "\\" + originalFileName);
        }

        //if the file to be moved is not available
        if(!originalFile.exists())
        {
            throw new IOException();
        }

        //if file with the same name already exists in the user given folder and overwrite is false
        if(newFile.exists() && !overwrite)
        {
            throw new FileAlreadyExistsException(null);
        }
        //if file with the same name already exists in the user given folder and overwrite is true
        else if(newFile.exists() && overwrite)
        {
            //delete the file in the user given folder and check the status of the operation
            boolean deleteSuccess = newFile.delete();
            if(!deleteSuccess)
            {
                throw new IOException();
            }
        }

        //move the file to the user given folder and check the status of the operation
        boolean renameSuccess = originalFile.renameTo(newFile);
        if(!renameSuccess)
        {
            throw new IOException();
        }
    }
}
