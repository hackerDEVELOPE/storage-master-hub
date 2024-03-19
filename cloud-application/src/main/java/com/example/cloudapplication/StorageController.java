package com.example.cloudapplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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

public class StorageController implements Initializable {
    @FXML
    public ListView<String> clientView;
    @FXML
    public ListView<String> serverView;
    @FXML
    public Button upload;
    @FXML
    public Button download;
    @FXML
    public TextField sysText;
    @FXML
    public Button regButton;
    @FXML
    public Button authButton;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passField;
    @FXML
    public HBox authBox;
    @FXML
    public Button disconnectButton;

    private final String homeDir = "clientFiles/";


    private String currentDir = Path.of(homeDir).toAbsolutePath().toString();

    private Network network;

    private Stage regStage;
    private RegController regController;

    private boolean isAuthenticated;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(currentDir));

            network = new Network(8189);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

            sysText.setText("Welcome to Great Storage!");
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
                } else if (message instanceof AuthResponse authResponse) {
                    setAuthenticated(authResponse.isAuthenticated());
                }
            }
        } catch (Exception e) {
            System.out.println("Connection lost");
            e.printStackTrace();
        }
    }

    private void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
        System.out.println(authenticated);

        authBox.getChildren().forEach(x-> {
            x.setVisible(!authenticated);
            x.setManaged(!authenticated);
        });
        disconnectButton.setVisible(authenticated);
        disconnectButton.setManaged(authenticated);


        if (authenticated) setSysText("You was authenticated");
        else setSysText("Invalid login or password");
    }


    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientView.getSelectionModel().getSelectedItem();
        network.write(new FileMessage(Path.of(currentDir).resolve(file)));

        setSysText("file: " + file + " was sent to storage");
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String file = serverView.getSelectionModel().getSelectedItem();
        network.write(new FileRequest(file));

        setSysText("file: " + file + " was downloaded from storage");
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


    public void changePathOnServerRequest(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            network.write(new ChangePathOnServerRequest(serverView.getSelectionModel().getSelectedItem()));
        }
    }

    public void deleteFileOnClientSide(ActionEvent actionEvent) throws IOException {
        String fileToDelete = clientView.getSelectionModel().getSelectedItem();
        Files.delete(Path.of(currentDir).resolve(fileToDelete));
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(currentDir));
        });

        setSysText("file: " + fileToDelete + " was deleted");
    }

    public void deleteFileOnServerSide(ActionEvent actionEvent) throws IOException {
        String fileToDelete = serverView.getSelectionModel().getSelectedItem();
        network.write(new DeleteRequest(fileToDelete));

        setSysText("file: " + fileToDelete + " was deleted");
    }

    private void setSysText(String msg) {
        sysText.clear();
        sysText.setText(msg);
    }

    public void regStage(ActionEvent actionEvent) {
        if (regStage == null) {
            createRegStage();
        }
        regStage.show();
    }

    private void createRegStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StorageApp.class.getResource("/com/example/cloudapplication/reg.fxml"));
            Parent root = fxmlLoader.load();

            regStage = new Stage();
            regStage.setTitle("Great Storage registration");
            regStage.setScene(new Scene(root, 600, 500));

            regController = fxmlLoader.getController();
            regController.setController(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registration(String login, String password) {
        try {
            network.write(new RegistrationRequest(login, password));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        try {
            network.write(new AuthRequest(loginField.getText(), passField.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(ActionEvent actionEvent) {
        setAuthenticated(false);
    }
}