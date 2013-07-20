package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Utils {
    public static final String SUCCESSFUL_CALL_FILE = "successCall.dat";
    public static final String PHONE_NUMBER_FILE = "phoneNumber.dat";
    public static ArrayList<String> successfulCallList = new ArrayList<String>();
    public static ArrayList<String> phoneNumberList = new ArrayList<String>();
    
        public static int atoi(String str) {
        if (str.length() == 0) {
            return 0;
        }
        try {
            if (str.charAt(0) == '+') {
                return Integer.parseInt(str.substring(1));
            }
            return Integer.parseInt(str);
        } catch (Exception e) {
            return -1;
        }
    }
        
    public static int convertBytesToInteger(byte data[], int offset) {
        int ret;
        ret = (int) data[offset + 3] & 0xff;
        ret += ((int) data[offset + 2] & 0xff) << 8;
        ret += ((int) data[offset + 1] & 0xff) << 16;
        ret += ((int) data[offset + 0] & 0xff) << 24;
        return ret;
    }

    public static void convertIntegerToBytes(byte data[], int offset, int value) {
        data[offset + 3] = (byte) (value & 0xff);
        value >>= 8;
        data[offset + 2] = (byte) (value & 0xff);
        value >>= 8;
        data[offset + 1] = (byte) (value & 0xff);
        value >>= 8;
        data[offset + 0] = (byte) (value & 0xff);
    }        

    public static void readPhoneNumberList() {
        BufferedReader reader = null;
        try {
            File file = new File(PHONE_NUMBER_FILE);
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String s;
                while ((s = reader.readLine()) != null) {
                    try {
                        phoneNumberList.add(s);
                    } catch (Exception ex) {
                    }
                }
                reader.close();
            }
        } catch (Exception ex) {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    public static int savePhoneNumberList(String currentCallaedNumber) {
        int length = 0;
        BufferedWriter outFile = null;
        try {
            phoneNumberList.add(currentCallaedNumber);
            outFile = new BufferedWriter(new FileWriter(PHONE_NUMBER_FILE));
            if (phoneNumberList != null && phoneNumberList.size() > 0) {
                length = phoneNumberList.size();
                for (int i = 0; i < length-1; i++) {
                    if (phoneNumberList.get(i).equals(currentCallaedNumber)) {
                        phoneNumberList.remove(i);
                        length--;
                        break;
                    }
                }
                if (length > 10) {
                    for (int i = 0; i < length - 10; i++) {
                        phoneNumberList.remove(i);
                    }
                    length = 10;
                }
                outFile.write(phoneNumberList.get(0));
                for (int i = 1; i < length; i++) {
                    outFile.write("\n" + phoneNumberList.get(i));
                }
            }
            outFile.close();
        } catch (Exception ex) {
            try {
                outFile.close();
            } catch (Exception e) {
            }
        }
        return length;
    }

    public static void readSuccessfulCallFile() {
        BufferedReader reader = null;
        try {
            File file = new File(SUCCESSFUL_CALL_FILE);
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String s;
                while ((s = reader.readLine()) != null) {
                    try {
                        successfulCallList.add(s);
                    } catch (Exception ex) {
                    }
                }
                reader.close();
            }
        } catch (Exception ex) {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    public static void saveSuccessfulCallFile(String cdrStr) {
        BufferedWriter outFile = null;
        try {
            successfulCallList.add(cdrStr);
            outFile = new BufferedWriter(new FileWriter(SUCCESSFUL_CALL_FILE));
            if (successfulCallList != null && successfulCallList.size() > 0) {
                int length = successfulCallList.size();
                if (length > 20) {
                    for (int i = 0; i < length - 20; i++) {
                        successfulCallList.remove(i);
                    }
                    length = 20;
                }
                outFile.write(successfulCallList.get(0));
                for (int i = 1; i < length; i++) {
                    outFile.write("\n" + successfulCallList.get(i));
                }
            }
            outFile.close();
        } catch (Exception ex) {
            try {
                outFile.close();
            } catch (Exception e) {
            }
        }
    }
}
