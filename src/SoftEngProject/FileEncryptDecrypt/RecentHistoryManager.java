package SoftEngProject.FileEncryptDecrypt;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class provides static methods used for storing and retrieving history of actions over a particular time period
 */
public class RecentHistoryManager
{
    //fully qualified address of the operating system's current user's documents folder
    private static final String DOCUMENTS_FOLDER_PATH;

    //fully qualified address of the app folder path inside documents folder
    private static final String APP_FOLDER_PATH;

    //fully qualified address of the file path containing the history data
    private static final String RECENT_HISTORY_FILE_PATH;

    //constant integer enumerations used for passing whether an action is an encryption or decryption to some method calls
    public static final int DECRYPTION = 0;
    public static final int ENCRYPTION = 1;

    //time period in days for which history is kept
    private static final int MAX_RECENT_HISTORY_TIME_IN_DAYS = 7;

    //initialise class constants
    static
    {
        DOCUMENTS_FOLDER_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        APP_FOLDER_PATH = DOCUMENTS_FOLDER_PATH + "\\File Encryptor";
        RECENT_HISTORY_FILE_PATH = APP_FOLDER_PATH + "\\recent history.txt";
    }

    /**
     * Accessor for the time period for which history of actions is kept
     * @return the time period in days for recent history is kept
     */
    public static int getMaxRecentHistoryTimeInDays()
    {
        return MAX_RECENT_HISTORY_TIME_IN_DAYS;
    }

    /**
     * Adds a new entry to the history
     * @param fileName The fully qualified address of the file on which the action was done on. The location of the
     *                 file should be its final location, that is, the location it is currently at now.
     * @param action What action was performed, either encryption or decryption. Allowable values are
     *               RecentHistoryManager.DECRYPTION and RecentHistoryManager.ENCRYPTION
     * @throws FileNotFoundException if recent history file does not exist and cannot be created
     * @throws IOException if there is some error while reading or writing
     * @throws SecurityException if some host security feature prevents access to files
     * @throws NullPointerException if file objects are not created properly
     */
    public static void addRecentHistory(String fileName, int action)
            throws FileNotFoundException, IOException, SecurityException, NullPointerException
    {
        File applicationFolder = new File(APP_FOLDER_PATH);

        //if app folder does not exist
        if(!applicationFolder.exists())
        {
            //create app folder and check status of operation
            boolean folderCreationSuccess = applicationFolder.mkdir();
            if(!folderCreationSuccess)
            {
                throw new IOException();
            }
        }

        File recentHistoryFile = new File(RECENT_HISTORY_FILE_PATH);

        FileOutputStream outputFile = null;
        try
        {
            //append to recent history file
            outputFile = new FileOutputStream(RECENT_HISTORY_FILE_PATH, true);

            //extract only the file name from the fully qualified file name passed to the method
            //since \ and / are both valid for file addresses, both needs to be checked to see which is at the end
            int lastForwardSlashIndex = fileName.lastIndexOf("/");
            int lastBackwardSlashIndex = fileName.lastIndexOf("\\");
            int fileNameIndex = Math.max(lastForwardSlashIndex, lastBackwardSlashIndex);
            String onlyFileName = fileName.substring(fileNameIndex + 1);

            //get current time info
            Date currentTime = new Date();

            //make a string of the data to be written
            //it contains file name, fully qualified file name, action taken, current time, and lastly followed by a
            //carriage return to add newline between history entries
            String writeData = onlyFileName + "," + fileName + "," + ((action == 0)? "Decrypted" : "Encrypted") + ","
                    + currentTime + "\r\n";

            byte[] writeChars = new byte[writeData.length()];

            //convert each character of the string into byte and store it into the byte array
            for (int i = 0; i < writeChars.length; i++)
            {
                writeChars[i] = (byte) (writeData.charAt(i));
            }

            //write the bytes
            outputFile.write(writeChars);
        }
        finally
        {
            //if file was opened
            if(outputFile != null)
            {
                outputFile.close();
            }
        }
    }

