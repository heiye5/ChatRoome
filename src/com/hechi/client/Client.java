package com.hechi.client;

public class Client {
    public static void main(String[] args) {
        LoginThread loginThread = new LoginThread();
        loginThread.run();
    }
}
