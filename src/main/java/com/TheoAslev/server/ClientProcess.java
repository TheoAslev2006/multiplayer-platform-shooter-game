package com.TheoAslev.server;


import com.TheoAslev.graphics.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class ClientProcess implements Serializable, Runnable {
    private Socket client;
    Thread thread;
    Game game;
    InputStreamReader inputStream;
    OutputStreamWriter outputStream;
    public JSONObject jsonPackage;
    public ClientProcess(Socket client, Game game) {
        jsonPackage = new JSONObject();
        this.client = client;
        this.game = game;
        try {
            inputStream = new InputStreamReader(client.getInputStream());
            outputStream = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public JSONArray writeJson(){
        JSONArray playerJson = new JSONArray();

        for (int i = 0; i < game.players.size(); i++) {
            playerJson.put(i);
            playerJson.put(game.players.get(i).getX() + "," + game.players.get(i).getY());
        }
        return playerJson;
    }

    public void sendData() throws IOException {

        JSONObject playerData = new JSONObject();
        playerData.put("id", 0);
        playerData.put("Coordinates", game.players.getFirst().getX()+ "," + game.players.getFirst().getY());

        jsonPackage.put("playerData", playerData);
        jsonPackage.put("players", writeJson());
        outputStream.write(jsonPackage.toString());
        outputStream.flush();
    }

    public void receiveData() throws IOException {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String unParsedJson = s.hasNext() ? s.next() : "";
        if (!unParsedJson.isEmpty()){
            jsonPackage = new JSONObject(unParsedJson);
        }else {
            System.out.println("no data received");
        }
    }



    @Override
    public void run() {
        while(!client.isClosed()) {
            try {
                client.setSoTimeout(100);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            System.out.println("hello");
            try {
                sendData();
                receiveData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                addInstanceToServerQueue(jsonPackage);
                System.out.println("ServerQueue size: " + game.serverQueue.size());
            }
        }
    }

    public void addInstanceToServerQueue(JSONObject request) {
        game.serverQueue.add(request);
    }
}
