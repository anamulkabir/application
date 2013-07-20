package pcdialer;

import util.SuccessfulCallList;
import com.sun.awt.AWTUtilities;
import pcdialer.media.Media;
import pcdialer.media.RTPHandler;
import pcdialer.signalling.Signalling;
import pcdialer.signalling.UA;
import pcdialer.signalling.UAInterface;
import pcdialer.media.codec.G711;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.DatagramSocket;
import javax.swing.text.JTextComponent;
import util.Utils;

public class Dialer extends JFrame implements ActionListener, KeyListener, FocusListener, ItemListener, MouseListener, MouseMotionListener, UAInterface {

    public static boolean IS_INCOMING;
    public static boolean IS_INCALL;
    public static boolean IS_TRYING;
    public static boolean IS_RINGING;
    public static boolean IS_200OK;
    public static boolean IS_UNAUTH;
    public boolean IS_REGISTERRED;
    public boolean IS_NETWORK_AVAILABLE;
    public boolean IS_ACTIVE = true;
    public static boolean dtmfEnd;
    public static char dtmf = 'x';
    public int terRTPPort;
    public int localPort = 10000;
    public int duration = 0;
    public int registerRetryCounter;
    public static int preferredCodec;
    public int tunnelPort = 60;
    public int switchPort = 7777;
    public int codec;
    public int codecList[];
    public String calledNumber = "";
    public String callingStatus = "";
    public String callID = "";
    public String orgCalledNumber;
    public String calleeNumber;
    public String callerID;
    public String terRTPHost;
    public String account;
    public String password;
    public String tunnelIP = "192.168.0.110";//"108.166.204.26";
    public String switchIP = "192.168.0.116";
    public String recentCalledNumber;
    public String registrationError = null;
    Point startDragPosition;
    Point startLocation;
    JLabel accountLabel;
    JLabel accountValLabel;
    JLabel balanceLabel;
    JLabel balanceValLabel;
    JLabel costLabel;
    JLabel costValLabel;    
    JLabel statusLabel;
    JLabel durationLabel;
    JLabel durationValLabel;
    JButton[] buttons = new JButton[19];
    JTextComponent phoneNumberBox;
    JPanel backgroundPanel;
    JPanel containerPanel;
    JMenuItem logOutMenu;
    JMenuItem exitMenu;
    JMenuItem successfulCallMenu;
    JMenuBar menu;
    JComboBox phoneNumbers;
    public java.util.Timer keepAliveTimer;
    public java.util.Timer regExpireTimer;
    public java.util.Timer regRetryTimer;
    public java.util.Timer durationCounter;
    public UA ua;
    public static RTPHandler rtpHandler;
    public Media media = new Media();

