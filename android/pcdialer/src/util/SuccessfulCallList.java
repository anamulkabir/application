package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class SuccessfulCallList {

    private static SuccessfulCallList successfulCallList = null;
    String columnNames[] = {"Called Number", "Duration"};
    String successfulCall[][] = null;
    JFrame listFrame = null;
    JTable listTable = null; 
    JScrollPane scrollPane = null;

    private SuccessfulCallList() {
        listFrame = new JFrame();
        listFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/resources/main_background.png"));
        Image image = imageIcon.getImage();
        listFrame.setIconImage(image);
        listFrame.setTitle("Flamma PC Dialer :: Successful Call List");
        listFrame.setLocationRelativeTo(null);
        successfulCall = new String[20][2];              
    }

    public static SuccessfulCallList getInstance() {
        if (successfulCallList == null) {
            createSuccessfulCallList();
        }
        return successfulCallList;
    }

    private synchronized static void createSuccessfulCallList() {
        if (successfulCallList == null) {
            successfulCallList = new SuccessfulCallList();
        }
    }

    public synchronized void getList(ArrayList<String> list) {
        int length = list.size();
        for (int iY = 0; iY < length; iY++) {
            String str = list.get(length-(iY+1));
            for (int iX = 0; iX < 2; iX++) {
                if(iX==0)
                {    
                  successfulCall[iY][iX] = str.substring(0,str.indexOf(","));
                }  
                else
                {    
                  successfulCall[iY][iX] = str.substring(str.indexOf(",")+1);
                }                 
            }
        }
        listTable = new JTable(successfulCall, columnNames);
        listTable.setGridColor(Color.BLUE);
        listTable.setBackground(Color.DARK_GRAY);
        listTable.setForeground(Color.ORANGE);
        scrollPane = new JScrollPane(listTable);
        listFrame.add(scrollPane, BorderLayout.CENTER);         
        listFrame.setState(Frame.NORMAL);
        listFrame.setSize(270, 330);
        listFrame.setVisible(true); 
    }
}