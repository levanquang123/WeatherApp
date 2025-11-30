package com.quang;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.*;
import java.net.Socket;

public class WeatherController {

    @FXML private ComboBox<String> cityCombo;
    @FXML private Button fetchBtn;
    @FXML private TextArea resultArea;

    // WEATHER UI LABELS
    @FXML private Label tempLabel;
    @FXML private Label statusLabel;

    @FXML private Label windLabel;
    @FXML private Label humidityLabel;
    @FXML private Label cloudLabel;
    @FXML private Label pressureLabel;
    @FXML private Label uvLabel;
    @FXML private Label visibilityLabel;

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

            StringBuilder sb = new StringBuilder();
            String line;

            String temp = "--";
            String status = "---";

            while (!(line = in.readLine()).equals("END")) {
                sb.append(line).append("\n");

                if (line.startsWith("Temperature:"))
                    temp = line.split(":")[1].trim();

                if (line.startsWith("Weather Status:"))
                    status = line.split(":")[1].trim();

                if (line.startsWith("Wind Speed"))
                    windLabel.setText(line.split(":")[1].trim());

                if (line.startsWith("Humidity"))
                    humidityLabel.setText(line.split(":")[1].trim());

                if (line.startsWith("Cloud Cover"))
                    cloudLabel.setText(line.split(":")[1].trim());

                if (line.startsWith("Pressure"))
                    pressureLabel.setText(line.split(":")[1].trim());

                if (line.startsWith("UV Index"))
                    uvLabel.setText(line.split(":")[1].trim());

                if (line.startsWith("Visibility"))
                    visibilityLabel.setText(line.split(":")[1].trim());
            }

            // Set main fields
            tempLabel.setText(temp);
            statusLabel.setText(status);

            // Show raw text
            resultArea.setText(sb.toString());

        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
}
