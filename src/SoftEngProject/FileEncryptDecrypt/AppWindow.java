package SoftEngProject.FileEncryptDecrypt;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class provides the frontend app window part of the entire application.
 */
public class AppWindow extends JFrame
{
    //resolution scaling support
    //reference resolution used for developing and basing calculations on
    final private double referenceResX = 1600;
    final private double referenceResY = 900;
    //resolution factors calculated using the user's current resolution
    private double resolutionScalingFactorX;
    private double resolutionScalingFactorY;

    //responsive resize scaling support
    //initial window sizes when starting the app used for basing calculations on
    private double initialWindowSizeX;
    private double initialWindowSizeY;
    //resize scaling factors calculated using the current window size
    private double windowScalingFactorX;
    private double windowScalingFactorY;

    //top headers of app window
    private JLabel mainWindowTopHeader;
    private JLabel differentWindowSubHeader;

    //main window buttons
    private JButton mainWindowEncryptButton;
    private JButton mainWindowDecryptButton;
    private JButton mainWindowEncryptStoreButton;
    private JButton mainWindowDecryptStoredButton;
    private JButton mainWindowRecentHistoryButton;

    //decrypt window labels, textfields, buttons and file browser
    private JLabel decryptWindowEnterFileLocation;
    private JLabel decryptWindowEnterKey;
    private JTextField decryptWindowEnterFileLocationText;
    private JTextField decryptWindowEnterKeyText;
    private JButton decryptWindowStartButton;
    private JButton decryptWindowTryStoredKeysButton;
    private JButton decryptWindowCancelButton;
    private JButton decryptWindowFileBrowserButton;
    private JButton decryptWindowForgotKeyButton;
    private JComboBox<String> decryptWindowForgotKeyList;
    private JButton decryptWindowCustomKeyButton;
    private JLabel decryptWindowWrongPasswordError;
    private JLabel decryptWindowNoStoredKeysError;
    private JLabel decryptWindowWrongKeyChosenError;
    private JFileChooser decryptWindowFileBrowser;
    private JLabel decryptWindowFileLocationNotFoundError;
    private JLabel decryptWindowFileLocationIOError;
    private JLabel decryptWindowKeyInvalidError;
    private JLabel decryptWindowDecryptingText;

    //encrypt window labels, textfields, buttons, file browser
    private JLabel encryptWindowEnterFileLocation;
    private JLabel encryptWindowEnterKey;
    private JTextField encryptWindowEnterFileLocationText;
    private JTextField encryptWindowEnterKeyText;
    private JButton encryptWindowStartButton;
    private JButton encryptWindowCancelButton;
    private JButton encryptWindowFileBrowserButton;
    private JFileChooser encryptWindowFileBrowser;
    private ButtonGroup encryptWindowKeyOptions;
    private JRadioButton encryptWindowUserGivenKeyOption;
    private JRadioButton encryptWindowRandomKeyOption;
    private JLabel encryptWindowFileLocationNotFoundError;
    private JLabel encryptWindowFileLocationIOError;
    private JLabel encryptWindowKeyInvalidError;
    private JLabel encryptWindowEncryptingText;

    //encrypt and store window labels, textfields, buttons, file browser
    private JLabel encryptStoreWindowEnterFileLocation;
    private JTextField encryptStoreWindowEnterFileLocationText;
    private JButton encryptStoreWindowBrowseFileButton;
    private JFileChooser encryptStoreWindowBrowseFileMenu;
    private JLabel encryptStoreWindowFileNotFoundError;
    private JLabel encryptStoreWindowFileIOError;
    private JLabel encryptStoreWindowEnterKey;
    private JRadioButton encryptStoreWindowCustomKeyButton;
    private JRadioButton encryptStoreWindowRandomKeyButton;
    private ButtonGroup encryptStoreWindowKeyOptions;
    private JTextField encryptStoreWindowEnterKeyText;
    private JLabel encryptStoreWindowKeyInvalidError;
    private JButton encryptStoreWindowStartButton;
    private JButton encryptStoreWindowCancelButton;
    private JLabel encryptStoreWindowEncryptingText;

    //decrypt from stored window labels, textfields, buttons and file browser
    private JLabel decryptStoredWindowChooseFile;
    private JComboBox<String> decryptStoredWindowChooseFileList;
    private JLabel decryptStoredWindowEnterKey;
    private JTextField decryptStoredWindowEnterKeyText;
    private JLabel decryptStoredWindowInvalidKeyError;
    private JButton decryptStoredWindowCancelButton;
    private JButton decryptStoredWindowStartButton;
    private JButton decryptStoredWindowTryAllButton;
    private JLabel decryptStoredWindowDecryptingText;
    private JLabel decryptStoredWindowEnterFolderLocation;
    private JTextField decryptStoredWindowEnterFolderLocationText;
    private JButton decryptStoredWindowBrowseFolderButton;
    private JFileChooser decryptStoredWindowBrowseFolderChooser;
    private JLabel decryptStoredWindowFolderNotFoundError;

    //recent window components
    private JButton recentWindowCancelButton;
    private DefaultTableModel recentWindowTableModel;
    private JTable recentWindowTable;
    private JScrollPane recentWindowTableContainer;

    /**
     * Constructor for the window frame. Initialises all functions and required components.
     */
    public AppWindow()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        resolutionScalingFactorX = screenSize.getWidth() / referenceResX;
        resolutionScalingFactorY = screenSize.getHeight() / referenceResY;
        //System.out.println(screenSize.getWidth() + " " + screenSize.getHeight());
        //System.out.println(resolutionScalingFactorX + " " + resolutionScalingFactorY);