    public Dialer() {
        Utils.readSuccessfulCallFile();
        startKeepAliveTimer();
        G711.init();
        media.init();
        containerPanel = (JPanel) getContentPane();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/resources/main_background.png"));
        Image image = imageIcon.getImage();
        this.setTitle("Flamma PCDialer");       
        this.setResizable(false);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(image);
        this.setSize(image.getWidth(null), image.getHeight(null));

        AWTUtilities.setWindowOpaque(this, false);
        backgroundPanel = new JPanel() {

            public void paintComponent(Graphics g) {
                Image image = new ImageIcon(getClass().getResource("/resources/main_background.png")).getImage();
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
        menu = new JMenuBar();
        JMenu menu = new JMenu("Menu >>");
        menu.setMnemonic(KeyEvent.VK_M);
        successfulCallMenu = menu.add("Call List");
        successfulCallMenu.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        successfulCallMenu.addActionListener(this);
        logOutMenu = menu.add("Log Out");
        logOutMenu.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.ALT_MASK));
        logOutMenu.addActionListener(this);
        menu.addSeparator();
        exitMenu = menu.add("Exit");
        exitMenu.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        exitMenu.addActionListener(this);
        this.menu.add(menu);
        this.menu.setBounds(115, 404, 57, 18);
        containerPanel.add(this.menu);

        buttons[15] = new JButton();
        buttons[15].setBackground(null);
        buttons[15].setBounds(225, 17, 12, 12);
        buttons[15].setIcon(new ImageIcon(getClass().getResource("/resources/minimize.png")));
        buttons[15].setBorder(null);
        buttons[15].setRolloverEnabled(true);
        buttons[15].setOpaque(false);
        buttons[15].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/minimize_hover.png")));
        buttons[15].addActionListener(this);

        containerPanel.add(buttons[15]);
        buttons[16] = new JButton();
        buttons[16].setBackground(null);
        buttons[16].setBounds(239, 17, 12, 12);
        buttons[16].setIcon(new ImageIcon(getClass().getResource("/resources/cross.png")));
        buttons[16].setBorder(null);
        buttons[16].setRolloverEnabled(true);
        buttons[16].setOpaque(false);
        buttons[16].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/cross_hover.png")));
        buttons[16].addActionListener(this);
        containerPanel.add(buttons[16]);

        accountLabel = new JLabel("Account: ");
        accountLabel.setBounds(25, 12, 55, 20);
        accountLabel.setHorizontalAlignment(JLabel.LEFT);
        accountLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(accountLabel);

        accountValLabel = new JLabel("-----");
        accountValLabel.setBounds(78, 12, 60, 20);
        accountValLabel.setHorizontalAlignment(JLabel.LEFT);
        accountValLabel.setForeground(new Color(0xFF, 0xFF, 0xD4));
        containerPanel.add(accountValLabel);

        balanceLabel = new JLabel("Balance: ");
        balanceLabel.setBounds(25, 25, 55, 20);
        balanceLabel.setHorizontalAlignment(JLabel.LEFT);
        balanceLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(balanceLabel);

        balanceValLabel = new JLabel("-----");
        balanceValLabel.setBounds(78, 25, 60, 20);
        balanceValLabel.setHorizontalAlignment(JLabel.LEFT);
        balanceValLabel.setForeground(new Color(0xFF, 0xFF, 0xD4));
        containerPanel.add(balanceValLabel);

        durationLabel = new JLabel("Duration: ");
        durationLabel.setBounds(25, 38, 55, 20);
        durationLabel.setHorizontalAlignment(JLabel.LEFT);
        durationLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(durationLabel);

        durationValLabel = new JLabel("-- : -- : --");
        durationValLabel.setBounds(78, 38, 80, 20);
        durationValLabel.setHorizontalAlignment(JLabel.LEFT);
        durationValLabel.setForeground(new Color(0xFF, 0xFF, 0xD4));
        containerPanel.add(durationValLabel);
        
        costLabel = new JLabel("Call Cost: ");
        costLabel.setBounds(23, 51, 80, 20);
        costLabel.setHorizontalAlignment(JLabel.LEFT);
        costLabel.setForeground(new Color(0x2A, 0x55, 0xFF));
        containerPanel.add(costLabel);

        costValLabel = new JLabel("-----");
        costValLabel.setBounds(78, 51, 80, 20);
        costValLabel.setHorizontalAlignment(JLabel.LEFT);
        costValLabel.setForeground(new Color(0xFF, 0xFF, 0xD4));
        containerPanel.add(costValLabel);         

        statusLabel = new JLabel("-----");
        statusLabel.setBounds(30, 105, 230, 20);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(new Color(0xD4, 0x00, 0xFF));
        containerPanel.add(statusLabel);

        phoneNumbers = new JComboBox();
        phoneNumbers.setEditable(true);
        phoneNumbers.addKeyListener(this);
        phoneNumberBox = (JTextComponent) phoneNumbers.getEditor().getEditorComponent();
        phoneNumberBox.addKeyListener(this);
        phoneNumbers.setBounds(42, 152, 198, 20);
        containerPanel.add(phoneNumbers);
        Utils.readPhoneNumberList();
        for (int i = Utils.phoneNumberList.size() - 1; i >= 0; i--) {
            phoneNumbers.addItem(Utils.phoneNumberList.get(i));
        }
        buttons[13] = new JButton();
        buttons[13].setBackground(null);
        buttons[13].setBounds(42, 190, 70, 25);
        buttons[13].setIcon(new ImageIcon(getClass().getResource("/resources/call.png")));
        buttons[13].setBorder(null);
        buttons[13].setRolloverEnabled(true);
        buttons[13].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/call_h.png")));
        buttons[13].addActionListener(this);
        containerPanel.add(buttons[13]);

        buttons[12] = new JButton();
        buttons[12].setBackground(null);
        buttons[12].setBounds(125, 190, 35, 25);
        buttons[12].setIcon(new ImageIcon(getClass().getResource("/resources/c.png")));
        buttons[12].setBorder(null);
        buttons[12].setRolloverEnabled(true);
        buttons[12].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/c_h.png")));
        buttons[12].addActionListener(this);
        containerPanel.add(buttons[12]);

        buttons[14] = new JButton();
        buttons[14].setBackground(null);
        buttons[14].setBounds(172, 190, 70, 25);
        buttons[14].setIcon(new ImageIcon(getClass().getResource("/resources/hang.png")));
        buttons[14].setBorder(null);
        buttons[14].setRolloverEnabled(true);
        buttons[14].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/hang_h.png")));
        buttons[14].addActionListener(this);
        containerPanel.add(buttons[14]);

        buttons[1] = new JButton();
        buttons[1].setBackground(null);
        buttons[1].setBounds(57, 225, 50, 25);
        buttons[1].setIcon(new ImageIcon(getClass().getResource("/resources/1.png")));
        buttons[1].setBorder(null);
        buttons[1].setRolloverEnabled(true);
        buttons[1].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/1_h.png")));
        buttons[1].addActionListener(this);
        containerPanel.add(buttons[1]);

        buttons[2] = new JButton();
        buttons[2].setBackground(null);
        buttons[2].setBounds(117, 225, 50, 25);
        buttons[2].setIcon(new ImageIcon(getClass().getResource("/resources/2.png")));
        buttons[2].setBorder(null);
        buttons[2].setRolloverEnabled(true);
        buttons[2].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/2_h.png")));
        buttons[2].addActionListener(this);
        containerPanel.add(buttons[2]);

        buttons[3] = new JButton();
        buttons[3].setBackground(null);
        buttons[3].setBounds(177, 225, 50, 25);
        buttons[3].setIcon(new ImageIcon(getClass().getResource("/resources/3.png")));
        buttons[3].setBorder(null);
        buttons[3].setRolloverEnabled(true);
        buttons[3].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/3_h.png")));
        buttons[3].addActionListener(this);
        containerPanel.add(buttons[3]);

        buttons[4] = new JButton();
        buttons[4].setBackground(null);
        buttons[4].setBounds(57, 260, 50, 25);
        buttons[4].setIcon(new ImageIcon(getClass().getResource("/resources/4.png")));
        buttons[4].setBorder(null);
        buttons[4].setRolloverEnabled(true);
        buttons[4].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/4_h.png")));
        buttons[4].addActionListener(this);
        containerPanel.add(buttons[4]);

        buttons[5] = new JButton();
        buttons[5].setBackground(null);
        buttons[5].setBounds(117, 260, 50, 25);
        buttons[5].setIcon(new ImageIcon(getClass().getResource("/resources/5.png")));
        buttons[5].setBorder(null);
        buttons[5].setRolloverEnabled(true);
        buttons[5].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/5_h.png")));
        buttons[5].addActionListener(this);
        containerPanel.add(buttons[5]);

        buttons[6] = new JButton();
        buttons[6].setBackground(null);
        buttons[6].setBounds(177, 260, 50, 25);
        buttons[6].setIcon(new ImageIcon(getClass().getResource("/resources/6.png")));
        buttons[6].setBorder(null);
        buttons[6].setRolloverEnabled(true);
        buttons[6].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/6_h.png")));
        buttons[6].addActionListener(this);
        containerPanel.add(buttons[6]);

        buttons[7] = new JButton();
        buttons[7].setBackground(null);
        buttons[7].setBounds(57, 295, 50, 25);
        buttons[7].setIcon(new ImageIcon(getClass().getResource("/resources/7.png")));
        buttons[7].setBorder(null);
        buttons[7].setRolloverEnabled(true);
        buttons[7].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/7_h.png")));
        buttons[7].addActionListener(this);
        containerPanel.add(buttons[7]);

        buttons[8] = new JButton();
        buttons[8].setBackground(null);
        buttons[8].setBounds(117, 295, 50, 25);
        buttons[8].setIcon(new ImageIcon(getClass().getResource("/resources/8.png")));
        buttons[8].setBorder(null);
        buttons[8].setRolloverEnabled(true);
        buttons[8].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/8_h.png")));
        buttons[8].addActionListener(this);
        containerPanel.add(buttons[8]);

        buttons[9] = new JButton();
        buttons[9].setBackground(null);
        buttons[9].setBounds(177, 295, 50, 25);
        buttons[9].setIcon(new ImageIcon(getClass().getResource("/resources/9.png")));
        buttons[9].setBorder(null);
        buttons[9].setRolloverEnabled(true);
        buttons[9].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/9_h.png")));
        buttons[9].addActionListener(this);
        containerPanel.add(buttons[9]);

        buttons[10] = new JButton();
        buttons[10].setBackground(null);
        buttons[10].setBounds(57, 330, 50, 25);
        buttons[10].setIcon(new ImageIcon(getClass().getResource("/resources/star.png")));
        buttons[10].setBorder(null);
        buttons[10].setRolloverEnabled(true);
        buttons[10].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/star_h.png")));
        buttons[10].addActionListener(this);
        buttons[10].addMouseListener(this);
        containerPanel.add(buttons[10]);

        buttons[0] = new JButton();
        buttons[0].setBackground(null);
        buttons[0].setBounds(117, 330, 50, 25);
        buttons[0].setIcon(new ImageIcon(getClass().getResource("/resources/0.png")));
        buttons[0].setBorder(null);
        buttons[0].setRolloverEnabled(true);
        buttons[0].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/0_h.png")));
        buttons[0].addActionListener(this);
        containerPanel.add(buttons[0]);

        buttons[11] = new JButton();
        buttons[11].setBackground(null);
        buttons[11].setBounds(177, 330, 50, 25);
        buttons[11].setIcon(new ImageIcon(getClass().getResource("/resources/hash.png")));
        buttons[11].setBorder(null);
        buttons[11].setRolloverEnabled(true);
        buttons[11].setRolloverIcon(new ImageIcon(getClass().getResource("/resources/hash_h.png")));
        buttons[11].addActionListener(this);
        containerPanel.add(buttons[11]);

        containerPanel.add(backgroundPanel);
        containerPanel.addMouseListener(this);
        containerPanel.addMouseMotionListener(this);
        addWindowListener(new WindowAdapter() {

            public void windowOpened(WindowEvent e) {
                phoneNumberBox.requestFocus();
            }
        });
    }