    /**
     * Retrieves recent history data from the history file
     * @return A two-dimensional array of the history data. The first index refers to each entry of history, and the
     *         second index refers to the 4 different parts in each entry. The 4 different parts are only file name,
     *         fully qualified file name, action taken and the time the action was taken. If no history data is found
     *         or if there is any error faced, it returns an empty two-dimensional array.
     */
    public static String[][] getRecentHistory()
    {
        //clean any history older than the maximum time period history is kept for
        cleanOldHistory();

        try
        {
            File applicationFolder = new File(APP_FOLDER_PATH);
            File recentHistoryFile = new File(RECENT_HISTORY_FILE_PATH);

            //if app folder or history file does not exist
            if (!applicationFolder.exists() || !recentHistoryFile.exists())
            {
                return new String[0][0];
            }
        }
        catch(Exception error)
        {
            return new String[0][0];
        }

        String readData;
        FileInputStream inputFile = null;
        try
        {
            //read from history file
            inputFile = new FileInputStream(RECENT_HISTORY_FILE_PATH);

            StringBuilder historyData = new StringBuilder();

            //read initial byte
            byte readByte = (byte)inputFile.read();

            //while bytes are available
            while(readByte != -1)
            {
                //store the byte
                historyData.append((char)readByte);

                //read next byte
                readByte = (byte)inputFile.read();
            }

            //convert the read data to string and store it
            readData = historyData.toString();
        }
        catch(Exception error)
        {
            return new String[0][0];
        }
        finally
        {
            try
            {
                //if file was opened
                if (inputFile != null)
                {
                    inputFile.close();
                }
            }
            catch(Exception error)
            {
                return new String[0][0];
            }
        }

        //string arrays for formatting the raw data into desired forms
        String[] readEntries;
        String[][] formattedReadData;
        try
        {
            //split the individual entries using the newline between each of them
            readEntries = readData.split("\r\n");

            //initialise the formatted string array with required space
            formattedReadData = new String[readEntries.length][4];

            //for each history entry present
            for(int i = 0; i < readEntries.length; i++)
            {
                //split the 4 parts of the entry using comma which was used as their separator when storing
                String[] entryDetails = readEntries[i].split(",");

                //if number of parts of the entry does not equal 4
                if(entryDetails.length != 4)
                {
                    return new String[0][0];
                }

                //put the parts of the entry extracted into the relevant positions in the formatted string array
                formattedReadData[i][0] = entryDetails[0];
                formattedReadData[i][1] = entryDetails[1];
                formattedReadData[i][2] = entryDetails[2];
                formattedReadData[i][3] = entryDetails[3];
            }

        }
        catch(Exception error)
        {
            return new String[0][0];
        }

        //return the formatted two-dimensional array
        return formattedReadData;
    }

    /**
     * Removes old history that exceeds the maximum time period history is kept for
     */
    private static void cleanOldHistory()
    {
        //get current time info
        Date currentDate = new Date();

        try
        {
            File applicationFolder = new File(APP_FOLDER_PATH);
            File recentHistoryFile = new File(RECENT_HISTORY_FILE_PATH);

            //if app folder or history file does not exist
            if (!applicationFolder.exists() || !recentHistoryFile.exists())
            {
                return;
            }
        }
        catch(Exception error)
        {
            return;
        }

        FileInputStream inputFile = null;
        FileOutputStream outputFile = null;
        try
        {
            //open files for reading and writing
            inputFile = new FileInputStream(RECENT_HISTORY_FILE_PATH);
            outputFile = new FileOutputStream(RECENT_HISTORY_FILE_PATH + "temp");

            StringBuilder historyData = new StringBuilder();

            //read initial byte
            byte readByte = (byte)inputFile.read();

            //while bytes are available
            while(readByte != -1)
            {
                //store the byte
                historyData.append((char)readByte);

                //read next byte
                readByte = (byte)inputFile.read();
            }

            //split the read data into each entry by using the newline between each of them
            String[] historyEntries = historyData.toString().split("\r\n");

            //for each entry read
            for(String entry : historyEntries)
            {
                //split the 4 parts of the entry using the comma which was used as their separator when they were stored
                String[] entryDetails = entry.split(",");

                //if the number of parts of the entry does not equal 4
                if(entryDetails.length != 4)
                {
                    return;
                }

                //parse the time info that is present in the entry to get the time when the entry was stored
                //the string pattern corresponds to how the time was stored in text format
                //consult documentation for more detail about meaning of each letter in the pattern
                Date entryDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(entryDetails[3]);

                //get the difference of time in milliseconds from the current time and the entry time
                long differenceInMilliseconds = currentDate.getTime() - entryDate.getTime();

                //convert the time difference in milliseconds to time difference in days
                long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds);

                //if the time difference in days is less than the max time history is kept for
                if(differenceInDays <= MAX_RECENT_HISTORY_TIME_IN_DAYS)
                {
                    //create a byte array to write the data to the new history file as it is within the history time limit
                    byte[] writeData = new byte[entry.length() + 2];

                    int i;
                    //write the entry into the byte array along with the carriage return at the end
                    for(i = 0; i < entry.length(); i++)
                    {
                        writeData[i] = (byte) entry.charAt(i);
                    }
                    writeData[i] = (byte) '\r';
                    i++;
                    writeData[i] = (byte) '\n';

                    //write the bytes to the new output file
                    outputFile.write(writeData);
                }
            }
        }
        catch(Exception error)
        {
            return;
        }
        finally
        {
            try
            {
                //if old history file was opened
                if (inputFile != null)
                {
                    inputFile.close();
                }
            }
            catch(Exception error)
            {
                return;
            }

            try
            {
                //if new history file was opened
                if (outputFile != null)
                {
                    outputFile.close();
                }
            }
            catch(Exception error)
            {
                return;
            }
        }

        try
        {
            //create file objects for both history files
            File originalFile = new File(RECENT_HISTORY_FILE_PATH);
            File newFile = new File(RECENT_HISTORY_FILE_PATH + "temp");

            //if either of the files are not present
            if(!originalFile.exists() || !newFile.exists())
            {
                return;
            }

            //delete the old history file and check the status of the operation
            boolean deleteSuccess = originalFile.delete();
            if(!deleteSuccess)
            {
                return;
            }

            //rename the new history file to the original history file name and check the status of the operation
            boolean renameSuccess = newFile.renameTo(originalFile);
            if(!renameSuccess)
            {
                return;
            }
        }
        catch(Exception error)
        {
            return;
        }
    }
}
