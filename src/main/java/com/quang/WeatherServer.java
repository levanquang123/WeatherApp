package com.quang;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WeatherServer {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Weather Server running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " +
                        clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String req = in.readLine();
            System.out.println("Received: " + req);

            if (req == null || !req.startsWith("GET_WEATHER|")) {
                out.write("ERROR|Invalid command");
                out.newLine();
                out.flush();
                return;
            }

            String city = req.split("\\|")[1];
            String weather = WeatherFetcher.getWeather(city);

            // MULTILINE FORMAT
            out.write("OK|");
            out.newLine();
            out.write(weather);
            out.newLine();
            out.write("END");
            out.newLine();
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
