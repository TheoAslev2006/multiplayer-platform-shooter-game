package com.TheoAslev.server;


import com.TheoAslev.character.Player;
import com.TheoAslev.graphics.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.WeakHashMap;


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
        Scanner s = new Scanner(System.in);
        try {
            inputStream = new InputStreamReader(client.getInputStream());
            outputStream = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            outputStream.write(game.name);
            outputStream.flush();
            System.out.println("sent name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        {

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
                                System.out.println("reading");

                                if (!inputStream.ready())
                                    break;

                            }
                            name = stringBuilder.toString().trim();
                            game.players.put(name, new Player(game, name));
                            break;
                        } catch (IOException e) {
                            System.err.println("Could not read json data");
                        }
                    }
                    System.out.println("Waiting for name data");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

//            while (true) {
//                try {
//                    System.out.println("stream is ready " + inputStream.ready());
//                    if (inputStream.ready())
//                        try {
//                            BufferedReader bufferedReader = new BufferedReader(inputStream);
//                            System.out.println("new reder");
//                            String name = null;
//                            if (bufferedReader.ready())
//                                name = bufferedReader.readLine();
//
//                            if (name != null) {
//                                System.err.println(name);
//                                game.players.put(name, new Player(game, name));
//                                break;
//                            }
//                        } catch (IOException e) {
//                            System.err.println("Error getting client name. ErrorMessage: " + e.getMessage());
//                        }
//                    System.out.println("Waiting for name data");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }

        }

    }

    public JSONArray writeJson() {
        JSONArray playerJson = new JSONArray();
        game.players.forEach((key, player) -> {
            playerJson.put(key);
            playerJson.put(player.getX() + "," + player.getY());
        });
        return playerJson;
    }

    public void sendData() throws IOException {

        jsonPackage.put("players", writeJson());
        System.out.println(jsonPackage);
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
                    System.out.println("reading");
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
            while (!client.isClosed()) {
                try {
                    sendData();
                    receiveData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    addInstanceToServerQueue(jsonPackage);
                    if (game.serverQueue.size() >= 100) {
                        game.serverQueue.poll();
                        System.out.println("game queue out of bounds");
                    }
                    System.out.println("ServerQueue size: " + game.serverQueue.size());
                }
                client.setSoTimeout(1000 / 16);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } finally {
            cleanupConnection();
        }

    }

    public void addInstanceToServerQueue(JSONObject request) {
        game.serverQueue.add(request);
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