        setTitle("File Encryptor Decryptor");
        setBounds((int)(200 * resolutionScalingFactorX), (int)(100 * resolutionScalingFactorY) ,
                (int)(800 * resolutionScalingFactorX), (int)(525 * resolutionScalingFactorY));
        setLayout(null);
        setMinimumSize(new Dimension((int)(720 * resolutionScalingFactorX), (int)(450 * resolutionScalingFactorY)));
        getContentPane().setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowCloseButtonClicked();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                appWindowResized();
            }
        });

        Dimension windowSize = getBounds().getSize();
        initialWindowSizeX = windowSize.getWidth();
        initialWindowSizeY = windowSize.getHeight();
        windowScalingFactorX = 1;
        windowScalingFactorY = 1;
        //System.out.println(windowSize.getWidth() + " " + windowSize.getHeight());
        //System.out.println(windowScalingFactorX + " " + windowScalingFactorY);

        initializeMainWindowComponents();

        initializeEncryptWindowComponents();

        initializeDecryptWindowComponents();

        initializeEncryptStoreWindowComponents();

        initializeDecryptStoredWindowComponents();

        initializeRecentWindowComponents();

        setVisible(true);
    }

    /**
     * Sets different attributes of a component based on the passed data.
     * @param component The reference to the component itself
     * @param xPosition X position in pixels to which the component is to be set to relative to window frame
     * @param yPosition Y position in pixels to which the component is to be set to relative to window frame
     * @param width Width in pixels to which the component should be set to
     * @param height Height in pixels to which the component should be set to
     * @param backgroundColor Background color to which the component is to be set to. Color object should be passed.
     * @param foregroundColor Foreground color to which the component is to be set to. Color object should be passed.
     * @param fontName The name of the font to which the component should be set to
     * @param fontStyle The font style to which the component should be set to
     * @param fontSize The font size to which the component should be set to
     * @param visible Whether the component is to be initially set to be visible or not. Allowable values are true
     *                and false.
     * @param focusPainted Whether the component should be focused by default. Allowable values are null, true and
     *                     false. null should be passed when the component is not a button.
     */
    private void setComponentAttributes(JComponent component, int xPosition, int yPosition, int width, int height,
                                        Color backgroundColor, Color foregroundColor, String fontName, int fontStyle,
                                        int fontSize, Boolean visible, Boolean focusPainted)
    {
        component.setBounds((int)(xPosition * resolutionScalingFactorX), (int)(yPosition * resolutionScalingFactorY),
                (int)(width * resolutionScalingFactorX), (int)(height * resolutionScalingFactorY));
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
        component.setFont(new Font(fontName, fontStyle, (int)(fontSize * (resolutionScalingFactorX + resolutionScalingFactorY) / 2)));
        add(component);
        component.setVisible(visible);
        if(focusPainted != null)
        {
            ((AbstractButton)component).setFocusPainted(focusPainted);
            //((AbstractButton)component).setContentAreaFilled(false);
            //((AbstractButton)component).setOpaque(true);
            ((AbstractButton)component).setBorderPainted(false);
            if(component instanceof JButton)
            {
                component.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        mouseStartHoveringOverButton(e.getComponent());
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        mouseStopHoveringOverButton(e.getComponent());
                    }
                });
            }
        }
        if(component instanceof JComboBox<?>)
        {
            ((JComboBox<?>)component).setFocusable(false);
            //((JComboBox<?>)component).setOpaque(true);
        }
    }

    /**
     * Creates the main window components
     */
    private void initializeMainWindowComponents()
    {
        mainWindowTopHeader = new JLabel("File Encryptor Decryptor", SwingConstants.CENTER);
        mainWindowTopHeader.setVerticalAlignment(SwingConstants.BOTTOM);
        mainWindowTopHeader.setOpaque(true);
        setComponentAttributes(mainWindowTopHeader, 0, 0, 800, 70, Color.black,
                Color.white, "Calibri", Font.BOLD, 36, true, null);

        differentWindowSubHeader = new JLabel("", SwingConstants.CENTER);
        differentWindowSubHeader.setVerticalAlignment(SwingConstants.CENTER);
        differentWindowSubHeader.setOpaque(true);
        setComponentAttributes(differentWindowSubHeader, 0, 70, 800, 30, Color.black,
                Color.white, "Calibri", Font.PLAIN, 20, true, null);

        mainWindowEncryptButton = new JButton("<html><center>Encrypt a file</center></html>");
        setComponentAttributes(mainWindowEncryptButton, 100, 200, 250, 80, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 24, true, false);
        mainWindowEncryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindowEncryptButtonClicked();
            }
        });

        mainWindowDecryptButton = new JButton("<html><center>Decrypt a file</center></html>");
        setComponentAttributes(mainWindowDecryptButton, 100, 350, 250, 80, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 24, true, false);
        mainWindowDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindowDecryptButtonClicked();
            }
        });

        mainWindowEncryptStoreButton = new JButton("<html><center>Encrypt and store a file</center></html>");
        setComponentAttributes(mainWindowEncryptStoreButton, 425, 200, 250, 80, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 24, true, false);
        mainWindowEncryptStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindowEncryptStoreButtonClicked();
            }
        });

        mainWindowDecryptStoredButton = new JButton("<html><center>Decrypt a stored file</center></html>");
        setComponentAttributes(mainWindowDecryptStoredButton, 425, 350, 250, 80, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 24, true, false);
        mainWindowDecryptStoredButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindowDecryptStoredButtonClicked();
            }
        });

        mainWindowRecentHistoryButton = new JButton("<html>Recent</html>");
        setComponentAttributes(mainWindowRecentHistoryButton, 690, 130, 75, 40, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 14, true, false);
        mainWindowRecentHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindowRecentButtonClicked();
            }
        });
    }

    /**
     * Creates the encrypt window components
     */
    private void initializeEncryptWindowComponents()
    {
        encryptWindowCancelButton = new JButton("<html><center>Cancel</center></html>");
        setComponentAttributes(encryptWindowCancelButton, 200, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        encryptWindowCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptWindowCancelButtonClicked();
            }
        });

        encryptWindowStartButton = new JButton("<html><center>Start Encrypting</center></html>");
        setComponentAttributes(encryptWindowStartButton, 480, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        encryptWindowStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptWindowStartButtonClicked();
            }
        });

        encryptWindowEnterFileLocation = new JLabel("Choose file to encrypt:", SwingConstants.LEFT);
        setComponentAttributes(encryptWindowEnterFileLocation, 200, 140, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        encryptWindowEnterFileLocationText = new JTextField();
        setComponentAttributes(encryptWindowEnterFileLocationText, 200, 170, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        encryptWindowEnterFileLocationText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                encryptWindowFileLocationTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                encryptWindowFileLocationTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                encryptWindowFileLocationTextUpdated();
            }
        });

        encryptWindowEnterKey = new JLabel("Enter valid key:", SwingConstants.LEFT);
        setComponentAttributes(encryptWindowEnterKey, 200, 240, 200, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        encryptWindowUserGivenKeyOption = new JRadioButton("Custom");
        setComponentAttributes(encryptWindowUserGivenKeyOption, 200, 265, 180, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, false);
        encryptWindowUserGivenKeyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptWindowCustomKeyButtonSelected();
            }
        });

        encryptWindowRandomKeyOption = new JRadioButton("Random");
        setComponentAttributes(encryptWindowRandomKeyOption, 400, 265, 180, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, false);
        encryptWindowRandomKeyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptWindowRandomKeyButtonSelected();
            }
        });

        encryptWindowKeyOptions = new ButtonGroup();
        encryptWindowKeyOptions.add(encryptWindowUserGivenKeyOption);
        encryptWindowKeyOptions.add(encryptWindowRandomKeyOption);

        encryptWindowEnterKeyText = new JTextField();
        setComponentAttributes(encryptWindowEnterKeyText, 200, 295, 400, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        encryptWindowEnterKeyText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                encryptWindowKeyTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                encryptWindowKeyTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                encryptWindowKeyTextUpdated();
            }
        });

        encryptWindowFileBrowserButton = new JButton("<html><center>Browse file</center></html>");
        setComponentAttributes(encryptWindowFileBrowserButton, 500, 170, 100, 25, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 12, false, false);
        encryptWindowFileBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptWindowBrowseFileButtonClicked();
            }
        });

        encryptWindowFileBrowser = new JFileChooser();
        setComponentAttributes(encryptWindowFileBrowser, 300, 170, 600, 400, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, null);
        encryptWindowFileBrowser.setPreferredSize(new Dimension(
                (int)(600 * resolutionScalingFactorX * resolutionScalingFactorX),
                (int)(400 * resolutionScalingFactorY * resolutionScalingFactorY)));
        encryptWindowFileBrowser.setApproveButtonText("Select");
        encryptWindowFileBrowser.setDialogTitle("Select file to encrypt");

        encryptWindowFileLocationNotFoundError = new JLabel("<html>The given file does not exist or is currently " +
                "inaccessible.</html>", SwingConstants.LEFT);
        encryptWindowFileLocationNotFoundError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptWindowFileLocationNotFoundError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptWindowFileLocationIOError = new JLabel("<html>An unknown error was encountered when reading the " +
                "file.</html>", SwingConstants.LEFT);
        encryptWindowFileLocationIOError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptWindowFileLocationIOError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptWindowKeyInvalidError = new JLabel("<html>The given key is invalid. It should contain " +
                KeyValidator.getMinKeyLength() + " - " + KeyValidator.getMaxKeyLength() + " ASCII characters.</html>",
                SwingConstants.LEFT);
        encryptWindowKeyInvalidError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptWindowKeyInvalidError, 200, 325, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptWindowEncryptingText = new JLabel("Encrypting...", SwingConstants.CENTER);
        encryptWindowEncryptingText.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptWindowEncryptingText, 200, 400, 400, 50,
                Color.darkGray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
    }

    /**
     * Creates the decrypt window components
     */
    private void initializeDecryptWindowComponents()
    {
        decryptWindowCancelButton = new JButton("<html><center>Cancel</center></html>");
        setComponentAttributes(decryptWindowCancelButton, 200, 380, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        decryptWindowCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowCancelButtonClicked();
            }
        });

        decryptWindowStartButton = new JButton("<html><center>Start Decrypting</center></html>");
        setComponentAttributes(decryptWindowStartButton, 480, 380, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        decryptWindowStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowStartButtonClicked();
            }
        });

        decryptWindowTryStoredKeysButton = new JButton("<html><center>Try all stored keys</center><html>");
        setComponentAttributes(decryptWindowTryStoredKeysButton, 340, 380, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 13, false, false);
        decryptWindowTryStoredKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowTryStoredKeysButtonClicked();
            }
        });

        decryptWindowEnterFileLocation = new JLabel("Choose file to decrypt:", SwingConstants.LEFT);
        setComponentAttributes(decryptWindowEnterFileLocation, 200, 140, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        decryptWindowEnterFileLocationText = new JTextField();
        setComponentAttributes(decryptWindowEnterFileLocationText, 200, 170, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        decryptWindowEnterFileLocationText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                decryptWindowFileLocationTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                decryptWindowFileLocationTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                decryptWindowFileLocationTextUpdated();
            }
        });

        decryptWindowEnterKey = new JLabel("Enter valid key:", SwingConstants.LEFT);
        setComponentAttributes(decryptWindowEnterKey, 200, 240, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        decryptWindowEnterKeyText = new JTextField();
        setComponentAttributes(decryptWindowEnterKeyText, 200, 270, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        decryptWindowEnterKeyText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                decryptWindowKeyTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                decryptWindowKeyTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                decryptWindowKeyTextUpdated();
            }
        });

        decryptWindowFileBrowserButton = new JButton("<html><center>Browse file</center></html>");
        setComponentAttributes(decryptWindowFileBrowserButton, 500, 170, 100, 25,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 12, false, false);
        decryptWindowFileBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowBrowseFileButtonClicked();
            }
        });

        decryptWindowFileBrowser = new JFileChooser();
        setComponentAttributes(decryptWindowFileBrowser, 300, 170, 600, 400,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
        decryptWindowFileBrowser.setPreferredSize(new Dimension(
                (int)(600 * resolutionScalingFactorX * resolutionScalingFactorX),
                (int)(400 * resolutionScalingFactorY * resolutionScalingFactorY)));
        decryptWindowFileBrowser.setApproveButtonText("Select");
        decryptWindowFileBrowser.setDialogTitle("Select file to decrypt");

        decryptWindowForgotKeyButton = new JButton("<html><center>Forgot Key?</center></html>");
        setComponentAttributes(decryptWindowForgotKeyButton, 500, 270, 100, 25,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 12, false, false);
        decryptWindowForgotKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowForgotKeyButtonClicked();
            }
        });

        decryptWindowForgotKeyList = new JComboBox<String>();
        setComponentAttributes(decryptWindowForgotKeyList, 200, 270, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        decryptWindowForgotKeyList.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                decryptWindowStoredKeysListClicked();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        decryptWindowCustomKeyButton = new JButton("<html><center>Custom key</center></html>");
        setComponentAttributes(decryptWindowCustomKeyButton, 500, 270, 100, 25,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 12, false, false);
        decryptWindowCustomKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptWindowCustomKeyButtonClicked();
            }
        });

        decryptWindowFileLocationNotFoundError = new JLabel("<html>The given file does not exist or is currently " +
                "inaccessible.</html>", SwingConstants.LEFT);
        decryptWindowFileLocationNotFoundError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowFileLocationNotFoundError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowFileLocationIOError = new JLabel("<html>An unknown error was encountered when reading the " +
                "file.</html>", SwingConstants.LEFT);
        decryptWindowFileLocationIOError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowFileLocationIOError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowKeyInvalidError = new JLabel("<html>The given key is invalid. It should be correct and contain " +
                KeyValidator.getMinKeyLength() + " - " + KeyValidator.getMaxKeyLength() + " characters.</html>",
                SwingConstants.LEFT);
        decryptWindowKeyInvalidError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowKeyInvalidError, 200, 300, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowWrongPasswordError = new JLabel("<html>The entered password is wrong. Stored keys cannot be " +
                "accessed.</html>", SwingConstants.LEFT);
        decryptWindowWrongPasswordError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowWrongPasswordError, 200, 300, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowNoStoredKeysError = new JLabel("<html>No previous stored keys were found.</html>",
                SwingConstants.LEFT);
        decryptWindowNoStoredKeysError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowNoStoredKeysError, 200, 300, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowWrongKeyChosenError = new JLabel("<html>The chosen key is wrong. Try again with another key " +
                "or use a custom key.</html>", SwingConstants.LEFT);
        decryptWindowWrongKeyChosenError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowWrongKeyChosenError, 200, 300, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptWindowDecryptingText = new JLabel("Decrypting...", SwingConstants.CENTER);
        decryptWindowDecryptingText.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptWindowDecryptingText, 200, 380, 400, 50,
                Color.darkGray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
    }

    /**
     * Creates the encrypt and store window components
     */
    private void initializeEncryptStoreWindowComponents()
    {
        encryptStoreWindowEnterFileLocation = new JLabel("Choose file to encrypt:", SwingConstants.LEFT);
        setComponentAttributes(encryptStoreWindowEnterFileLocation, 200, 140, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        encryptStoreWindowEnterFileLocationText = new JTextField();
        setComponentAttributes(encryptStoreWindowEnterFileLocationText, 200, 170, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        encryptStoreWindowEnterFileLocationText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                encryptStoreWindowFileTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                encryptStoreWindowFileTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                encryptStoreWindowFileTextUpdated();
            }
        });

        encryptStoreWindowBrowseFileButton = new JButton("<html><center>Browse file</center></html>");
        setComponentAttributes(encryptStoreWindowBrowseFileButton, 500, 170, 100, 25,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 12, false, false);
        encryptStoreWindowBrowseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptStoreWindowBrowseFileButtonClicked();
            }
        });

        encryptStoreWindowBrowseFileMenu = new JFileChooser();
        setComponentAttributes(encryptStoreWindowBrowseFileMenu, 300, 170, 600, 400,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
        encryptStoreWindowBrowseFileMenu.setPreferredSize(new Dimension(
                (int)(600 * resolutionScalingFactorX * resolutionScalingFactorX),
                (int)(400 * resolutionScalingFactorY * resolutionScalingFactorY)));
        encryptStoreWindowBrowseFileMenu.setApproveButtonText("Select");
        encryptStoreWindowBrowseFileMenu.setDialogTitle("Select file to encrypt");

        encryptStoreWindowFileNotFoundError = new JLabel("<html>The given file does not exist or is currently " +
                "inaccessible.</html>", SwingConstants.LEFT);
        encryptStoreWindowFileNotFoundError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptStoreWindowFileNotFoundError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptStoreWindowFileIOError = new JLabel("<html>An unknown error was encountered when reading the " +
                "file.</html>", SwingConstants.LEFT);
        encryptStoreWindowFileIOError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptStoreWindowFileIOError, 200, 200, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptStoreWindowEnterKey = new JLabel("Enter valid key:", SwingConstants.LEFT);
        setComponentAttributes(encryptStoreWindowEnterKey, 200, 240, 200, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        encryptStoreWindowCustomKeyButton = new JRadioButton("Custom");
        setComponentAttributes(encryptStoreWindowCustomKeyButton, 200, 265, 180, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, false);
        encryptStoreWindowCustomKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptStoreWindowCustomKeyButtonSelected();
            }
        });

        encryptStoreWindowRandomKeyButton = new JRadioButton("Random");
        setComponentAttributes(encryptStoreWindowRandomKeyButton, 400, 265, 180, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, false);
        encryptStoreWindowRandomKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptStoreWindowRandomKeyButtonSelected();
            }
        });

        encryptStoreWindowKeyOptions = new ButtonGroup();
        encryptStoreWindowKeyOptions.add(encryptStoreWindowCustomKeyButton);
        encryptStoreWindowKeyOptions.add(encryptStoreWindowRandomKeyButton);

        encryptStoreWindowEnterKeyText = new JTextField();
        setComponentAttributes(encryptStoreWindowEnterKeyText, 200, 295, 400, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        encryptStoreWindowEnterKeyText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                encryptStoreWindowKeyTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                encryptStoreWindowKeyTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                encryptStoreWindowKeyTextUpdated();
            }
        });

        encryptStoreWindowKeyInvalidError = new JLabel("<html>The given key is invalid. It should contain " +
                KeyValidator.getMinKeyLength() + " - " + KeyValidator.getMaxKeyLength() + " ASCII characters.</html>",
                SwingConstants.LEFT);
        encryptStoreWindowKeyInvalidError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptStoreWindowKeyInvalidError, 200, 325, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        encryptStoreWindowStartButton = new JButton("<html><center>Start Encrypting and Storing</center></html>");
        setComponentAttributes(encryptStoreWindowStartButton, 480, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 13, false, false);
        encryptStoreWindowStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptStoreWindowStartButtonClicked();
            }
        });

        encryptStoreWindowCancelButton = new JButton("<html><center>Cancel</center></html>");
        setComponentAttributes(encryptStoreWindowCancelButton, 200, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        encryptStoreWindowCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptStoreWindowCancelButtonClicked();
            }
        });

        encryptStoreWindowEncryptingText = new JLabel("Encrypting and storing...", SwingConstants.CENTER);
        encryptStoreWindowEncryptingText.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(encryptStoreWindowEncryptingText, 200, 400, 400, 50,
                Color.darkGray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
    }

    /**
     * Creates the decrypt from stored window components
     */
    private void initializeDecryptStoredWindowComponents()
    {
        decryptStoredWindowChooseFile = new JLabel("Choose file to decrypt:", SwingConstants.LEFT);
        setComponentAttributes(decryptStoredWindowChooseFile, 200, 110, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        decryptStoredWindowChooseFileList = new JComboBox<String>();
        setComponentAttributes(decryptStoredWindowChooseFileList, 200, 140, 400, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);

        decryptStoredWindowEnterKey = new JLabel("Enter valid key:", SwingConstants.LEFT);
        setComponentAttributes(decryptStoredWindowEnterKey, 200, 280, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        decryptStoredWindowEnterKeyText = new JTextField();
        setComponentAttributes(decryptStoredWindowEnterKeyText, 200, 310, 400, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        decryptStoredWindowEnterKeyText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                decryptStoredWindowKeyTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                decryptStoredWindowKeyTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                decryptStoredWindowKeyTextUpdated();
            }
        });

        decryptStoredWindowInvalidKeyError = new JLabel("<html>The given key is invalid. It should be correct " +
                "and contain " + KeyValidator.getMinKeyLength() + " - " + KeyValidator.getMaxKeyLength() +
                " characters.</html>", SwingConstants.LEFT);
        decryptStoredWindowInvalidKeyError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptStoredWindowInvalidKeyError, 200, 340, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);

        decryptStoredWindowCancelButton = new JButton("<html><center>Cancel</center></html>");
        setComponentAttributes(decryptStoredWindowCancelButton, 200, 400, 120, 50,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 18, false, false);
        decryptStoredWindowCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptStoredWindowCancelButtonClicked();
            }
        });

        decryptStoredWindowStartButton = new JButton("<html><center>Start Decrypting Stored File</center></html>");
        setComponentAttributes(decryptStoredWindowStartButton, 480, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 13, false, false);
        decryptStoredWindowStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptStoredWindowStartButtonClicked();
            }
        });

        decryptStoredWindowTryAllButton = new JButton("<html><center>Try all stored keys</center><html>");
        setComponentAttributes(decryptStoredWindowTryAllButton, 340, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 13, false, false);
        decryptStoredWindowTryAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptStoredWindowTryAllButtonClicked();
            }
        });

        decryptStoredWindowDecryptingText = new JLabel("Decrypting stored file...", SwingConstants.CENTER);
        decryptStoredWindowDecryptingText.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptStoredWindowDecryptingText, 200, 400, 400, 50,
                Color.darkGray, Color.black, "Calibri", Font.PLAIN, 18, false, null);

        decryptStoredWindowEnterFolderLocation = new JLabel("Choose folder to store file after decryption:",
                SwingConstants.LEFT);
        setComponentAttributes(decryptStoredWindowEnterFolderLocation, 200, 185, 400, 25,
                Color.darkGray, Color.white, "Calibri", Font.PLAIN, 18, false, null);

        decryptStoredWindowEnterFolderLocationText = new JTextField();
        setComponentAttributes(decryptStoredWindowEnterFolderLocationText, 200, 215, 290, 25,
                Color.white, Color.black, "Calibri", Font.PLAIN, 14, false, null);
        decryptStoredWindowEnterFolderLocationText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                decryptStoredWindowFolderTextUpdated();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                decryptStoredWindowFolderTextUpdated();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                decryptStoredWindowFolderTextUpdated();
            }
        });

        decryptStoredWindowBrowseFolderButton = new JButton("<html><center>Browse folder</center><html>");
        setComponentAttributes(decryptStoredWindowBrowseFolderButton, 500, 215, 100, 25, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 12, false, false);
        decryptStoredWindowBrowseFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptStoredWindowBrowseFolderButtonClicked();
            }
        });

        decryptStoredWindowBrowseFolderChooser = new JFileChooser();
        setComponentAttributes(decryptStoredWindowBrowseFolderChooser, 300, 170, 600, 400,
                Color.gray, Color.black, "Calibri", Font.PLAIN, 18, false, null);
        decryptStoredWindowBrowseFolderChooser.setPreferredSize(new Dimension(
                (int)(600 * resolutionScalingFactorX * resolutionScalingFactorX),
                (int)(400 * resolutionScalingFactorY * resolutionScalingFactorY)));
        decryptStoredWindowBrowseFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        decryptStoredWindowBrowseFolderChooser.setApproveButtonText("Select");
        decryptStoredWindowBrowseFolderChooser.setDialogTitle("Select folder to store file");

        decryptStoredWindowFolderNotFoundError = new JLabel("<html>The folder does not exist or is currently " +
                "inaccessible.</html>", SwingConstants.LEFT);
        decryptStoredWindowFolderNotFoundError.setVerticalAlignment(SwingConstants.CENTER);
        setComponentAttributes(decryptStoredWindowFolderNotFoundError, 200, 235, 400, 30,
                Color.gray, Color.red, "Calibri", Font.PLAIN, 14, false, null);
    }

    /**
     * Creates the recent window components
     */
    private void initializeRecentWindowComponents()
    {
        recentWindowCancelButton = new JButton("<html><center>Go back</center></html>");
        setComponentAttributes(recentWindowCancelButton, 50, 400, 120, 50, Color.gray,
                Color.black, "Calibri", Font.PLAIN, 18, false, false);
        recentWindowCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recentWindowCancelButtonClicked();
            }
        });

        String[] columns = {"File name", "Location", "Last modified"};
        //String[][] test = {{"test", "test", "test"}, {"test", "test", "test"}};
        recentWindowTableModel = new DefaultTableModel(new String[0][0], columns);
        recentWindowTable = new JTable(recentWindowTableModel);
        recentWindowTable.setBounds((int)(50 * resolutionScalingFactorX), (int)(125 * resolutionScalingFactorY),
                (int)(700 * resolutionScalingFactorX), (int)(250 * resolutionScalingFactorY));
        recentWindowTable.setBackground(Color.gray);
        recentWindowTable.setForeground(Color.black);
        recentWindowTable.setDefaultEditor(Object.class, null);
        recentWindowTable.setGridColor(Color.lightGray);
        recentWindowTable.setShowGrid(true);
        recentWindowTable.setFont(new Font("Calibri", Font.PLAIN,
                (int)(12 * (16 * windowScalingFactorX + 9 * windowScalingFactorY) / 25 *
                        (16 * resolutionScalingFactorX + 9 * resolutionScalingFactorY) / 25)));
        recentWindowTable.getTableHeader().setOpaque(false);
        recentWindowTable.getTableHeader().setBackground(Color.lightGray);
        recentWindowTable.getTableHeader().setReorderingAllowed(false);
        //recentWindowTable.getTableHeader().setResizingAllowed(false);
        ((DefaultTableCellRenderer)recentWindowTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
                SwingConstants.CENTER);
        recentWindowTable.setVisible(false);

        recentWindowTableContainer = new JScrollPane(recentWindowTable);
        recentWindowTableContainer.setBounds((int)(50 * resolutionScalingFactorX), (int)(125 * resolutionScalingFactorY),
                (int)(702 * resolutionScalingFactorX), (int)(250 * resolutionScalingFactorY));
        recentWindowTableContainer.getViewport().setBackground(Color.lightGray);
        recentWindowTableContainer.setForeground(Color.black);
        recentWindowTableContainer.setFont(new Font("Calibri", Font.PLAIN, (int)(14 * (resolutionScalingFactorX + resolutionScalingFactorY) / 2)));
        add(recentWindowTableContainer);
        recentWindowTableContainer.setVisible(false);

        recentWindowTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int totalTableWidth = recentWindowTable.getWidth();
        recentWindowTable.getColumn("File name").setPreferredWidth((int)(totalTableWidth * 0.3));
        recentWindowTable.getColumn("Location").setPreferredWidth((int)(totalTableWidth * 0.5));
        recentWindowTable.getColumn("Last modified").setPreferredWidth((int)(totalTableWidth * 0.2));
        recentWindowTable.setRowHeight((int)(30 * windowScalingFactorX * resolutionScalingFactorX));
        recentWindowTable.getTableHeader().setFont(new Font("Calibri", Font.PLAIN,
                (int)(14 * (16 * windowScalingFactorX + 9 * windowScalingFactorY) / 25 *
                        (16 * resolutionScalingFactorX + 9 * resolutionScalingFactorY) / 25)));
        recentWindowTable.getTableHeader().setPreferredSize(new Dimension(
                recentWindowTableContainer.getWidth(), (int)(20 * windowScalingFactorX * resolutionScalingFactorX)));
    }

    /**
     * Switches the app from main window to encrypt window
     */
    private void mainWindowEncryptButtonClicked()
    {
        mainWindowEncryptButton.setVisible(false);
        mainWindowDecryptButton.setVisible(false);
        mainWindowEncryptStoreButton.setVisible(false);
        mainWindowDecryptStoredButton.setVisible(false);
        mainWindowRecentHistoryButton.setVisible(false);
        differentWindowSubHeader.setText("> Encrypt a file");

        encryptWindowCancelButton.setVisible(true);
        encryptWindowStartButton.setVisible(true);
        encryptWindowEnterFileLocation.setVisible(true);
        encryptWindowEnterFileLocationText.setText("");
        encryptWindowEnterFileLocationText.setVisible(true);
        encryptWindowFileBrowserButton.setVisible(true);
        encryptWindowEnterKey.setVisible(true);
        encryptWindowUserGivenKeyOption.setSelected(true);
        encryptWindowUserGivenKeyOption.setVisible(true);
        encryptWindowRandomKeyOption.setSelected(false);
        encryptWindowRandomKeyOption.setVisible(true);
        encryptWindowEnterKeyText.setText("");
        encryptWindowEnterKeyText.setVisible(true);
    }

    /**
     * Switches the app from main window to decrypt window
     */
    private void mainWindowDecryptButtonClicked()
    {
        mainWindowEncryptButton.setVisible(false);
        mainWindowDecryptButton.setVisible(false);
        mainWindowEncryptStoreButton.setVisible(false);
        mainWindowDecryptStoredButton.setVisible(false);
        mainWindowRecentHistoryButton.setVisible(false);
        differentWindowSubHeader.setText("> Decrypt a file");

        decryptWindowCancelButton.setVisible(true);
        decryptWindowStartButton.setVisible(true);
        decryptWindowTryStoredKeysButton.setVisible(true);
        decryptWindowEnterFileLocation.setVisible(true);
        decryptWindowEnterFileLocationText.setText("");
        decryptWindowFileBrowserButton.setVisible(true);
        decryptWindowEnterFileLocationText.setVisible(true);
        decryptWindowEnterKey.setText("Enter valid key:");
        decryptWindowEnterKey.setVisible(true);
        decryptWindowEnterKeyText.setText("");
        decryptWindowForgotKeyButton.setVisible(true);
        decryptWindowEnterKeyText.setVisible(true);
    }

    /**
     * Switches the app from main window to encrypt and store window
     */
    private void mainWindowEncryptStoreButtonClicked()
    {
        mainWindowEncryptButton.setVisible(false);
        mainWindowDecryptButton.setVisible(false);
        mainWindowEncryptStoreButton.setVisible(false);
        mainWindowDecryptStoredButton.setVisible(false);
        mainWindowRecentHistoryButton.setVisible(false);
        differentWindowSubHeader.setText("> Encrypt and store a file");

        encryptStoreWindowCancelButton.setVisible(true);
        encryptStoreWindowStartButton.setVisible(true);
        encryptStoreWindowEnterFileLocation.setVisible(true);
        encryptStoreWindowEnterFileLocationText.setText("");
        encryptStoreWindowEnterFileLocationText.setVisible(true);
        encryptStoreWindowBrowseFileButton.setVisible(true);
        encryptStoreWindowEnterKey.setVisible(true);
        encryptStoreWindowCustomKeyButton.setSelected(true);
        encryptStoreWindowCustomKeyButton.setVisible(true);
        encryptStoreWindowRandomKeyButton.setSelected(false);
        encryptStoreWindowRandomKeyButton.setVisible(true);
        encryptStoreWindowEnterKeyText.setText("");
        encryptStoreWindowEnterKeyText.setVisible(true);
    }

    /**
     * Switches the app from main window to decrypt from stored window
     */
    private void mainWindowDecryptStoredButtonClicked()
    {
        String password = JOptionPane.showInputDialog(this, "Enter the password to access stored files:",
                "Password", JOptionPane.QUESTION_MESSAGE);
        if(password == null)
        {
            return;
        }
        if(!password.equals(KeyManager.getSecretPassword()))
        {
            JOptionPane.showMessageDialog(this, "The entered password is wrong. Stored files " +
                    "cannot be accessed.", "Wrong password", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] files = FileManager.retrieveStoredFiles();
        if(files.length == 0)
        {
            JOptionPane.showMessageDialog(this, "No previously stored files were found.",
                    "No files", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainWindowEncryptButton.setVisible(false);
        mainWindowDecryptButton.setVisible(false);
        mainWindowEncryptStoreButton.setVisible(false);
        mainWindowDecryptStoredButton.setVisible(false);
        mainWindowRecentHistoryButton.setVisible(false);
        differentWindowSubHeader.setText("> Decrypt a stored file");

        decryptStoredWindowChooseFileList.removeAllItems();
        for(String file : files)
        {
            decryptStoredWindowChooseFileList.addItem(file);
        }

        decryptStoredWindowChooseFile.setVisible(true);
        decryptStoredWindowChooseFileList.setVisible(true);
        decryptStoredWindowEnterFolderLocation.setVisible(true);
        decryptStoredWindowEnterFolderLocationText.setText("");
        decryptStoredWindowEnterFolderLocationText.setBackground(Color.white);
        decryptStoredWindowEnterFolderLocationText.setVisible(true);
        decryptStoredWindowBrowseFolderButton.setVisible(true);
        decryptStoredWindowEnterKey.setVisible(true);
        decryptStoredWindowEnterKeyText.setText("");
        decryptStoredWindowEnterKeyText.setBackground(Color.white);
        decryptStoredWindowEnterKeyText.setVisible(true);
        decryptStoredWindowCancelButton.setVisible(true);
        decryptStoredWindowStartButton.setVisible(true);
        decryptStoredWindowTryAllButton.setVisible(true);
    }

    /**
     * Switches the app from main window to recent history window
     */
    private void mainWindowRecentButtonClicked()
    {
        String[][] recentHistoryData = RecentHistoryManager.getRecentHistory();
        if(recentHistoryData.length == 0)
        {
            JOptionPane.showMessageDialog(this, "No recent history was found.\n\nOnly history " +
                    "for the last " + RecentHistoryManager.getMaxRecentHistoryTimeInDays() + " days is stored.",
                    "No history found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for(int i = recentWindowTableModel.getRowCount() - 1; i >= 0; i--)
        {
            recentWindowTableModel.removeRow(i);
        }

        Date currentDate = new Date();
        for(int i = recentHistoryData.length - 1; i >= 0; i--)
        {
            String[] rowData = new String[3];

            rowData[0] = recentHistoryData[i][0];
            rowData[1] = recentHistoryData[i][1];

            String lastModified;
            Date entryDate;
            try
            {
                entryDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(recentHistoryData[i][3]);
            }
            catch(Exception error)
            {
                continue;
            }
            long differenceInMilliseconds = currentDate.getTime() - entryDate.getTime();
            long differenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMilliseconds);
            if(differenceInSeconds >= 60)
            {
                long differenceInMinutes = TimeUnit.SECONDS.toMinutes(differenceInSeconds);
                if(differenceInMinutes >= 60)
                {
                    long differenceInHours = TimeUnit.MINUTES.toHours(differenceInMinutes);
                    if(differenceInHours >= 24)
                    {
                        long differenceInDays = TimeUnit.HOURS.toDays(differenceInHours);
                        lastModified = differenceInDays + " day(s) ago";
                    }
                    else
                    {
                        lastModified = differenceInHours + " hour(s) ago";
                    }
                }
                else
                {
                    lastModified = differenceInMinutes + " minute(s) ago";
                }
            }
            else
            {
                lastModified = differenceInSeconds + " second(s) ago";
            }

            rowData[2] = recentHistoryData[i][2] + " " + lastModified;

            recentWindowTableModel.addRow(rowData);
        }

        if(recentWindowTableModel.getRowCount() == 0)
        {
            JOptionPane.showMessageDialog(this, "No recent history was found.\n\nOnly history " +
                            "for the last " + RecentHistoryManager.getMaxRecentHistoryTimeInDays() + " days is stored.",
                    "No history found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        mainWindowEncryptButton.setVisible(false);
        mainWindowDecryptButton.setVisible(false);
        mainWindowEncryptStoreButton.setVisible(false);
        mainWindowDecryptStoredButton.setVisible(false);
        mainWindowRecentHistoryButton.setVisible(false);
        differentWindowSubHeader.setText("> Recent history");

        recentWindowCancelButton.setVisible(true);
        recentWindowTableContainer.setVisible(true);
        recentWindowTable.setVisible(true);
    }

    /**
     * Switches the app from recent history window to main window
     */
    private void recentWindowCancelButtonClicked()
    {
        recentWindowCancelButton.setVisible(false);
        recentWindowTable.setVisible(false);
        recentWindowTableContainer.setVisible(false);

        differentWindowSubHeader.setText("");
        mainWindowEncryptButton.setVisible(true);
        mainWindowDecryptButton.setVisible(true);
        mainWindowEncryptStoreButton.setVisible(true);
        mainWindowDecryptStoredButton.setVisible(true);
        mainWindowRecentHistoryButton.setVisible(true);
    }

    /**
     * Switches the app from decrypt window to main window
     */
    private void decryptWindowCancelButtonClicked()
    {
        decryptWindowCancelButton.setVisible(false);
        decryptWindowStartButton.setVisible(false);
        decryptWindowTryStoredKeysButton.setVisible(false);
        decryptWindowEnterFileLocation.setVisible(false);
        decryptWindowEnterFileLocationText.setVisible(false);
        decryptWindowFileLocationNotFoundError.setVisible(false);
        decryptWindowFileLocationIOError.setVisible(false);
        decryptWindowEnterFileLocationText.setBackground(Color.white);
        decryptWindowFileBrowserButton.setVisible(false);
        decryptWindowEnterKey.setVisible(false);
        decryptWindowEnterKeyText.setVisible(false);
        decryptWindowKeyInvalidError.setVisible(false);
        decryptWindowEnterKeyText.setBackground(Color.white);
        decryptWindowForgotKeyButton.setVisible(false);
        decryptWindowForgotKeyList.setVisible(false);
        decryptWindowCustomKeyButton.setVisible(false);
        decryptWindowWrongPasswordError.setVisible(false);
        decryptWindowNoStoredKeysError.setVisible(false);
        decryptWindowWrongKeyChosenError.setVisible(false);

        differentWindowSubHeader.setText("");
        mainWindowEncryptButton.setVisible(true);
        mainWindowDecryptButton.setVisible(true);
        mainWindowEncryptStoreButton.setVisible(true);
        mainWindowDecryptStoredButton.setVisible(true);
        mainWindowRecentHistoryButton.setVisible(true);
    }

    /**
     * Switches the app from encrypt window to main window
     */
    private void encryptWindowCancelButtonClicked()
    {
        encryptWindowCancelButton.setVisible(false);
        encryptWindowStartButton.setVisible(false);
        encryptWindowEnterFileLocation.setVisible(false);
        encryptWindowEnterFileLocationText.setVisible(false);
        encryptWindowFileLocationIOError.setVisible(false);
        encryptWindowFileLocationNotFoundError.setVisible(false);
        encryptWindowEnterFileLocationText.setBackground(Color.white);
        encryptWindowFileBrowserButton.setVisible(false);
        encryptWindowEnterKey.setVisible(false);
        encryptWindowUserGivenKeyOption.setVisible(false);
        encryptWindowRandomKeyOption.setVisible(false);
        encryptWindowEnterKeyText.setVisible(false);
        encryptWindowKeyInvalidError.setVisible(false);
        encryptWindowEnterKeyText.setBackground(Color.white);

        differentWindowSubHeader.setText("");
        mainWindowEncryptButton.setVisible(true);
        mainWindowDecryptButton.setVisible(true);
        mainWindowEncryptStoreButton.setVisible(true);
        mainWindowDecryptStoredButton.setVisible(true);
        mainWindowRecentHistoryButton.setVisible(true);
    }

    /**
     * Switches the app from encrypt and store window to main window
     */
    private void encryptStoreWindowCancelButtonClicked()
    {
        encryptStoreWindowCancelButton.setVisible(false);
        encryptStoreWindowStartButton.setVisible(false);
        encryptStoreWindowEnterFileLocation.setVisible(false);
        encryptStoreWindowEnterFileLocationText.setVisible(false);
        encryptStoreWindowFileNotFoundError.setVisible(false);
        encryptStoreWindowFileIOError.setVisible(false);
        encryptStoreWindowEnterFileLocationText.setBackground(Color.white);
        encryptStoreWindowBrowseFileButton.setVisible(false);
        encryptStoreWindowEnterKey.setVisible(false);
        encryptStoreWindowCustomKeyButton.setVisible(false);
        encryptStoreWindowRandomKeyButton.setVisible(false);
        encryptStoreWindowEnterKeyText.setVisible(false);
        encryptStoreWindowKeyInvalidError.setVisible(false);
        encryptStoreWindowEnterKeyText.setBackground(Color.white);

        differentWindowSubHeader.setText("");
        mainWindowEncryptButton.setVisible(true);
        mainWindowDecryptButton.setVisible(true);
        mainWindowEncryptStoreButton.setVisible(true);
        mainWindowDecryptStoredButton.setVisible(true);
        mainWindowRecentHistoryButton.setVisible(true);
    }

    /**
     * Switches the app from decrypt from stored window to main window
     */
    private void decryptStoredWindowCancelButtonClicked()
    {
        decryptStoredWindowChooseFile.setVisible(false);
        decryptStoredWindowChooseFileList.setVisible(false);
        decryptStoredWindowEnterFolderLocation.setVisible(false);
        decryptStoredWindowFolderNotFoundError.setVisible(false);
        decryptStoredWindowEnterFolderLocationText.setBackground(Color.white);
        decryptStoredWindowEnterFolderLocationText.setVisible(false);
        decryptStoredWindowBrowseFolderButton.setVisible(false);
        decryptStoredWindowEnterKey.setVisible(false);
        decryptStoredWindowInvalidKeyError.setVisible(false);
        decryptStoredWindowEnterKeyText.setBackground(Color.white);
        decryptStoredWindowEnterKeyText.setVisible(false);
        decryptStoredWindowCancelButton.setVisible(false);
        decryptStoredWindowStartButton.setVisible(false);
        decryptStoredWindowTryAllButton.setVisible(false);

        differentWindowSubHeader.setText("");
        mainWindowEncryptButton.setVisible(true);
        mainWindowDecryptButton.setVisible(true);
        mainWindowEncryptStoreButton.setVisible(true);
        mainWindowDecryptStoredButton.setVisible(true);
        mainWindowRecentHistoryButton.setVisible(true);
    }

    /**
     * Opens the file browser window while in the decrypt window
     */
    private void decryptWindowBrowseFileButtonClicked()
    {
        decryptWindowFileBrowser.setSelectedFile(new File(""));
        decryptWindowFileBrowser.setCurrentDirectory(null);
        decryptWindowFileBrowser.setVisible(true);
        int fileChooseResult = decryptWindowFileBrowser.showOpenDialog(this);
        if(fileChooseResult == JFileChooser.APPROVE_OPTION)
        {
            decryptWindowEnterFileLocationText.setText(decryptWindowFileBrowser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Opens the file browser window while in the encrypt window
     */
    private void encryptWindowBrowseFileButtonClicked()
    {
        encryptWindowFileBrowser.setSelectedFile(new File(""));
        encryptWindowFileBrowser.setCurrentDirectory(null);
        encryptWindowFileBrowser.setVisible(true);
        int fileChooseResult = encryptWindowFileBrowser.showOpenDialog(this);
        if(fileChooseResult == JFileChooser.APPROVE_OPTION)
        {
            encryptWindowEnterFileLocationText.setText(encryptWindowFileBrowser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Opens the file browser window while in the encrypt and store window
     */
    private void encryptStoreWindowBrowseFileButtonClicked()
    {
        encryptStoreWindowBrowseFileMenu.setSelectedFile(new File(""));
        encryptStoreWindowBrowseFileMenu.setCurrentDirectory(null);
        encryptStoreWindowBrowseFileMenu.setVisible(true);
        int fileChooseResult = encryptStoreWindowBrowseFileMenu.showOpenDialog(this);
        if(fileChooseResult == JFileChooser.APPROVE_OPTION)
        {
            encryptStoreWindowEnterFileLocationText.setText(
                    encryptStoreWindowBrowseFileMenu.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Switches encryption to custom user key option in the encrypt window
     */
    private void encryptWindowCustomKeyButtonSelected()
    {
        encryptWindowUserGivenKeyOption.setSelected(true);
        encryptWindowEnterKeyText.setVisible(true);
        encryptWindowRandomKeyOption.setSelected(false);
    }

    /**
     * Switches encryption to random key option in the encrypt window
     */
    private void encryptWindowRandomKeyButtonSelected()
    {
        encryptWindowRandomKeyOption.setSelected(true);
        encryptWindowEnterKeyText.setVisible(false);
        encryptWindowUserGivenKeyOption.setSelected(false);
        encryptWindowKeyInvalidError.setVisible(false);
        encryptWindowEnterKeyText.setBackground(Color.white);
    }

    /**
     * Switches encryption to custom user key option in the encrypt and store window
     */
    private void encryptStoreWindowCustomKeyButtonSelected()
    {
        encryptStoreWindowCustomKeyButton.setSelected(true);
        encryptStoreWindowRandomKeyButton.setSelected(false);
        encryptStoreWindowEnterKeyText.setVisible(true);
    }

    /**
     * Switches encryption to random key option in the encrypt and store window
     */
    private void encryptStoreWindowRandomKeyButtonSelected()
    {
        encryptStoreWindowCustomKeyButton.setSelected(false);
        encryptStoreWindowRandomKeyButton.setSelected(true);
        encryptStoreWindowEnterKeyText.setVisible(false);
        encryptStoreWindowKeyInvalidError.setVisible(false);
        encryptStoreWindowEnterKeyText.setBackground(Color.white);
    }

    /**
     * Starts encrypting process in the encrypt window
     */
    private void encryptWindowStartButtonClicked()
    {
        SwingWorker<Void, Void> encryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                encryptWindowFileLocationNotFoundError.setVisible(false);
                encryptWindowFileLocationIOError.setVisible(false);
                encryptWindowEnterFileLocationText.setBackground(Color.white);
                encryptWindowKeyInvalidError.setVisible(false);
                encryptWindowEnterKeyText.setBackground(Color.white);

                String fileName = encryptWindowEnterFileLocationText.getText();
                boolean isFileValid = FileNameValidator.isFileValid(fileName);

                String key = null;
                boolean isKeyValid = false;
                if(encryptWindowUserGivenKeyOption.isSelected())
                {
                    key = encryptWindowEnterKeyText.getText();
                    isKeyValid = KeyValidator.isKeyValid(key);
                }

                if(isFileValid && (key == null || isKeyValid))
                {
                    try
                    {
                        encryptWindowCancelButton.setVisible(false);
                        encryptWindowStartButton.setVisible(false);
                        encryptWindowEncryptingText.setVisible(true);

                        if(key != null)
                        {
                            Main.encrypt(fileName, key);
                            String message = "The file has been successfully encrypted with this user given key:\n" + key +
                                    "\nRemember this key as the encrypted file cannot be decrypted without this exact key.";
                            JOptionPane.showMessageDialog(getWindowReference(), message, "Encryption Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            encryptWindowEnterFileLocationText.setText("");
                            encryptWindowEnterKeyText.setText("");
                        }
                        else
                        {
                            String randomKey = Main.encrypt(fileName);
                            String message = "The file has been successfully encrypted with this random encryption key:\n"
                                    + randomKey + "\nWrite this down somewhere as the encrypted file cannot be decrypted " +
                                    "without this exact key.";
                            JOptionPane.showMessageDialog(getWindowReference(), message, "Encryption Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            encryptWindowEnterFileLocationText.setText("");
                        }
                    }
                    catch(FileNotFoundException | NullPointerException fileError)
                    {
                        encryptionFileNotFoundError();
                    }
                    catch(IOException | SecurityException fileError)
                    {
                        encryptionFileIOError();
                    }
                    finally
                    {
                        encryptWindowCancelButton.setVisible(true);
                        encryptWindowStartButton.setVisible(true);
                        encryptWindowEncryptingText.setVisible(false);
                    }
                }
                else
                {
                    if(!isFileValid)
                    {
                        encryptionFileNotFoundError();
                    }

                    if(key != null && !isKeyValid)
                    {
                        encryptionKeyInvalidError();
                    }
                }
                return null;
            }
        };
        encryptWorker.execute();
    }

    /**
     * Shows invalid file name error in the encrypt window
     */
    private void encryptionFileNotFoundError()
    {
        encryptWindowFileLocationNotFoundError.setVisible(true);
        encryptWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows file access error in the encrypt window
     */
    private void encryptionFileIOError()
    {
        encryptWindowFileLocationIOError.setVisible(true);
        encryptWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows invalid key error in the encrypt window
     */
    private void encryptionKeyInvalidError()
    {
        encryptWindowKeyInvalidError.setVisible(true);
        encryptWindowEnterKeyText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Removes the error from file location text field in the encrypt window
     */
    private void encryptWindowFileLocationTextUpdated()
    {
        encryptWindowFileLocationNotFoundError.setVisible(false);
        encryptWindowFileLocationIOError.setVisible(false);
        encryptWindowEnterFileLocationText.setBackground(Color.white);
    }

    /**
     * Removes the error from key text field in the encrypt window
     */
    private void encryptWindowKeyTextUpdated()
    {
        encryptWindowKeyInvalidError.setVisible(false);
        encryptWindowEnterKeyText.setBackground(Color.white);
    }

    /**
     * Starts decrypting process in the decrypt window
     */
    private void decryptWindowStartButtonClicked()
    {
        SwingWorker<Void, Void> decryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                decryptWindowFileLocationNotFoundError.setVisible(false);
                decryptWindowFileLocationIOError.setVisible(false);
                decryptWindowEnterFileLocationText.setBackground(Color.white);
                decryptWindowKeyInvalidError.setVisible(false);
                decryptWindowEnterKeyText.setBackground(Color.white);
                decryptWindowNoStoredKeysError.setVisible(false);
                decryptWindowWrongPasswordError.setVisible(false);
                decryptWindowWrongKeyChosenError.setVisible(false);
                decryptWindowForgotKeyList.setBorder(null);
                decryptWindowForgotKeyList.setBackground(Color.white);

                String fileName = decryptWindowEnterFileLocationText.getText();
                boolean isFileValid = FileNameValidator.isFileValid(fileName);

                String key = null;
                if(decryptWindowForgotKeyButton.isVisible())
                {
                    key = decryptWindowEnterKeyText.getText();
                }
                else if(decryptWindowCustomKeyButton.isVisible())
                {
                    key = (String) decryptWindowForgotKeyList.getSelectedItem();
                }
                boolean isKeyValid = KeyValidator.isKeyValid(key);

                if(isFileValid && isKeyValid)
                {
                    try
                    {
                        decryptWindowCancelButton.setVisible(false);
                        decryptWindowStartButton.setVisible(false);
                        decryptWindowTryStoredKeysButton.setVisible(false);
                        decryptWindowDecryptingText.setVisible(true);

                        Main.decrypt(fileName, key);

                        JOptionPane.showMessageDialog(getWindowReference(), "The file has been successfully decrypted.",
                                "Decryption successful", JOptionPane.INFORMATION_MESSAGE);
                        decryptWindowEnterFileLocationText.setText("");
                        decryptWindowEnterKeyText.setText("");
                    }
                    catch(FileNotFoundException | NullPointerException fileError)
                    {
                        decryptionFileNotFoundError();
                    }
                    catch(IOException | SecurityException fileError)
                    {
                        decryptionFileIOError();
                    }
                    catch(InvalidKeyException keyError)
                    {
                        if(decryptWindowForgotKeyButton.isVisible())
                        {
                            decryptionKeyInvalidError();
                        }
                        else if(decryptWindowCustomKeyButton.isVisible())
                        {
                            decryptionWrongKeyChosenError();
                        }
                    }
                    finally
                    {
                        decryptWindowCancelButton.setVisible(true);
                        decryptWindowStartButton.setVisible(true);
                        decryptWindowTryStoredKeysButton.setVisible(true);
                        decryptWindowDecryptingText.setVisible(false);
                    }
                }
                else
                {
                    if(!isFileValid)
                    {
                        decryptionFileNotFoundError();
                    }

                    if(!isKeyValid)
                    {
                        decryptionKeyInvalidError();
                    }
                }
                return null;
            }
        };
        decryptWorker.execute();
    }

    /**
     * Starts decrypting process using all the saved keys in the decrypt window
     */
    private void decryptWindowTryStoredKeysButtonClicked()
    {
        SwingWorker<Void, Void> decryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                decryptWindowFileLocationNotFoundError.setVisible(false);
                decryptWindowFileLocationIOError.setVisible(false);
                decryptWindowEnterFileLocationText.setBackground(Color.white);
                decryptWindowKeyInvalidError.setVisible(false);
                decryptWindowEnterKeyText.setBackground(Color.white);
                decryptWindowNoStoredKeysError.setVisible(false);
                decryptWindowWrongPasswordError.setVisible(false);
                decryptWindowWrongKeyChosenError.setVisible(false);
                decryptWindowForgotKeyList.setBorder(null);
                decryptWindowForgotKeyList.setBackground(Color.white);

                String fileName = decryptWindowEnterFileLocationText.getText();
                boolean isFileValid = FileNameValidator.isFileValid(fileName);
                if(!isFileValid)
                {
                    decryptionFileNotFoundError();
                    return null;
                }

                String password = JOptionPane.showInputDialog(getWindowReference(), "Enter the password to access stored keys: ",
                        "Password", JOptionPane.QUESTION_MESSAGE);
                if(password == null)
                {
                    return null;
                }
                else if(!password.equals(KeyManager.getSecretPassword()))
                {
                    JOptionPane.showMessageDialog(getWindowReference(), "The entered password is wrong. Stored keys " +
                            "cannot be accessed.", "Wrong password", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                decryptWindowCancelButton.setVisible(false);
                decryptWindowTryStoredKeysButton.setVisible(false);
                decryptWindowStartButton.setVisible(false);
                decryptWindowDecryptingText.setVisible(true);

                String[] storedKeys = KeyManager.getStoredKeys();
                if(storedKeys.length == 0)
                {
                    JOptionPane.showMessageDialog(getWindowReference(), "No stored keys were found.", "No keys",
                            JOptionPane.ERROR_MESSAGE);
                    decryptWindowCancelButton.setVisible(true);
                    decryptWindowTryStoredKeysButton.setVisible(true);
                    decryptWindowStartButton.setVisible(true);
                    decryptWindowDecryptingText.setVisible(false);
                    return null;
                }

                for(String key : storedKeys)
                {
                    try
                    {
                        Decryptor decryptObject = new Decryptor(fileName, key);
                        decryptObject.startDecrypting();

                        RecentHistoryManager.addRecentHistory(fileName, RecentHistoryManager.DECRYPTION);

                        JOptionPane.showMessageDialog(getWindowReference(), "The file has been successfully decrypted " +
                                "with the key:\n" + key, "Decryption Successful", JOptionPane.INFORMATION_MESSAGE);

                        decryptWindowCancelButton.setVisible(true);
                        decryptWindowTryStoredKeysButton.setVisible(true);
                        decryptWindowStartButton.setVisible(true);
                        decryptWindowDecryptingText.setVisible(false);

                        decryptWindowEnterKeyText.setText("");
                        decryptWindowEnterFileLocationText.setText("");

                        return null;
                    }
                    catch(Exception decryptError)
                    {

                    }
                }

                JOptionPane.showMessageDialog(getWindowReference(), "The file could not be decrypted with any of the " +
                        "stored keys.", "Decryption Unsuccessful", JOptionPane.ERROR_MESSAGE);

                decryptWindowCancelButton.setVisible(true);
                decryptWindowTryStoredKeysButton.setVisible(true);
                decryptWindowStartButton.setVisible(true);
                decryptWindowDecryptingText.setVisible(false);
                return null;
            }
        };
        decryptWorker.execute();
    }

    /**
     * Shows invalid file name error in the decrypt window
     */
    private void decryptionFileNotFoundError()
    {
        decryptWindowFileLocationNotFoundError.setVisible(true);
        decryptWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows file access error in the decrypt window
     */
    private void decryptionFileIOError()
    {
        decryptWindowFileLocationIOError.setVisible(true);
        decryptWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows invalid key error in the key textfield in the decrypt window
     */
    private void decryptionKeyInvalidError()
    {
        decryptWindowKeyInvalidError.setVisible(true);
        decryptWindowEnterKeyText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows invalid key error in the saved keys list in the decrypt window
     */
    private void decryptionWrongKeyChosenError()
    {
        decryptWindowWrongKeyChosenError.setVisible(true);
        decryptWindowForgotKeyList.setBorder(BorderFactory.createLineBorder(new Color(255, 189, 189), 4));
        //decryptWindowForgotKeyList.setBackground(new Color(255, 189, 189));
    }

    /**
     * Removes the error from file location text field in the decrypt window
     */
    private void decryptWindowFileLocationTextUpdated()
    {
        decryptWindowFileLocationNotFoundError.setVisible(false);
        decryptWindowFileLocationIOError.setVisible(false);
        decryptWindowEnterFileLocationText.setBackground(Color.white);
    }

    /**
     * Removes the error from key text field in the decrypt window
     */
    private void decryptWindowKeyTextUpdated()
    {
        decryptWindowKeyInvalidError.setVisible(false);
        decryptWindowEnterKeyText.setBackground(Color.white);

        decryptWindowWrongPasswordError.setVisible(false);
        decryptWindowNoStoredKeysError.setVisible(false);
    }

    /**
     * Removes the error from saved keys list in the decrypt window
     */
    private void decryptWindowStoredKeysListClicked()
    {
        decryptWindowWrongKeyChosenError.setVisible(false);
        decryptWindowForgotKeyList.setBorder(null);
        //decryptWindowForgotKeyList.setBackground(Color.white);
    }

    /**
     * Switches key text field with saved keys list in the decrypt window
     */
    private void decryptWindowForgotKeyButtonClicked()
    {
        decryptWindowKeyInvalidError.setVisible(false);
        decryptWindowEnterKeyText.setBackground(Color.white);
        decryptWindowWrongPasswordError.setVisible(false);
        decryptWindowNoStoredKeysError.setVisible(false);

        String password = JOptionPane.showInputDialog(this, "Enter the password to access stored keys: ",
                "Password", JOptionPane.QUESTION_MESSAGE);
        if(password == null)
        {
            return;
        }
        if(!password.equals(KeyManager.getSecretPassword()))
        {
            JOptionPane.showMessageDialog(this, "The entered password is wrong. Stored keys " +
                    "cannot be accessed.", "Wrong password", JOptionPane.ERROR_MESSAGE);
            //decryptWindowWrongPasswordError.setVisible(true);
            return;
        }

        String[] keys = KeyManager.getStoredKeys();
        if(keys.length == 0)
        {
            JOptionPane.showMessageDialog(this, "No previous stored keys were found.",
                    "No keys", JOptionPane.ERROR_MESSAGE);
            //decryptWindowNoStoredKeysError.setVisible(true);
            return;
        }
        decryptWindowForgotKeyList.removeAllItems();
        for(String key : keys)
        {
            decryptWindowForgotKeyList.addItem(key);
        }

        decryptWindowEnterKeyText.setVisible(false);
        decryptWindowEnterKeyText.setText("");
        decryptWindowForgotKeyButton.setVisible(false);

        decryptWindowEnterKey.setText("Choose correct key:");
        decryptWindowCustomKeyButton.setVisible(true);
        decryptWindowForgotKeyList.setVisible(true);
        decryptWindowForgotKeyList.setBorder(null);
        decryptWindowForgotKeyList.setBackground(Color.white);
    }

    /**
     * Switches saved keys list with key text field in the decrypt window
     */
    private void decryptWindowCustomKeyButtonClicked()
    {
        decryptWindowWrongKeyChosenError.setVisible(false);
        decryptWindowForgotKeyList.setBackground(Color.white);
        decryptWindowCustomKeyButton.setVisible(false);
        decryptWindowForgotKeyList.setVisible(false);

        decryptWindowEnterKey.setText("Enter valid key:");
        decryptWindowEnterKeyText.setVisible(true);
        decryptWindowEnterKeyText.setText("");
        decryptWindowForgotKeyButton.setVisible(true);
    }

    /**
     * Shows invalid file name error in the encrypt and store window
     */
    private void encryptionStoreWindowFileNotFoundError()
    {
        encryptStoreWindowFileNotFoundError.setVisible(true);
        encryptStoreWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows file access error in the encrypt and store window
     */
    private void encryptionStoreWindowFileIOError()
    {
        encryptStoreWindowFileIOError.setVisible(true);
        encryptStoreWindowEnterFileLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows invalid key error in the encrypt and store window
     */
    private void encryptionStoreWindowKeyInvalidError()
    {
        encryptStoreWindowKeyInvalidError.setVisible(true);
        encryptStoreWindowEnterKeyText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Removes the error from file location text field in the encrypt and store window
     */
    private void encryptStoreWindowFileTextUpdated()
    {
        encryptStoreWindowFileNotFoundError.setVisible(false);
        encryptStoreWindowFileIOError.setVisible(false);
        encryptStoreWindowEnterFileLocationText.setBackground(Color.white);
    }

    /**
     * Removes the error from key text field in the encrypt and store window
     */
    private void encryptStoreWindowKeyTextUpdated()
    {
        encryptStoreWindowKeyInvalidError.setVisible(false);
        encryptStoreWindowEnterKeyText.setBackground(Color.white);
    }

    /**
     * Starts encryption and storing process in the encrypt and store window
     */
    private void encryptStoreWindowStartButtonClicked()
    {
        SwingWorker<Void, Void> encryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                encryptStoreWindowFileNotFoundError.setVisible(false);
                encryptStoreWindowFileIOError.setVisible(false);
                encryptStoreWindowEnterFileLocationText.setBackground(Color.white);
                encryptStoreWindowKeyInvalidError.setVisible(false);
                encryptStoreWindowEnterKeyText.setBackground(Color.white);

                String fileName = encryptStoreWindowEnterFileLocationText.getText();
                boolean isFileValid = FileNameValidator.isFileValid(fileName);

                String key = null;
                boolean isKeyValid = false;
                if(encryptStoreWindowCustomKeyButton.isSelected())
                {
                    key = encryptStoreWindowEnterKeyText.getText();
                    isKeyValid = KeyValidator.isKeyValid(key);
                }

                if(isFileValid && (key == null || isKeyValid))
                {
                    try
                    {
                        encryptStoreWindowCancelButton.setVisible(false);
                        encryptStoreWindowStartButton.setVisible(false);
                        encryptStoreWindowEncryptingText.setVisible(true);

                        if(key != null)
                        {
                            Main.encryptAndStore(getWindowReference(), fileName, key);
                            String message = "The file has been stored and successfully encrypted with this user given key:\n"
                                    + key + "\nRemember this key as the encrypted file cannot be decrypted without this exact key.";
                            JOptionPane.showMessageDialog(getWindowReference(), message, "Encryption Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            encryptStoreWindowEnterFileLocationText.setText("");
                            encryptStoreWindowEnterKeyText.setText("");
                        }
                        else
                        {
                            String randomKey = Main.encryptAndStore(getWindowReference(), fileName);
                            String message = "The file has been stored and successfully encrypted with this random encryption key:\n"
                                    + randomKey + "\nWrite this down somewhere as the encrypted file cannot be decrypted " +
                                    "without this exact key.";
                            JOptionPane.showMessageDialog(getWindowReference(), message, "Encryption Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                            encryptStoreWindowEnterFileLocationText.setText("");
                        }
                    }
                    catch(FileNotFoundException | NullPointerException fileError)
                    {
                        encryptionStoreWindowFileNotFoundError();
                    }
                    catch(IOException | SecurityException fileError)
                    {
                        encryptionStoreWindowFileIOError();
                    }
                    catch(InvalidPathException operationAbortedError)
                    {
                        JOptionPane.showMessageDialog(getWindowReference(), "Encryption has been stopped and the file " +
                                "has been restored to its original form.", "Encryption aborted", JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally
                    {
                        encryptStoreWindowCancelButton.setVisible(true);
                        encryptStoreWindowStartButton.setVisible(true);
                        encryptStoreWindowEncryptingText.setVisible(false);
                    }
                }
                else
                {
                    if(!isFileValid)
                    {
                        encryptionStoreWindowFileNotFoundError();
                    }

                    if(key != null && !isKeyValid)
                    {
                        encryptionStoreWindowKeyInvalidError();
                    }
                }
                return null;
            }
        };
        encryptWorker.execute();
    }

    /**
     * Opens the folder browser window while in the decrypt from stored window
     */
    private void decryptStoredWindowBrowseFolderButtonClicked()
    {
        decryptStoredWindowBrowseFolderChooser.setSelectedFile(
                new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()));
        decryptStoredWindowBrowseFolderChooser.setCurrentDirectory(null);
        decryptStoredWindowBrowseFolderChooser.setVisible(true);
        int fileChooseResult = decryptStoredWindowBrowseFolderChooser.showOpenDialog(this);
        if(fileChooseResult == JFileChooser.APPROVE_OPTION)
        {
            decryptStoredWindowEnterFolderLocationText.setText(
                    decryptStoredWindowBrowseFolderChooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Removes the error from folder location text field in the decrypt from stored window
     */
    private void decryptStoredWindowFolderTextUpdated()
    {
        decryptStoredWindowFolderNotFoundError.setVisible(false);
        decryptStoredWindowEnterFolderLocationText.setBackground(Color.white);
    }

    /**
     * Removes the error from key text field in the decrypt from stored window
     */
    private void decryptStoredWindowKeyTextUpdated()
    {
        decryptStoredWindowInvalidKeyError.setVisible(false);
        decryptStoredWindowEnterKeyText.setBackground(Color.white);
    }

    /**
     * Starts decryption and storing process in the decrypt from stored window
     */
    private void decryptStoredWindowStartButtonClicked()
    {
        SwingWorker<Void, Void> decryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                decryptStoredWindowFolderNotFoundError.setVisible(false);
                decryptStoredWindowEnterFolderLocationText.setBackground(Color.white);
                decryptStoredWindowInvalidKeyError.setVisible(false);
                decryptStoredWindowEnterKeyText.setBackground(Color.white);

                String fileName = (String) decryptStoredWindowChooseFileList.getSelectedItem();
                String ENCRYPTED_FILES_FOLDER_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\File Encryptor" + "\\.files\\";
                fileName = ENCRYPTED_FILES_FOLDER_PATH + fileName;
                boolean isFileValid = FileNameValidator.isFileValid(fileName);

                String folderName = decryptStoredWindowEnterFolderLocationText.getText();
                boolean isFolderValid = FileNameValidator.isFolderValid(folderName);

                String key = decryptStoredWindowEnterKeyText.getText();
                boolean isKeyValid = KeyValidator.isKeyValid(key);

                if(isFileValid && isFolderValid && isKeyValid)
                {
                    try
                    {
                        decryptStoredWindowCancelButton.setVisible(false);
                        decryptStoredWindowStartButton.setVisible(false);
                        decryptStoredWindowTryAllButton.setVisible(false);
                        decryptStoredWindowDecryptingText.setVisible(true);

                        Main.decryptStored(getWindowReference(), fileName, folderName, key);

                        JOptionPane.showMessageDialog(getWindowReference(), "The file has been successfully decrypted " +
                                        "and stored to the given folder.", "Decryption successful",
                                JOptionPane.INFORMATION_MESSAGE);
                        String[] files = FileManager.retrieveStoredFiles();
                        if(files.length == 0)
                        {
                            JOptionPane.showMessageDialog(getWindowReference(), "No other previously stored files were " +
                                            "found.\n\nReturning to main window.", "No other stored files",
                                    JOptionPane.INFORMATION_MESSAGE);
                            decryptStoredWindowCancelButtonClicked();
                            return null;
                        }
                        decryptStoredWindowChooseFileList.removeAllItems();
                        for(String file : files)
                        {
                            decryptStoredWindowChooseFileList.addItem(file);
                        }
                        decryptStoredWindowEnterFolderLocationText.setText("");
                        decryptStoredWindowEnterKeyText.setText("");

                    }
                    catch(NullPointerException | IOException | SecurityException fileError)
                    {
                        decryptionStoredWindowFolderNotFoundError();
                    }
                    catch(InvalidPathException decryptionAborted)
                    {
                        JOptionPane.showMessageDialog(getWindowReference(), "Decryption has been stopped and the file " +
                                "has been restored to its original form.", "Decryption aborted", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch(InvalidKeyException keyError)
                    {
                        decryptionStoredWindowInvalidKeyError();
                    }
                    finally
                    {
                        if(!mainWindowDecryptStoredButton.isVisible())
                        {
                            decryptStoredWindowCancelButton.setVisible(true);
                            decryptStoredWindowStartButton.setVisible(true);
                            decryptStoredWindowTryAllButton.setVisible(true);
                        }
                        decryptStoredWindowDecryptingText.setVisible(false);
                    }
                }
                else
                {
                    if(!isFileValid)
                    {
                        JOptionPane.showMessageDialog(getWindowReference(), "The stored file that you are trying to " +
                                "decrypt does not exist anymore.\nThe file was removed unexpectedly.\n\nThe stored files will now" +
                                " be reloaded and updated.", "Unexpected stored file removal", JOptionPane.ERROR_MESSAGE);

                        String[] files = FileManager.retrieveStoredFiles();
                        if(files.length == 0)
                        {
                            JOptionPane.showMessageDialog(getWindowReference(), "No other stored files were found.\n\n" +
                                    "Returning to main window.", "No stored files", JOptionPane.INFORMATION_MESSAGE);
                            decryptStoredWindowCancelButtonClicked();
                            return null;
                        }

                        decryptStoredWindowChooseFileList.removeAllItems();
                        for(String file : files)
                        {
                            decryptStoredWindowChooseFileList.addItem(file);
                        }
                    }

                    if(!isFolderValid)
                    {
                        decryptionStoredWindowFolderNotFoundError();
                    }

                    if(!isKeyValid)
                    {
                        decryptionStoredWindowInvalidKeyError();
                    }
                }
                return null;
            }
        };
        decryptWorker.execute();
    }

    /**
     * Starts decryption and storing process using all the saved keys in the decrypt from stored window
     */
    private void decryptStoredWindowTryAllButtonClicked()
    {
        SwingWorker<Void, Void> decryptWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                decryptStoredWindowFolderNotFoundError.setVisible(false);
                decryptStoredWindowEnterFolderLocationText.setBackground(Color.white);
                decryptStoredWindowInvalidKeyError.setVisible(false);
                decryptStoredWindowEnterKeyText.setBackground(Color.white);

                String fileName = (String) decryptStoredWindowChooseFileList.getSelectedItem();
                String ENCRYPTED_FILES_FOLDER_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\File Encryptor" + "\\.files\\";
                fileName = ENCRYPTED_FILES_FOLDER_PATH + fileName;
                boolean isFileValid = FileNameValidator.isFileValid(fileName);

                String folderName = decryptStoredWindowEnterFolderLocationText.getText();
                boolean isFolderValid = FileNameValidator.isFolderValid(folderName);

                if(!isFolderValid)
                {
                    decryptionStoredWindowFolderNotFoundError();
                }

                if(!isFileValid)
                {
                    JOptionPane.showMessageDialog(getWindowReference(), "The stored file that you are trying to " +
                            "decrypt does not exist anymore.\nThe file was removed unexpectedly.\n\nThe stored files will now" +
                            " be reloaded and updated.", "Unexpected stored file removal", JOptionPane.ERROR_MESSAGE);

                    String[] files = FileManager.retrieveStoredFiles();
                    if(files.length == 0)
                    {
                        JOptionPane.showMessageDialog(getWindowReference(), "No other stored files were found.\n\n" +
                                "Returning to main window.", "No stored files", JOptionPane.INFORMATION_MESSAGE);
                        decryptStoredWindowCancelButtonClicked();
                        return null;
                    }

                    decryptStoredWindowChooseFileList.removeAllItems();
                    for(String file : files)
                    {
                        decryptStoredWindowChooseFileList.addItem(file);
                    }
                }

                if(!isFolderValid || !isFileValid)
                {
                    return null;
                }

                String password = JOptionPane.showInputDialog(getWindowReference(), "Enter the password to access stored keys: ",
                        "Password", JOptionPane.QUESTION_MESSAGE);
                if(password == null)
                {
                    return null;
                }
                else if(!password.equals(KeyManager.getSecretPassword()))
                {
                    JOptionPane.showMessageDialog(getWindowReference(), "The entered password is wrong. Stored keys " +
                            "cannot be accessed.", "Wrong password", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                decryptStoredWindowCancelButton.setVisible(false);
                decryptStoredWindowStartButton.setVisible(false);
                decryptStoredWindowTryAllButton.setVisible(false);
                decryptStoredWindowDecryptingText.setVisible(true);

                String[] storedKeys = KeyManager.getStoredKeys();
                if(storedKeys.length == 0)
                {
                    JOptionPane.showMessageDialog(getWindowReference(), "No stored keys were found.", "No keys",
                            JOptionPane.ERROR_MESSAGE);
                    decryptStoredWindowCancelButton.setVisible(true);
                    decryptStoredWindowStartButton.setVisible(true);
                    decryptStoredWindowTryAllButton.setVisible(true);
                    decryptStoredWindowDecryptingText.setVisible(false);
                    return null;
                }

                for(String key : storedKeys)
                {
                    try
                    {
                        Main.decryptStored(getWindowReference(), fileName, folderName, key);

                        JOptionPane.showMessageDialog(getWindowReference(), "The file has been successfully decrypted " +
                                        "with the key:\n" + key + "\nand stored in the given folder.", "Decryption Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        String[] files = FileManager.retrieveStoredFiles();
                        if(files.length == 0)
                        {
                            JOptionPane.showMessageDialog(getWindowReference(), "No other previously stored files were " +
                                            "found.\n\nReturning to main window.", "No other stored files",
                                    JOptionPane.INFORMATION_MESSAGE);
                            decryptStoredWindowCancelButtonClicked();
                            decryptStoredWindowDecryptingText.setVisible(false);
                            return null;
                        }
                        decryptStoredWindowChooseFileList.removeAllItems();
                        for(String file : files)
                        {
                            decryptStoredWindowChooseFileList.addItem(file);
                        }
                        decryptStoredWindowEnterFolderLocationText.setText("");
                        decryptStoredWindowEnterKeyText.setText("");

                        decryptStoredWindowCancelButton.setVisible(true);
                        decryptStoredWindowStartButton.setVisible(true);
                        decryptStoredWindowTryAllButton.setVisible(true);
                        decryptStoredWindowDecryptingText.setVisible(false);

                        return null;
                    }
                    catch(InvalidPathException operationAborted)
                    {
                        JOptionPane.showMessageDialog(getWindowReference(), "Decryption has been stopped and the file " +
                                "has been restored to its original form.", "Decryption aborted", JOptionPane.INFORMATION_MESSAGE);
                        decryptStoredWindowCancelButton.setVisible(true);
                        decryptStoredWindowStartButton.setVisible(true);
                        decryptStoredWindowTryAllButton.setVisible(true);
                        decryptStoredWindowDecryptingText.setVisible(false);
                        return null;
                    }
                    catch(Exception decryptError)
                    {

                    }
                }

                JOptionPane.showMessageDialog(getWindowReference(), "The file could not be decrypted with any of the " +
                        "stored keys.", "Decryption Unsuccessful", JOptionPane.ERROR_MESSAGE);

                decryptStoredWindowCancelButton.setVisible(true);
                decryptStoredWindowStartButton.setVisible(true);
                decryptStoredWindowTryAllButton.setVisible(true);
                decryptStoredWindowDecryptingText.setVisible(false);
                return null;
            }
        };
        decryptWorker.execute();
    }

    /**
     * Shows invalid folder name error in the decrypt from stored window
     */
    private void decryptionStoredWindowFolderNotFoundError()
    {
        decryptStoredWindowFolderNotFoundError.setVisible(true);
        decryptStoredWindowEnterFolderLocationText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Shows invalid key error in the decrypt from stored window
     */
    private void decryptionStoredWindowInvalidKeyError()
    {
        decryptStoredWindowInvalidKeyError.setVisible(true);
        decryptStoredWindowEnterKeyText.setBackground(new Color(255, 189, 189));
    }

    /**
     * Activates the highlighting effect on the button while mouse is hovering over it
     * @param button The reference to the button component on which the mouse is hovering on
     */
    private void mouseStartHoveringOverButton(Component button)
    {
        button.setBackground(new Color(162, 202, 228));
    }

    /**
     * Deactivates the highlighting effect on the button when mouse has stopped hovering over it
     * @param button The reference to the button component on which the mouse has stopped hovering over
     */
    private void mouseStopHoveringOverButton(Component button)
    {
        button.setBackground(Color.gray);
    }

    /**
     * Accessor for app window frame reference
     * @return Reference to the application window frame
     */
    private JFrame getWindowReference()
    {
        return this;
    }

    /**
     * Resizes the component according to the current resolution and resize scaling factors
     * @param component The reference to the component which is to be resized
     * @param xPos The initial X position in pixels that the component was set to relative to the app window
     * @param yPos The initial Y position in pixels that the component was set to relative to the app window
     * @param width The initial width in pixels that the component was set to
     * @param height The initial height in pixels that the component was set to
     * @param fontSize The initial font size that was component was set to
     */
    private void resizeComponent(JComponent component, int xPos, int yPos, int width, int height, int fontSize)
    {
        component.setBounds((int)(xPos * windowScalingFactorX * resolutionScalingFactorX),
                (int)(yPos * windowScalingFactorY * resolutionScalingFactorY),
                (int)(width * windowScalingFactorX * resolutionScalingFactorX),
                (int)(height * windowScalingFactorY * resolutionScalingFactorY));
        component.setFont(component.getFont().deriveFont(
                (float)(fontSize * (16 * windowScalingFactorX + 9 * windowScalingFactorY) / 25 *
                        (16 * resolutionScalingFactorX + 9 * resolutionScalingFactorY) / 25)));
    }

    /**
     * Handles window resizing by re-calculating resolution and resize scaling factors and resizing every app components
     */
    private void appWindowResized()
    {
        Dimension windowSize = getBounds().getSize();
        windowScalingFactorX = windowSize.getWidth() / initialWindowSizeX;
        windowScalingFactorY = windowSize.getHeight() / initialWindowSizeY;
        //System.out.println(windowSize.getWidth() + " " + windowSize.getHeight());
        //System.out.println(windowScalingFactorX + " " + windowScalingFactorY);

        resizeComponent(mainWindowTopHeader, 0, 0, 800, 70, 36);
        resizeComponent(differentWindowSubHeader, 0, 70, 800, 30, 20);
        resizeComponent(mainWindowEncryptButton, 100, 200, 250, 80, 24);
        resizeComponent(mainWindowDecryptButton, 100, 350, 250, 80, 24);
        resizeComponent(mainWindowEncryptStoreButton, 425, 200, 250, 80, 24);
        resizeComponent(mainWindowDecryptStoredButton, 425, 350, 250, 80, 24);
        resizeComponent(mainWindowRecentHistoryButton, 690, 130, 75, 40, 14);

        resizeComponent(encryptWindowCancelButton, 200, 400, 120, 50, 18);
        resizeComponent(encryptWindowStartButton, 480, 400, 120, 50, 18);
        resizeComponent(encryptWindowEnterFileLocation, 200, 140, 400, 25, 18);
        resizeComponent(encryptWindowEnterFileLocationText, 200, 170, 290, 25, 14);
        resizeComponent(encryptWindowEnterKey, 200, 240, 200, 25, 18);
        resizeComponent(encryptWindowUserGivenKeyOption, 200, 265, 180, 25, 18);
        resizeComponent(encryptWindowRandomKeyOption, 400, 265, 180, 25, 18);
        resizeComponent(encryptWindowEnterKeyText, 200, 295, 400, 25, 14);
        resizeComponent(encryptWindowFileBrowserButton, 500, 170, 100, 25, 12);
        resizeComponent(encryptWindowFileBrowser, 300, 170, 600, 400, 18);
        encryptWindowFileBrowser.setPreferredSize(new Dimension(
                (int)(600 * windowScalingFactorX * resolutionScalingFactorX),
                (int)(400 * windowScalingFactorY * resolutionScalingFactorY)));
        resizeComponent(encryptWindowFileLocationNotFoundError, 200, 200, 400, 30, 14);
        resizeComponent(encryptWindowFileLocationIOError, 200, 200, 400, 30, 14);
        resizeComponent(encryptWindowKeyInvalidError, 200, 325, 400, 30, 14);
        resizeComponent(encryptWindowEncryptingText, 200, 400, 400, 50, 18);

        resizeComponent(decryptWindowCancelButton, 200, 380, 120, 50, 18);
        resizeComponent(decryptWindowStartButton, 480, 380, 120, 50, 18);
        resizeComponent(decryptWindowTryStoredKeysButton, 340, 380, 120, 50, 13);
        resizeComponent(decryptWindowEnterFileLocation, 200, 140, 400, 25, 18);
        resizeComponent(decryptWindowEnterFileLocationText, 200, 170, 290, 25, 14);
        resizeComponent(decryptWindowEnterKey, 200, 240, 400, 25, 18);
        resizeComponent(decryptWindowEnterKeyText, 200, 270, 290, 25, 14);
        resizeComponent(decryptWindowFileBrowserButton, 500, 170, 100, 25, 12);
        resizeComponent(decryptWindowFileBrowser, 300, 170, 600, 400, 18);
        decryptWindowFileBrowser.setPreferredSize(new Dimension(
                (int)(600 * windowScalingFactorX * resolutionScalingFactorX),
                (int)(400 * windowScalingFactorY * resolutionScalingFactorY)));
        resizeComponent(decryptWindowForgotKeyButton, 500, 270, 100, 25, 12);
        resizeComponent(decryptWindowForgotKeyList, 200, 270, 290, 25, 14);
        resizeComponent(decryptWindowCustomKeyButton, 500, 270, 100, 25, 12);
        resizeComponent(decryptWindowFileLocationNotFoundError, 200, 200, 400, 30, 14);
        resizeComponent(decryptWindowFileLocationIOError, 200, 200, 400, 30, 14);
        resizeComponent(decryptWindowKeyInvalidError, 200, 300, 400, 30, 14);
        resizeComponent(decryptWindowWrongPasswordError, 200, 300, 400, 30, 14);
        resizeComponent(decryptWindowNoStoredKeysError, 200, 300, 400, 30, 14);
        resizeComponent(decryptWindowWrongKeyChosenError, 200, 300, 400, 30, 14);
        resizeComponent(decryptWindowDecryptingText, 200, 380, 400, 50, 18);

        resizeComponent(encryptStoreWindowEnterFileLocation, 200, 140, 400, 25, 18);
        resizeComponent(encryptStoreWindowEnterFileLocationText, 200, 170, 290, 25, 14);
        resizeComponent(encryptStoreWindowBrowseFileButton, 500, 170, 100, 25, 12);
        resizeComponent(encryptStoreWindowBrowseFileMenu, 300, 170, 600, 400, 18);
        encryptStoreWindowBrowseFileMenu.setPreferredSize(new Dimension(
                (int)(600 * windowScalingFactorX * resolutionScalingFactorX),
                (int)(400 * windowScalingFactorY * resolutionScalingFactorY)));
        resizeComponent(encryptStoreWindowFileNotFoundError, 200, 200, 400, 30, 14);
        resizeComponent(encryptStoreWindowFileIOError, 200, 200, 400, 30, 14);
        resizeComponent(encryptStoreWindowEnterKey, 200, 240, 200, 25, 18);
        resizeComponent(encryptStoreWindowCustomKeyButton, 200, 265, 180, 25, 18);
        resizeComponent(encryptStoreWindowRandomKeyButton, 400, 265, 180, 25, 18);
        resizeComponent(encryptStoreWindowEnterKeyText, 200, 295, 400, 25, 14);
        resizeComponent(encryptStoreWindowKeyInvalidError, 200, 325, 400, 30, 14);
        resizeComponent(encryptStoreWindowStartButton, 480, 400, 120, 50, 13);
        resizeComponent(encryptStoreWindowCancelButton, 200, 400, 120, 50, 18);
        resizeComponent(encryptStoreWindowEncryptingText, 200, 400, 400, 50, 18);

        resizeComponent(decryptStoredWindowChooseFile, 200, 110, 400, 25, 18);
        resizeComponent(decryptStoredWindowChooseFileList, 200, 140, 400, 25, 14);
        resizeComponent(decryptStoredWindowEnterKey, 200, 280, 400, 25, 18);
        resizeComponent(decryptStoredWindowEnterKeyText, 200, 310, 400, 25, 14);
        resizeComponent(decryptStoredWindowInvalidKeyError, 200, 340, 400, 30, 14);
        resizeComponent(decryptStoredWindowCancelButton, 200, 400, 120, 50, 18);
        resizeComponent(decryptStoredWindowStartButton, 480, 400, 120, 50, 13);
        resizeComponent(decryptStoredWindowTryAllButton, 340, 400, 120, 50, 13);
        resizeComponent(decryptStoredWindowDecryptingText, 200, 400, 400, 50, 18);
        resizeComponent(decryptStoredWindowEnterFolderLocation, 200, 185, 400, 25, 18);
        resizeComponent(decryptStoredWindowEnterFolderLocationText, 200, 215, 290, 25, 14);
        resizeComponent(decryptStoredWindowBrowseFolderButton, 500, 215, 100, 25, 12);
        resizeComponent(decryptStoredWindowBrowseFolderChooser, 300, 170, 600, 400, 18);
        decryptStoredWindowBrowseFolderChooser.setPreferredSize(new Dimension(
                (int)(600 * windowScalingFactorX * resolutionScalingFactorX),
                (int)(400 * windowScalingFactorY * resolutionScalingFactorY)));
        resizeComponent(decryptStoredWindowFolderNotFoundError, 200, 235, 400, 30, 14);

        resizeComponent(recentWindowCancelButton, 50, 400, 120, 50, 18);
        resizeComponent(recentWindowTable, 50, 125, 700, 250, 12);
        resizeComponent(recentWindowTableContainer, 50, 125, 702, 250, 14);
        int totalTableWidth = recentWindowTable.getWidth();
        recentWindowTable.getColumn("File name").setPreferredWidth((int)(totalTableWidth * 0.3));
        recentWindowTable.getColumn("Location").setPreferredWidth((int)(totalTableWidth * 0.5));
        recentWindowTable.getColumn("Last modified").setPreferredWidth((int)(totalTableWidth * 0.2));
        recentWindowTable.setRowHeight((int)(30 * windowScalingFactorX * resolutionScalingFactorX));
        recentWindowTable.getTableHeader().setFont(new Font("Calibri", Font.PLAIN,
                (int)(14 * (16 * windowScalingFactorX + 9 * windowScalingFactorY) / 25 *
                        (16 * resolutionScalingFactorX + 9 * resolutionScalingFactorY) / 25)));
        recentWindowTable.getTableHeader().setPreferredSize(new Dimension(
                recentWindowTableContainer.getWidth(), (int)(20 * windowScalingFactorX * resolutionScalingFactorX)));
    }

    /**
     * Handles closing of the app window frame when the red close button is clicked
     */
    private void windowCloseButtonClicked()
    {
        if(encryptWindowEncryptingText.isVisible() || decryptWindowDecryptingText.isVisible()
                || encryptStoreWindowEncryptingText.isVisible() || decryptStoredWindowDecryptingText.isVisible())
        {
            String message = "This application is currently busy " +
                    ((encryptWindowEncryptingText.isVisible() || encryptStoreWindowEncryptingText.isVisible())?
                            "encrypting" : "decrypting") + " a file.\nExiting now " +
                    "can result in corruption of the files being currently accessed.\n\nAre you sure you want to exit?";
            String title = ((encryptWindowEncryptingText.isVisible() || encryptStoreWindowEncryptingText.isVisible())?
                    "Encryption" : "Decryption") + " in progress";

            int chosenOption = JOptionPane.showConfirmDialog(this, message, title,
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(chosenOption == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        dispose();
    }
}
