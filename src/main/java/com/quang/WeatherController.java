package com.quang;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;

import java.io.*;
import java.net.Socket;

public class WeatherController {

    @FXML
    private ComboBox<String> cityCombo;

    @FXML
    private Button fetchBtn;

    @FXML
    private TextArea resultArea;

    @FXML
    public void initialize() {
        cityCombo.getItems().addAll("DANANG", "HANOI", "HCM");
        cityCombo.getSelectionModel().selectFirst();
    }

    @FXML
    public void onFetchWeather() {
        String city = cityCombo.getValue();

        try (Socket socket = new Socket("127.0.0.1", 5000);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            out.write("GET_WEATHER|" + city);
            out.newLine();
            out.flush();

            String first = in.readLine();
            if (!first.startsWith("OK|")) {
                resultArea.setText("Server Error: " + first);
                return;
            }

            StringBuilder data = new StringBuilder();
            String line;

            while (!(line = in.readLine()).equals("END")) {
                data.append(line).append("\n");
            }

            resultArea.setText(data.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}
