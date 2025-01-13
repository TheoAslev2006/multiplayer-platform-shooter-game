package com.TheoAslev.server;

//exception class that gets triggered in an event where the server queue is too long
public class ServerQueueOutOfBounds extends Exception {
    public ServerQueueOutOfBounds(String message) {
        super(message);
    }
}
