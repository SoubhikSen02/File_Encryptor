package SoftEngProject.FileEncryptDecrypt;

/**
 * This class is a static utility class which verifies whether a key follows the basic rules given
 */
public class KeyValidator
{
    //constant minimum and maximum length of key which is allowed
    final static private int MIN_KEY_LENGTH = 8;
    final static private int MAX_KEY_LENGTH = 16;

    //max ascii charset value
    final static private int ASCII_UPPER_BOUND = 127;

    /**
     * Accessor for minimum allowed key length
     * @return minimum key length
     */
    public static int getMinKeyLength()
    {
        return MIN_KEY_LENGTH;
    }

    /**
     * Accessor for maximum allowed key length
     * @return maximum key length
     */
    public static int getMaxKeyLength()
    {
        return MAX_KEY_LENGTH;
    }

    /**
     * Verifies whether the key is within the allowable length and whether it follows ascii charset or not
     * @param key The key to be verified
     * @return True if key is within allowable length and it only uses ascii characters, otherwise false
     */
    public static boolean isKeyValid(String key)
    {
        //if key is not given properly or key length is outside the allowable limits
        if(key == null || key.length() < MIN_KEY_LENGTH || key.length() > MAX_KEY_LENGTH)
        {
            return false;
        }

        //check each character of the key whether it is ascii
        for(int i = 0; i < key.length(); i++)
        {
            char letter = key.charAt(i);

            if(letter > ASCII_UPPER_BOUND)
            {
                return false;
            }
        }

        //all previous tests have been passed, so return true as key is valid
        return true;
    }
}
