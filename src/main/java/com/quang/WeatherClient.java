package com.quang;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class WeatherClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập mã city (DANANG / HANOI / HCM): ");
        String city = scanner.nextLine().trim();

        try (Socket socket = new Socket("127.0.0.1", 5000);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.write("GET_WEATHER|" + city);
            out.newLine();
            out.flush();

            String firstLine = in.readLine();
            if (!firstLine.startsWith("OK|")) {
                System.out.println("Server error: " + firstLine);
                return;
            }

            System.out.println("\n--- Weather Information ---");

            String line;
            while (!(line = in.readLine()).equals("END")) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
