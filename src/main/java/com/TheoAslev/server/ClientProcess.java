package com.TheoAslev.server;


import com.TheoAslev.entity.Player;
import com.TheoAslev.graphics.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

//client process that connects server and client via a queue based system that hold information on other players position
public class ClientProcess implements Serializable, Runnable {
    private Socket client;
    public boolean isHosting;
    Game game;
    InputStreamReader inputStream;
    OutputStreamWriter outputStream;
    public JSONObject jsonPackage;

    //server constructor
    public ClientProcess(Socket client, Server server) {
        isHosting = true;
        jsonPackage = new JSONObject();
        this.client = client;
        this.game = server.game;
        //IO stream initialization
        try {
            inputStream = new InputStreamReader(client.getInputStream());
            outputStream = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //sending server name
        try {
            outputStream.write(game.name);
            outputStream.flush();
            System.out.println("Sent host name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //reading client name
        while (true) {
            try {
                String name;
                char[] buffer = new char[1024];
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
                        System.err.println("Could not read name");
                    }
                }
                System.out.println("reading name.");

            } catch (IOException e) {
                System.err.println("Error retrieving name from client, trying again");
            }
        }
    }

    // client constructor
    public ClientProcess(Socket client, Game game) throws Exception {
        isHosting = false;
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
        //sending client name
        try {
            outputStream.write(game.name);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //reading server name
        short iterations = 0;
        while (true) {
            try {
                String name;
                char[] buffer = new char[1024];
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
                        System.err.println("Could not read name");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error retrieving name from server, trying again");
            }
            iterations++;
            if (iterations == 1000) {
                throw new Exception();
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
            playerData.put("bullets", bulletJson);
            playerJson.put(playerData);
        });
        return playerJson;

    }

    public void sendData() throws IOException {
        //sends data to socket/server-socket with the help of an output stream
        jsonPackage.put("players", writePlayerJson());
        outputStream.write(jsonPackage.toString());
        outputStream.flush();
    }

    public void receiveData() throws IOException {
        //receives data with the help of an input stream, string builder to assemble the json, and a buffer to save memory when using the string builder
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
        //loop where client/server sends and receives player data to the server/client that gets added to a queue in the game loop
        try {
            client.setSoTimeout(1000 / 16);
            while (!client.isClosed()) {
                try {
                    sendData();
                    receiveData();
                } catch (IOException e) {
                    if (!isHosting)
                        System.err.println("Connection lost with server");
                    else
                        System.err.println("Connection lost with client");
                    System.err.println("Cleaning upp lost connection");
                    cleanupConnection();
                } finally {
                    try {
                        addInstanceToServerQueue(jsonPackage);
                    } catch (ServerQueueOutOfBounds e) {
                        System.out.println(e.getMessage());
                    }
                }
                Thread.sleep(1000 / 30);
            }
        } catch (SocketException e) {
            System.err.println("Socket error, cleaning up...");
            cleanupConnection();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted, cleaning up...");
        } finally {
            System.out.println("Cleaning up...");
            cleanupConnection();
        }

    }

    public void addInstanceToServerQueue(JSONObject request) throws ServerQueueOutOfBounds {
        //adds json that will get parsed in game class to a queue
        if (game.serverQueue.size() < 10)
            game.serverQueue.add(request);
        else throw new ServerQueueOutOfBounds("ServerQueue out of bounds with size: " + game.serverQueue.size());
    }

    public void cleanupConnection() {
        //shuts everything down in the socket connection
        while (true) {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                if (!client.isClosed()) {
                    client.close();
                }
                System.out.println("Disconnected");
                break;
            } catch (IOException e) {
                System.out.println("Error Closing server-client Connection, trying again");
            }
        }
    }
}
