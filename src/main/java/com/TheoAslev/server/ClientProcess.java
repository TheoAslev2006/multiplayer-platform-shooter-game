package com.TheoAslev.server;


import com.TheoAslev.character.Player;
import com.TheoAslev.graphics.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


public class ClientProcess implements Serializable, Runnable {
    private Socket client;
    Game game;
    InputStreamReader inputStream;
    OutputStreamWriter outputStream;
    public JSONObject jsonPackage;

    public ClientProcess(Socket client, Game game) {
        jsonPackage = new JSONObject();
        this.client = client;
        this.game = game;
        //stream initialization
        try {
            inputStream = new InputStreamReader(client.getInputStream());
            outputStream = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //sending client/server name
        try {
            outputStream.write(game.name);
            outputStream.flush();
            System.out.println("sent name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //reading client/server name
        while (true) {
            try {
                String name;
                char[] buffer = new char[1028];
                StringBuilder stringBuilder = new StringBuilder();
                if (inputStream.ready()) {
                    int charsRead;
                    try {
                        while ((charsRead = inputStream.read(buffer)) > 0) {
                            stringBuilder.append(buffer, 0, charsRead);
                            if (!inputStream.ready())
                                break;
                            System.err.println("failed to read, trying again");
                        }
                        name = stringBuilder.toString().trim();
                        game.players.put(name, new Player(game, name));
                        break;
                    } catch (IOException e) {
                        System.err.println("Could not read json data");
                    }
                }
                System.err.println("waiting for name");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JSONArray writePlayerJson() {
        //writing all the json for players to send to server
        JSONArray playerJson = new JSONArray();
        game.players.forEach((key, player) -> {
            JSONObject playerData = new JSONObject();
            playerData.put("name", key);
            playerData.put("coords", player.getX() + "," + player.getY());
            JSONArray bulletJson = new JSONArray();
            int length = game.bulletsToServer.size();
            for (int i = 0; i < length; i++) {
                String bulletData = game.bulletsToServer.poll();
                bulletJson.put(bulletData);
            }
            System.err.println(bulletJson.length() + " bullets");
            playerData.put("bullets", bulletJson);
            playerJson.put(playerData);
        });
        return playerJson;

    }

    public void sendData() throws IOException {
        jsonPackage.put("players", writePlayerJson());
        outputStream.write(jsonPackage.toString());
        outputStream.flush();
    }

    public void receiveData() throws IOException {
        char[] buffer = new char[1024];
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream.ready())
            try {
                int charsRead;
                while ((charsRead = inputStream.read(buffer)) > 0) {
                    stringBuilder.append(buffer, 0, charsRead);
                    if (stringBuilder.toString().endsWith("}")) {
                        break;
                    }
                    System.err.println("reading failed, trying again: " + stringBuilder);
                }
                String jsonData = stringBuilder.toString();
                if (jsonData.isEmpty())
                    System.err.println("No data received");
                else {
                    jsonPackage = new JSONObject(jsonData);
                }

            } catch (IOException e) {
                System.err.println("Could not read json data");
            }

    }

    @Override
    public void run() {
        try {
            client.setSoTimeout(1000 / 16);
            while (!client.isClosed()) {
                try {
                    sendData();
                    receiveData();
                } catch (IOException e) {
                    System.err.println("Failed to send or retrieve data in " + this);
                } finally {
                    addInstanceToServerQueue(jsonPackage);
                    if (game.serverQueue.size() >= 100) {
                        game.serverQueue.poll();
                        System.out.println("game queue out of bounds");
                    }
                    System.out.println("ServerQueue size: " + game.serverQueue.size());
                }
                Thread.sleep(1000 / 30);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            cleanupConnection();
        }

    }

    public void addInstanceToServerQueue(JSONObject request) {
        if (game.serverQueue.size() < 100)
            game.serverQueue.add(request);
        else System.err.println("too big server queue size");
    }

    public void cleanupConnection() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (!client.isClosed()) {
                client.close();
            }
            System.out.println("Disconnected");
        } catch (IOException e) {
            System.out.println("Error Closing server Connection");
            throw new RuntimeException(e);
        }
    }
}
