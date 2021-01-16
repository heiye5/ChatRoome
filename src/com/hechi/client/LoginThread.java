package com.hechi.client;


import com.hechi.util.MD5;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * 登录线程
 */
public class LoginThread extends Thread {
    private JFrame loginf;

    private JTextField t;

    public void run() {
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("登录名:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("liwei");
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("密码:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("liwei1234");
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("退  出");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("登  录");
        loginp.add(b2);

        loginf.setVisible(true);

        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener {
            private String sql = "";

            public void actionPerformed(ActionEvent e) {
                String username = loginname.getText();
                String password = loginPassword.getText();

//                System.out.println(username + password);

                String url = "jdbc:oracle:thin:@localhost:1521:orcl";
                String dbUserName = "opts";
                String dbPassword = "opts1234";
                sql = "select * from users where username = ?";

                try {
                    Connection connection = DriverManager.getConnection(url,dbUserName,dbPassword);
//                    System.out.println(connection);
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1,username);
                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        try {
                            if(MD5.checkpassword(password,rs.getString("password"))){
                                //获取本机ip
                                InetAddress ip = InetAddress.getLocalHost();
                                //设置端口，被占用+1
                                int port = 1688;
                                while(true) {
                                    try {
                                        ServerSocket serverSocket = new ServerSocket(port);
                                        break;
                                    } catch (IOException ex) {
                                        port +=1;
                                    }
                                }
                                //在数据库添加ip和端口
                                sql = "update users set ip = ?,port = ?,status=? where username = ?";
                                ps = connection.prepareStatement(sql);
                                ps.setString(1, String.valueOf(ip));
                                ps.setInt(2,port);
                                ps.setInt(3,1);
                                ps.setString(4,username);
                                ps.executeUpdate();

                                //窗口隐藏,public void setVisible(boolean aFlag)：使该组件可见或不可见。
                                loginf.setVisible(false);
                                ChatThreadWindow  chatThreadWindow = new ChatThreadWindow(username);

                            }else{
                                System.out.println("登录失败");
                            }
                        } catch (NoSuchAlgorithmException ex) {
                            ex.printStackTrace();
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        } catch (UnknownHostException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
//                try {
//                    String url = "jdbc:oracle:thin:@localhost:1521:orcl";
//                    String username_db = "opts";
//                    String password_db = "opts1234";
//                    Connection conn = DriverManager.getConnection(url, username_db, password_db);
//                    String sql = "SELECT password FROM users WHERE username=?";
//                    PreparedStatement pstmt = conn.prepareStatement(sql);
//                    pstmt.setString(1,username);
//                    ResultSet rs = pstmt.executeQuery();
//                    if (rs.next()) {
//                        String encodePassword = rs.getString("PASSWORD");
//                        if (MD5.checkpassword(password, encodePassword)) {
//                            System.out.println("登录成功");
//                        } else {
//                            System.out.println("登录失败");
//                        }
//                    }
//                } catch (SQLException ee) {
//                    ee.printStackTrace();
//                } catch (NoSuchAlgorithmException ex) {
//                    ex.printStackTrace();
//                } catch (UnsupportedEncodingException ex) {
//                    ex.printStackTrace();
//                }
				/*
				1、根据用户去数据库把加密后的密码拿到
				SELECT password FROM users WHERE username='liwei';
				2、把登录界面输入的密码和数据库里加密后的进行比对（调用MD5类的checkpassword方法）
				 */
            }
        }
        ButtonListener bl = new ButtonListener();
        b2.addActionListener(bl);
        loginname.addActionListener(bl);
        loginPassword.addActionListener(bl);
    }
}