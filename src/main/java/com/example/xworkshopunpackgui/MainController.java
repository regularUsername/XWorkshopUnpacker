package com.example.xworkshopunpackgui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainController {
    @FXML
    private TextField inputField;
    @FXML
    private TextField outputField;
    @FXML
    private ListView<String> fileList;
    @FXML
    private Label somethingLabel;
    @FXML
    private Button unpackBtn;
    @FXML
    private ProgressIndicator progressIndicator;

    private Unpacker unpacker;

    // TODO add drag and drop support

    @FXML
    protected void onDragOver(DragEvent event) {
        var db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            inputField.setText(db.getFiles().get(0).getPath());
        } else {
            event.consume();
        }

    }
    @FXML
    protected void onDragDropped(DragEvent event) {
        System.out.println("drop detected");
        var db = event.getDragboard();
        var success = false;
        if (db.hasFiles()) {
            success = true;
            var path = db.getFiles().get(0);
            // TODO hier unpacker initialisieren und header lesen
            System.out.println(path);
        }
        event.setDropCompleted(success);
        event.consume();

    }

    @FXML
    protected void onBrowseInputClick() {
        var fc = new FileChooser();
        fc.setTitle("Open XRW Archive");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XRW Dat file", "*.dat"));
        var selectedFile = fc.showOpenDialog(inputField.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        unpacker = new Unpacker(selectedFile);
        try {
            unpacker.readHeader();
        } catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setTitle("Can't read file");
            alert.showAndWait();
            return;
        }
        fileList.getItems().setAll(unpacker.getFilenames());
        fileList.getItems().add("contents.xml");
        fileList.disableProperty().setValue(false);
        inputField.setText(selectedFile.getPath());
        outputField.setText(selectedFile.getPath().replace(".dat", ""));
        unpackBtn.disableProperty().setValue(false);
    }

    @FXML
    protected void onBrowseOutputClick() {
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        var selectedDirectory = directoryChooser.showDialog(outputField.getScene().getWindow());
        outputField.setText(selectedDirectory.getPath());
    }

    @FXML
    void onUnpackClick() {
        var task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(-1, 0);
                updateMessage("unpacking file...");

                unpacker.unpack(Paths.get(outputField.getText()));

                updateProgress(0, 0);
                updateMessage("done");
                return null;
            }

            @Override
            protected void failed() {
                updateMessage(getException().getMessage());
                new Alert(Alert.AlertType.ERROR, getException().getMessage()).showAndWait();
                updateProgress(0, 0);
            }
        };
        unpackBtn.disableProperty().bind(task.runningProperty());
        progressIndicator.visibleProperty().bind(task.runningProperty());
        somethingLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
}