    public void ShowDialer(String accVal, String passVal) {
        account = accVal;
        password = passVal;
        accountValLabel.setText(accVal);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        for (int i = 0; i < buttons.length; i++) {
            if (event.getSource() == buttons[i]) {
                switch (i) {
                    case 0:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "0");
                        phoneNumberBox.requestFocus();
                        break;
                    case 1:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "1");
                        phoneNumberBox.requestFocus();
                        break;
                    case 2:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "2");
                        phoneNumberBox.requestFocus();
                        break;
                    case 3:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "3");
                        phoneNumberBox.requestFocus();
                        break;
                    case 4:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "4");
                        phoneNumberBox.requestFocus();
                        break;
                    case 5:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "5");
                        phoneNumberBox.requestFocus();
                        break;
                    case 6:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "6");
                        phoneNumberBox.requestFocus();
                        break;
                    case 7:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "7");
                        phoneNumberBox.requestFocus();
                        break;
                    case 8:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "8");
                        phoneNumberBox.requestFocus();
                        break;
                    case 9:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "9");
                        phoneNumberBox.requestFocus();
                        break;
                    case 10:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "*");
                        phoneNumberBox.requestFocus();
                        break;
                    case 11:
                        phoneNumberBox.setText(phoneNumberBox.getText() + "#");
                        phoneNumberBox.requestFocus();
                        break;
                    case 12:
                        String input = phoneNumberBox.getText();
                        if (input != null && input.length() > 0) {
                            int selectedPoint = phoneNumberBox.getSelectionStart();
                            if (selectedPoint == input.length()) {
                                phoneNumberBox.setText("");
                                phoneNumberBox.requestFocus();
                            } else {
                                String part1 = input.substring(0, selectedPoint);
                                String part2 = input.substring(selectedPoint + 1);
                                phoneNumberBox.setText(part1 + part2);
                                phoneNumberBox.requestFocus();
                                phoneNumberBox.setSelectionStart(selectedPoint);
                                phoneNumberBox.setSelectionEnd(selectedPoint);
                            }
                        }
                        break;
                    case 13:
                        processSIPCall();
                        break;
                    case 14:
                        terminateSIPCall();
                        break;
                    case 15:
                        this.setState(Frame.ICONIFIED);
                        break;
                    case 16:
                        this.dispose();
                        unregisterSIP();
                        System.exit(0);
                        break;
                }
            }
        }
        if (event.getSource() == exitMenu) {
            this.dispose();
            unregisterSIP();
            System.exit(0);
        }
        if (event.getSource() == logOutMenu) {
            terminateSIPCall();
            if (keepAliveTimer != null) {
                keepAliveTimer.cancel();
            }
            if (regExpireTimer != null) {
                regExpireTimer.cancel();
            }
            if (regRetryTimer != null) {
                regRetryTimer.cancel();
            }
            endDurationCounter();
            this.dispose();
            unregisterSIP();
            MainApp.mainApp.showLogin();
        }
        if (event.getSource() == successfulCallMenu) {
            SuccessfulCallList.getInstance().getList(Utils.successfulCallList);
        }
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
    }

    public void itemStateChanged(ItemEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
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

    public void mouseMoved(MouseEvent event) {
    }

    public void keyTyped(KeyEvent event) {
    }

    public void keyPressed(KeyEvent event) {
        int key = (int) event.getKeyChar();
        if (key == KeyEvent.VK_ENTER) {
            if (buttons[13].isEnabled()) {
                processSIPCall();
            } else {
                terminateSIPCall();
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

    public void registerSIP() {
        int index;
        String tunnelIP;
        int tunnelPort;
        ua = new UA();
        index = MainApp.tunnel.indexOf(':');
        if (index == -1) {
            tunnelIP = MainApp.tunnel;
            tunnelPort = 5060;
        } else {
            tunnelIP = MainApp.tunnel.substring(0, index);
            tunnelPort = Utils.atoi(MainApp.tunnel.substring(index + 1));
        }
        DatagramSocket datagramSocket = null;
        while (true) {
            try {
                datagramSocket = new DatagramSocket(localPort++);
                datagramSocket.close();
                break;
            } catch (Exception e) {
                try {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
        ua.initUA(tunnelIP, tunnelPort, localPort, ua, this);
        ua.registerUA(MainApp.account, MainApp.authAccount, MainApp.password);

        regExpireTimer = new java.util.Timer();
        regExpireTimer.scheduleAtFixedRate(new ProcessRegisterRetry(), 395 * 1000, 395 * 1000);
        registerRetryCounter = 0;
        regRetryTimer = new java.util.Timer();
        regRetryTimer.schedule(new startRegisterRetryProcessing(), 1000);
    }

    public void unregisterSIP() {
        if (regExpireTimer != null) {
            regExpireTimer.cancel();
            regExpireTimer = null;
        }
        if (IS_INCALL) {
            buttons[13].setSelected(true);
            buttons[14].setSelected(false);
            if (calledNumber.equals("")) {
                calledNumber = phoneNumberBox.getText();
            } else {
                phoneNumberBox.setText(calledNumber);
            }
            statusLabel.setText(callingStatus);
            terminateSIPCall();
        }
        calledNumber = "";
        callingStatus = "";
        IS_UNAUTH = false;
        if (ua != null) {
            if (ua.isRegistered()) {
                ua.unRegisterUA();
            }
            ua.uninit();
        }
    }

    public void processSIPCall() {
        if (ua == null) {
            return;
        }
        if (!ua.isRegistered()) {
            return;
        }
        if (IS_INCALL) {
            return;
        }
        if (phoneNumberBox.getText().trim().length() > 0) {
            calledNumber = phoneNumberBox.getText().trim();
        } else {
            return;
        }
        durationValLabel.setText("00:00:00");
        costValLabel.setText("-----");
        buttons[13].setEnabled(false);
        if (IS_INCOMING) {
            acceptInboundSIPCall();
        } else {
            int length = Utils.savePhoneNumberList(calledNumber);
            phoneNumbers.removeAllItems();
            for (int i = length - 1; i >= 0; i--) {
                phoneNumbers.addItem(Utils.phoneNumberList.get(i));
            }
            media.startCalling();
            startSIPCall();
        }
    }

    public void terminateSIPCall() {
        if (IS_INCOMING) {
            ua.sendDeny(callID, "IGNORE", 480);
            IS_INCOMING = false;
            IS_RINGING = false;
            calledNumber = "";
            buttons[13].setEnabled(true);
            callingStatus = "Ready To Call.....";
            endDurationCounter();
            buttons[14].setSelected(false);
            if (IS_INCALL) {
                buttons[13].setSelected(true);
            } else {
                buttons[13].setSelected(false);
            }
            if (calledNumber.equals("")) {
                calledNumber = phoneNumberBox.getText();
            } else {
                phoneNumberBox.setText(calledNumber);
            }
            statusLabel.setText(callingStatus);
            return;
        }
        calledNumber = "";
        if (!IS_INCALL) {
            if ((ua != null) && (!IS_UNAUTH)) {
                buttons[13].setEnabled(true);
                callingStatus = "Ready To Call.....";
            }
            buttons[14].setSelected(false);
            buttons[13].setSelected(false);
            if (calledNumber.equals("")) {
                calledNumber = phoneNumberBox.getText();
            } else {
                phoneNumberBox.setText(calledNumber);
            }
            statusLabel.setText(callingStatus);
            return;
        }
        if (IS_200OK) {
            ua.sendBye(callID);
        } else {
            ua.sendCancel(callID);
        }
        String cdr = orgCalledNumber + "," + getCallDuration(duration);
        calledNumber = "";
        orgCalledNumber = "";
        buttons[13].setEnabled(true);
        callingStatus = "Ready To Call...";
        endDurationCounter();
        if (duration > 0) {
            Utils.saveSuccessfulCallFile(cdr);
        }
        duration = 0;
        IS_TRYING = false;
        IS_RINGING = false;
        IS_INCOMING = false;
        IS_INCALL = false;
        IS_200OK = false;
        rtpHandler.stopRTPSession();
        rtpHandler = null;
        callID = "";
        buttons[13].setSelected(false);
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
    }

    public void startSIPCall() {
        calleeNumber = calledNumber;
        rtpHandler = new RTPHandler();
        rtpHandler.initRTPSession(ua);
        IS_INCALL = true;
        IS_TRYING = false;
        IS_RINGING = false;
        IS_200OK = false;
        IS_INCOMING = false;
        callingStatus = "Calling.....";
        recentCalledNumber = calledNumber;
        preferredCodec = RTPHandler.CODEC_G729a;  //see CallInviteOk()
        callID = ua.sendInvite(calledNumber, rtpHandler.getLocalRTPPort(), new int[]{RTPHandler.CODEC_G729a, RTPHandler.CODEC_G711});
        orgCalledNumber = calledNumber;
        buttons[14].setSelected(false);
        buttons[13].setSelected(true);

        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
    }

    public void acceptInboundSIPCall() {
        int currentCodec;
        if (Signalling.hasCodec(codecList, RTPHandler.CODEC_G729a)) {
            currentCodec = RTPHandler.CODEC_G729a;
        } else if (Signalling.hasCodec(codecList, RTPHandler.CODEC_G711)) {
            currentCodec = RTPHandler.CODEC_G711;
        } else {
            ua.sendDeny(callID, "Only g711 or g729a is supported", 415);
            setCancelled(callID, 415);
            return;
        }
        calleeNumber = calledNumber;
        codec = currentCodec;
        rtpHandler = new RTPHandler();
        rtpHandler.initRTPSession(ua);
        ua.sendAccept(callID, rtpHandler.getLocalRTPPort(), new int[]{codec});
        media.flush();
        rtpHandler.startRTPSession(terRTPHost, terRTPPort, codec);
        IS_INCALL = true;
        IS_RINGING = false;
        IS_INCOMING = false;
        IS_200OK = true;
        callingStatus = "Connected.....";
        buttons[14].setSelected(false);
        buttons[13].setSelected(false);
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
        duration = 0;
        durationCounter = new java.util.Timer();
        durationCounter.scheduleAtFixedRate(new DurationCounter(), 0, 1 * 1000);
    }

    public void acceptOutboundSIPCall(String remotertphost, int remotertpport, int codecs[]) {
        int currentCodec;
        if (Signalling.hasCodec(codecs, RTPHandler.CODEC_G729a)) {
            currentCodec = RTPHandler.CODEC_G729a;
        } else if (Signalling.hasCodec(codecs, RTPHandler.CODEC_G711)) {
            currentCodec = RTPHandler.CODEC_G711;
        } else {
            return;
        }
        codec = currentCodec;
        media.flush();
        rtpHandler.startRTPSession(remotertphost, remotertpport, codec);
        buttons[14].setSelected(false);
        if (IS_INCALL) {
            buttons[13].setSelected(true);
        } else {
            buttons[13].setSelected(false);
        }
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
    }

    public void rejectSIPCall() {
        IS_INCALL = false;
        IS_TRYING = false;
        rtpHandler.stopRTPSession();
        rtpHandler = null;
        callID = "";
    }

    public void startKeepAliveTimer() {
        keepAliveTimer = new java.util.Timer();
        keepAliveTimer.scheduleAtFixedRate(new SendKeepAliveRequest(), 0, 30 * 1000);
    }

    public class SendKeepAliveRequest extends java.util.TimerTask {

        public void run() {
            if (ua == null) {
                return;
            }
            ua.keepAliveUA();
        }
    }

    public class DurationCounter extends java.util.TimerTask {

        public void run() {
            duration++;
            durationValLabel.setText(getCallDuration(duration));
        }
    }

    public String getCallDuration(int callDuration) {
        int h = callDuration / 3600;
        callDuration = callDuration - h * 3600;
        int i = (int) (callDuration % 60),
                m = callDuration / 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (i < 10 ? "0" + i : i);
    }

    public String getCallDuration(String callDuration) {
        try {
            return getCallDuration(Integer.parseInt(callDuration));
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        return "-- : -- : --";
    }

    public class startRegisterRetryProcessing extends java.util.TimerTask {

        public void run() {
            if (ua == null) {
                return;
            }
            ua.reRegisterUA();
            registerRetryCounter = 0;
            if (regRetryTimer != null) {
                regRetryTimer = new java.util.Timer();
                regRetryTimer.schedule(new ProcessRegisterRetry(), 1000);
            }
        }
    }

    public class ProcessRegisterRetry extends java.util.TimerTask {

        public void run() {
            boolean again = false;
            if (ua == null) {
                return;
            }
            if (IS_UNAUTH) {
                return;
            }
            if (ua.isRegistered()) {
                ua.reRegisterUA();
                again = true;
            }
            registerRetryCounter++;
            if ((again) && (registerRetryCounter < 5)) {
                regRetryTimer = new java.util.Timer();
                regRetryTimer.schedule(new ProcessRegisterRetry(), 3000);
            } else {
                if (ua == null) {
                    return;
                }
                if (IS_UNAUTH) {
                    return;
                }
                if (ua.isRegistered()) {
                    IS_UNAUTH = true;  //server not responding after 5 attempts to register
                }
            }
            regRetryTimer = null;
        }
    }

    public void setRegistered() {
        if (callingStatus.length() == 0 || !IS_INCALL) {
            buttons[13].setEnabled(true);
            callingStatus = "Ready To Call.....";
        }
        buttons[14].setSelected(false);
        if (IS_INCALL) {
            buttons[13].setSelected(true);
        } else {
            buttons[13].setSelected(false);
        }
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
        IS_REGISTERRED = true;
    }

    public void setUnauthorized() {
        callingStatus = "Unauthorized...";
        IS_UNAUTH = true;
        buttons[14].setSelected(false);
        if (IS_INCALL) {
            buttons[13].setSelected(true);
        } else {
            buttons[13].setSelected(false);
        }
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
        registrationError = "Unauthorized User.....";
        IS_REGISTERRED = false;
    }

    public void setTrying(String callid) {
        if ((IS_INCALL) && (!IS_TRYING)) {
            if (callID.equals(callid)) {
                IS_TRYING = true;
                callingStatus = "Trying To Call.....";
                buttons[14].setSelected(false);
                buttons[13].setSelected(true);
                if (calledNumber.equals("")) {
                    calledNumber = phoneNumberBox.getText();
                } else {
                    phoneNumberBox.setText(calledNumber);
                }
                statusLabel.setText(callingStatus);
            }
        }
    }

    public void setCommandReceived(String callid, String commands[]) {
        if (commands == null || commands.length < 1) {
            return;
        }
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].startsWith("Account state :")) {
                String balance = commands[i].substring(15).trim();
                balanceValLabel.setText(balance);
                continue;
            }
            else if (commands[i].startsWith("Last call cost :")) {
                String balance = commands[i].substring(17).trim();
                costValLabel.setText(balance);
                continue;
            }              
        }
    }

    public void setRinging(String callid, String remotertphost, int remotertpport, int codecs[]) {
        if ((IS_INCALL)) {
            if (callID.equals(callid)) {

                if (remotertphost != null) {
                    acceptOutboundSIPCall(remotertphost, remotertpport, codecs);
                    IS_200OK = true;
                    IS_RINGING = false;
                } else {
                    IS_RINGING = true;
                }
                if ((IS_RINGING)) {
                    return;
                }
                callingStatus = "Ringing.....";

                buttons[14].setSelected(false);

                buttons[13].setSelected(false);
                if (calledNumber.equals("")) {
                    calledNumber = phoneNumberBox.getText();
                } else {
                    phoneNumberBox.setText(calledNumber);
                }
                statusLabel.setText(callingStatus);
            }
        }
    }

    public void setAccepted(String callid, String remotertphost, int remotertpport, int codecs[]) {
        if (remotertphost == null) {
            return;
        }
        if (!IS_INCALL) {
            return;
        }
        if (!callID.equals(callid)) {
            return;
        }

        if (Signalling.hasCodec(codecs, RTPHandler.CODEC_G711) && Signalling.hasCodec(codecs, RTPHandler.CODEC_G729a)) {
            ua.reinvite(callid, rtpHandler.getLocalRTPPort(), new int[]{RTPHandler.CODEC_G729a});
            return;
        }
        callingStatus = "Connected.....";
        buttons[14].setSelected(false);
        buttons[13].setSelected(false);

        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
        acceptOutboundSIPCall(remotertphost, remotertpport, codecs);
        IS_200OK = true;
        IS_RINGING = false;
        duration = 0;
        durationCounter = new java.util.Timer();
        durationCounter.scheduleAtFixedRate(new DurationCounter(), 0, 1 * 1000);
        return;
    }

    public void setBadCodec(String callid) {
        if (IS_INCALL) {
            return;
        }
        if (callID.equals(callid)) {
            return;
        }
        if (preferredCodec == RTPHandler.CODEC_G711) {
            rejectSIPCall();
            return;
        }
        preferredCodec = RTPHandler.CODEC_G711;
        callID = ua.sendInvite(calledNumber, rtpHandler.getLocalRTPPort(), new int[]{RTPHandler.CODEC_G711});
        return;
    }

    public void setBye(String callid) {
        if (IS_INCALL) {
            if (callID.equals(callid)) {
                String cdr = orgCalledNumber + "," + getCallDuration(duration);
                calledNumber = "";
                orgCalledNumber = "";
                buttons[13].setEnabled(true);
                callingStatus = "Ready To Call...";
                endDurationCounter();
                if (duration > 0) {
                    Utils.saveSuccessfulCallFile(cdr);
                }
                duration = 0;
                IS_TRYING = false;
                IS_RINGING = false;
                IS_INCOMING = false;
                IS_INCALL = false;
                IS_200OK = false;
                rtpHandler.stopRTPSession();
                rtpHandler = null;
                callID = "";
                buttons[14].setSelected(false);
                buttons[13].setSelected(true);

                if (calledNumber.equals("")) {
                    calledNumber = phoneNumberBox.getText();
                } else {
                    phoneNumberBox.setText(calledNumber);
                }
                statusLabel.setText(callingStatus);
            }
        }
    }

    public void setFailed(String callid) {
        if ((IS_INCALL) && (!IS_200OK)) {
            if (callID.equals(callid)) {
                rejectSIPCall();
            }
        }
    }

    public int setInvited(String callid, String fromid, String fromnumber, String remotertphost, int remotertpport, int codecs[]) {
        if (IS_INCALL) {
            if (callID.equals(callid)) {
                terRTPHost = remotertphost;
                terRTPPort = remotertpport;
                rtpHandler.changeRemoteInfo(remotertphost, remotertpport);
                return 200;
            }
            return 486;
        }
        calledNumber = fromnumber;
        callerID = fromid;
        if ((callerID == null) || (callerID.trim().length() == 0)) {
            callerID = "Unknown";
        }
        callingStatus = fromid + " Is Calling.....";
        IS_INCOMING = true;
        terRTPHost = remotertphost;
        terRTPPort = remotertpport;
        callID = callid;
        IS_RINGING = true;
        codecList = codecs;
        buttons[14].setSelected(false);
        buttons[13].setSelected(false);
        if (calledNumber.equals("")) {
            calledNumber = phoneNumberBox.getText();
        } else {
            phoneNumberBox.setText(calledNumber);
        }
        statusLabel.setText(callingStatus);
        return 180;  //reply RINGING
    }

    public void setCancelled(String callid, int code) {
        if (callID.equals(callid)) {
            calledNumber = "";
            buttons[13].setEnabled(true);
            callingStatus = "Ready To Call";
            endDurationCounter();
            IS_RINGING = false;
            IS_INCOMING = false;
            IS_INCALL = false;
            String cdr = orgCalledNumber + "," + getCallDuration(duration);
            calledNumber = "";
            orgCalledNumber = "";
            buttons[13].setEnabled(true);
            callingStatus = "Ready To Call...";
            endDurationCounter();
            if (duration > 0) {
                Utils.saveSuccessfulCallFile(cdr);
            }
            duration = 0;
            IS_TRYING = false;
            IS_RINGING = false;
            IS_INCOMING = false;
            IS_INCALL = false;
            IS_200OK = false;
            rtpHandler.stopRTPSession();
            rtpHandler = null;
            callID = "";
            buttons[14].setSelected(false);
            buttons[13].setSelected(false);

            if (calledNumber.equals("")) {
                calledNumber = phoneNumberBox.getText();
            } else {
                phoneNumberBox.setText(calledNumber);
            }
            statusLabel.setText(callingStatus);
            buttons[14].setSelected(false);
            buttons[13].setSelected(false);
            if (calledNumber.equals("")) {
                calledNumber = phoneNumberBox.getText();
            } else {
                phoneNumberBox.setText(calledNumber);
            }
            statusLabel.setText(callingStatus);
        }
    }

    public void endDurationCounter() {
        if (durationCounter != null) {
            durationCounter.cancel();
        }
    }
}
