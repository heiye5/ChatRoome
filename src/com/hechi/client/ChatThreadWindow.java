package com.hechi.client;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * 聊天线程
 */
public class ChatThreadWindow {
    private String name;
    private JComboBox cb;
    private JFrame f;
    private JTextArea ta;
    private JTextField tf;
    private static int total;// 在线人数统计

    public ChatThreadWindow(String username) {
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("聊天室" + " - " + username + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);

        showXXXIntoChatRoom();
    }

    public void showXXXIntoChatRoom() {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            String sql = "SELECT username,ip,port FROM users WHERE status='1' AND username!=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username=rs.getString("USERNAME");
                String ip = rs.getString("IP");
                int port = rs.getInt("PORT");
                System.out.println(ip);
                System.out.println(port);
                byte[] ipB = new byte[4];

                String ips[] = ip.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    ipB[i] = (byte)Integer.parseInt(ips[i]);
                }
                String message = username+"进入了聊天室";
                byte[] m = message.getBytes();
                DatagramPacket dp = new DatagramPacket(m, m.length);
                dp.setAddress(InetAddress.getByAddress(ipB));
                dp.setPort(port);
                DatagramSocket ds = new DatagramSocket();
                ds.send(dp);//投递
            }
        } catch (SQLException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}