package com.example.cloudapplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.example.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    public ListView<String> clientView;
    @FXML
    public ListView<String> serverView;
    @FXML
    public Button upload;
    @FXML
    public Button download;
    @FXML
    public Button upButtonClient;
    @FXML
    public Button upButtonServer;

    private final String homeDir = "clientFiles/";
    private String currentDir = Path.of(homeDir).toAbsolutePath().toString();

    private Network network;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(currentDir));

            network = new Network(8189);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;

        List<String> result = new ArrayList<>(Arrays.asList(list));
        result.add(0, "..");
        return result;
    }


    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = network.read();
                if (message instanceof ListFiles listFiles) {
                    Platform.runLater(() -> {
                        listFiles.getFiles().add(0, "..");
                        serverView.getItems().clear();
                        serverView.getItems().addAll(listFiles.getFiles());
                    });
                } else if (message instanceof FileMessage fileMessage) {
                    Path current = Path.of(currentDir).resolve(fileMessage.getName());
                    Files.write(current, fileMessage.getData());
                    Platform.runLater(() -> {
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getFiles(currentDir));
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Connection lost");
            e.printStackTrace();
        }
    }


    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientView.getSelectionModel().getSelectedItem();
        network.write(new FileMessage(Path.of(currentDir).resolve(file)));
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String file = serverView.getSelectionModel().getSelectedItem();
        network.write(new FileRequest(file));
    }

    public void pathUpClient(ActionEvent actionEvent) {
        currentDir = Path.of(currentDir).resolve("..").normalize().toString();
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(currentDir));
        });
    }


    public void changePathClient(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2 &&
                Files.isDirectory(Path.of(currentDir).resolve(clientView.getSelectionModel().getSelectedItem()).normalize())) {

            currentDir = Path.of(currentDir)
                    .resolve(clientView.getSelectionModel().getSelectedItem())
                    .normalize()
                    .toString();
            Platform.runLater(() -> {
                clientView.getItems().clear();
                clientView.getItems().addAll(getFiles(currentDir));
            });
        }

    }

    public void pathUpRequest(ActionEvent actionEvent) throws IOException {
        network.write(new PathUpRequest());
    }

    public void changePathOnServerRequest(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            network.write(new ChangePathOnServerRequest(serverView.getSelectionModel().getSelectedItem()));
        }
    }
}