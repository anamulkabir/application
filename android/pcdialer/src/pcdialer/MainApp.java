package pcdialer;

import com.sun.awt.AWTUtilities;
import pcdialer.signalling.Signalling;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class MainApp extends JFrame implements ActionListener, KeyListener, FocusListener, ItemListener, MouseListener, MouseMotionListener {
    int errorType = 0;
    int save = ItemEvent.DESELECTED;
     static String tunnel = null;
    static String account = null;
    static String authAccount = null;
    static String password = null;
    static String ACCOUNT_INFO_FILE = "accInfo.dat";
    static String ACCOUNT = "account";
    static String PASSWORD = "password";
    Point startDragPosition;
    Point startLocation;
    JLabel accountLabel;
    JLabel passwordLabel;
    JLabel currentActivityLabel;
    JTextField accountText;
    JTextField passwordText;
    JCheckBox saveInfoBox;
    JButton minimizeButton;
    JButton closeButton;
    JButton loginButton;
    JButton cancelButton;
    JPanel containerPanel;
    JPanel backgroundPanel;
    java.util.Timer registerTimer;
    RegisterChecker registerChecker;
    public static Dialer dialer;
    public static MainApp mainApp;

    public static void main(String[] args) {
        mainApp = new MainApp();
        mainApp.setVisible(true);
    }

    public void showLogin() {
        mainApp = new MainApp();
        mainApp.setVisible(true);
    }

    public MainApp() {
        super();
        dialer = new Dialer();
        containerPanel = (JPanel) getContentPane();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/resources/background.png"));
        Image image = imageIcon.getImage();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(image);
        this.setTitle("Flamma PCDialer");
        
        this.setResizable(false);
        this.setUndecorated(true);
        this.setSize(image.getWidth(null), image.getHeight(null));
        AWTUtilities.setWindowOpaque(this, false);

        backgroundPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Image image = new ImageIcon(getClass().getResource("/resources/background.png")).getImage();
                Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
                setPreferredSize(size);
                setMinimumSize(size);
                setMaximumSize(size);
                setSize(size);
                g.drawImage(image, 0, 0, null);
            }
        };
        backgroundPanel.setBounds(0, 0, image.getWidth(null), image.getHeight(null));
        this.setLocationRelativeTo(null);
        minimizeButton = new JButton();
        minimizeButton.setBackground(null);
        minimizeButton.setBounds(150, 9, 12, 12);
        minimizeButton.setIcon(new ImageIcon(getClass().getResource("/resources/minimize.png")));
        minimizeButton.setBorder(null);
        minimizeButton.setRolloverEnabled(true);
        minimizeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/resources/minimize_hover.png")));
        minimizeButton.setOpaque(false);
        minimizeButton.addActionListener(this);
        containerPanel.add(minimizeButton);

        closeButton = new JButton();
        closeButton.setBackground(null);
        closeButton.setBounds(164, 9, 12, 12);
        closeButton.setIcon(new ImageIcon(getClass().getResource("/resources/cross.png")));
        closeButton.setBorder(null);
        closeButton.setRolloverEnabled(true);
        closeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/resources/cross_hover.png")));
        closeButton.setOpaque(false);
        closeButton.addActionListener(this);
        containerPanel.add(closeButton);

        currentActivityLabel = new JLabel("");
        currentActivityLabel.setBounds(25, 48, 200, 20);
        containerPanel.add(currentActivityLabel);

        accountLabel = new JLabel("Account");
        accountLabel.setBounds(25, 75, 50, 20);
        accountLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(accountLabel);

        accountText = new JTextField(100);
        accountText.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        accountText.setBounds(25, 100, 125, 20);
        accountText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        accountText.addFocusListener(this);
        accountText.addKeyListener(this);
        containerPanel.add(accountText);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(25, 125, 60, 20);
        passwordLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(passwordLabel);

        passwordText = new JPasswordField(100);
        passwordText.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        passwordText.setBounds(25, 150, 125, 20);
        passwordText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        passwordText.addFocusListener(this);
        passwordText.addKeyListener(this);
        containerPanel.add(passwordText);

        saveInfoBox = new JCheckBox("Save Account Info");
        saveInfoBox.setBounds(22, 180, 130, 20);
        saveInfoBox.setForeground(new Color(0x2A, 0x55, 0xFF));
        saveInfoBox.setOpaque(false);
        saveInfoBox.addItemListener(this);
        saveInfoBox.addKeyListener(this);
        containerPanel.add(saveInfoBox);

        loginButton = new JButton("Login");
        loginButton.setBounds(25, 215, 65, 20);
        loginButton.setForeground(new Color(0x2A, 0x55, 0xFF));
        loginButton.addActionListener(this);
        loginButton.addKeyListener(this);
        containerPanel.add(loginButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(100, 215, 75, 20);
        cancelButton.setForeground(new Color(0x2A, 0x55, 0xFF));
        cancelButton.addActionListener(this);
        cancelButton.addKeyListener(this);
        containerPanel.add(cancelButton);

        containerPanel.add(backgroundPanel);
        containerPanel.addMouseListener(this);
        containerPanel.addMouseMotionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                readAccountInfo();
                accountText.requestFocus();
            }
        });
    }

    public void actionPerformed(ActionEvent event) {
        Object actionSrc = event.getSource();
        if (actionSrc == closeButton) {
            this.dispose();
            dialer.unregisterSIP();
            System.exit(0);
        } else if (actionSrc == cancelButton) {
            if (registerTimer != null) {
                registerChecker.regRunning = false;
                registerTimer.cancel();
            }
            currentActivityLabel.setText(" ");
            dialer.unregisterSIP();
            loginButton.setEnabled(true);
        } else if (actionSrc == minimizeButton) {
            this.setState(Frame.ICONIFIED);
        } else if (actionSrc == loginButton) {
            if (!loginButton.isEnabled()) {
                return;
            }
            if (accountText.getText().trim().length() < 1) {
                errorType = 1;
                currentActivityLabel.setForeground(Color.RED);
                currentActivityLabel.setText("Please Enter Account.....");
                accountText.requestFocus();
            } else if (passwordText.getText().trim().length() < 1) {
                errorType = 2;
                currentActivityLabel.setForeground(Color.RED);
                currentActivityLabel.setText("Please Enter Password.....");
                passwordText.requestFocus();
            } else {
                loginButton.setEnabled(false);
                currentActivityLabel.setForeground(new Color(0xD4, 0x00, 0xFF));
                currentActivityLabel.setText("Trying To Login.......");
                loginToSwitch();
            }
        }
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        Object actionSrc = event.getSource();
        if (save == ItemEvent.SELECTED && (actionSrc == accountText || actionSrc == passwordText)) {
            saveAccountInfo();
        }
    }

    public void itemStateChanged(ItemEvent event) {
        save = event.getStateChange();
        if (save == ItemEvent.SELECTED) {
            saveAccountInfo();
        } else {
            deleteAccountInfo();
        }
    }

    public void mousePressed(MouseEvent event) {
        this.startDragPosition = this.getScreenLocation(event);
        this.startLocation = this.getFrame(containerPanel).getLocation();
    }

    public void mouseDragged(MouseEvent event) {
        Point current = this.getScreenLocation(event);
        Point offset = new Point((int) current.getX() - (int) startDragPosition.getX(),
                (int) current.getY() - (int) startDragPosition.getY());
        JFrame frame = this.getFrame(containerPanel);
        Point new_location = new Point(
                (int) (this.startLocation.getX() + offset.getX()), (int) (this.startLocation.getY() + offset.getY()));
        frame.setLocation(new_location);
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {
    }

    public void keyTyped(KeyEvent event) {
        Object actionSrc = event.getSource();
        if (errorType == 1 && actionSrc == accountText) {
            currentActivityLabel.setText("");
        } else if (errorType == 2 && actionSrc == passwordText) {
            currentActivityLabel.setText("");
        }
    }

    public void keyPressed(KeyEvent event) {
        int key = (int) event.getKeyChar();
        if (key == KeyEvent.VK_ENTER) {
            if (!loginButton.isEnabled()) {
                if (registerTimer != null) {
                    registerChecker.regRunning = false;
                    registerTimer.cancel();
                }
                currentActivityLabel.setText(" ");
                dialer.unregisterSIP();
                loginButton.setEnabled(true);
            } else {
                loginButton.doClick();
            }
        }
    }

    public void keyReleased(KeyEvent event) {
    }

    public static JFrame getFrame(Container target) {
        if (target instanceof JFrame) {
            return (JFrame) target;
        }
        return getFrame(target.getParent());
    }

    Point getScreenLocation(MouseEvent event) {
        Point cursor = event.getPoint();
        Point target_location = containerPanel.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
    }

    public void readAccountInfo() {
        try {
            InputStream input = null;
            File file = new File(ACCOUNT_INFO_FILE);
            if (file.exists()) {
                input = new FileInputStream(file);
                Properties fileProp = new Properties();
                fileProp.load(input);
                input.close();
                if (fileProp.containsKey("account")) {
                    String accStr = fileProp.getProperty("account");
                    if (accStr != null && accStr.trim().length() > 0) {
                        accountText.setText(accStr);
                    }
                }
                if (fileProp.containsKey("password")) {
                    String pass = fileProp.getProperty("password");
                    if (pass != null && pass.trim().length() > 0) {
                        String newPass = "";
                        for (int i = 0; i < pass.length(); i++) {
                            int val = (int) pass.charAt(i);
                            if (val < 2 || val > 126) {
                                newPass += pass.charAt(i);
                            } else if (val % 2 == 0) {
                                newPass += (char) (pass.charAt(i) - 1);
                            } else {
                                newPass += (char) (pass.charAt(i) + 1);
                            }
                        }
                        passwordText.setText(newPass);
                    }
                }
                saveInfoBox.setSelected(true);
            }
        } catch (Exception e) {
        }
    }

    public void saveAccountInfo() {
        try {
            BufferedWriter outFile = new BufferedWriter(new FileWriter(ACCOUNT_INFO_FILE));
            outFile.write("account=" + accountText.getText().trim());
            String password = passwordText.getText().trim();
            String newPassword = "";
            if (password != null && password.length() > 0) {
                for (int i = 0; i < password.length(); i++) {
                    int val = (int) password.charAt(i);
                    if (val < 2 || val > 126) {
                        newPassword += password.charAt(i);
                    } else if (val % 2 == 0) {
                        newPassword += (char) (password.charAt(i) - 1);
                    } else {
                        newPassword += (char) (password.charAt(i) + 1);
                    }
                }
            }
            outFile.write("\npassword=" + newPassword);
            outFile.close();
        } catch (Exception ex) {
        }
    }

    public void deleteAccountInfo() {
        try {
            File accountInfo = new File(ACCOUNT_INFO_FILE);
            if (accountInfo.exists()) {
                accountInfo.delete();
            }
        } catch (Exception ex) {
        }
    }

    public void showMainDialer() {
        if (registerTimer != null) {
            registerTimer.cancel();
        }
        dialer.ShowDialer(accountText.getText(), passwordText.getText());
        this.dispose();
    }

    public void setErrorLabel() {
        if (registerTimer != null) {
            registerTimer.cancel();
        }
        loginButton.setEnabled(true);
        currentActivityLabel.setForeground(Color.RED);
        if (dialer.registrationError != null) {
            currentActivityLabel.setText(dialer.registrationError);
        } else {
            currentActivityLabel.setText("Login Error.......");
        }
    }

    public void setProcesingLabel(int counter) {
        currentActivityLabel.setForeground(new Color(0xD4, 0x00, 0xFF));
        if (counter == 1) {
            currentActivityLabel.setText("Trying To Login........");
        } else if (counter == 2) {
            currentActivityLabel.setText("Trying To Login.........");
        } else if (counter == 3) {
            currentActivityLabel.setText("Trying To Login..........");
        } else if (counter == 4) {
            currentActivityLabel.setText("Trying To Login...........");
        } else if (counter == 5) {
            currentActivityLabel.setText("Trying To Login............");
        }
    }

    public void loginToSwitch() {
        try {
            byte ip[] = InetAddress.getByName(dialer.switchIP).getAddress();
            Signalling.encode[0] = ip[0];
            Signalling.encode[1] = ip[1];
            Signalling.encode[2] = ip[2];
            Signalling.encode[3] = ip[3];
            byte[] a = Signalling.intToByteArray(dialer.switchPort);
            Signalling.encode[4] = a[2];
            Signalling.encode[5] = a[3];
        } catch (Exception nfe) {
            currentActivityLabel.setForeground(Color.RED);
            currentActivityLabel.setText("Invalid SIP Switch Info.");
            accountText.requestFocus();
            loginButton.setEnabled(true);
            return;
        }
        try {
            tunnel = InetAddress.getByName(dialer.tunnelIP).getHostAddress() + ":" + dialer.tunnelPort;
        } catch (Exception nfe) {
            currentActivityLabel.setForeground(Color.RED);
            currentActivityLabel.setText("Invalid Bypass Information");
            accountText.requestFocus();
            loginButton.setEnabled(true);
            return;
        }

        account = accountText.getText().trim();
        authAccount = accountText.getText().trim();
        password = passwordText.getText().trim();
        dialer.registerSIP();
        registerTimer = new java.util.Timer();
        registerChecker = new RegisterChecker(this);
        registerTimer.schedule(registerChecker, 5000, 5000);
    }
}

class RegisterChecker extends java.util.TimerTask {

    public boolean regRunning = true;
    int counter;
    MainApp dialer;

    public RegisterChecker(MainApp pcdialer) {
        dialer = pcdialer;
        counter = 0;
    }

    public void run() {
        counter++;
        if (regRunning) {
            if (dialer.dialer.IS_REGISTERRED) {
                dialer.showMainDialer();
            } else {
                if (counter > 6) {
                    dialer.setErrorLabel();
                } else {
                    dialer.setProcesingLabel(counter);
                }
            }
        }
    }
}
