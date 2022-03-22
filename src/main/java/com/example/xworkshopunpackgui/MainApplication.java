package com.example.xworkshopunpackgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 600, 240);
//        scene.getStylesheets().add(MainApplication.class.getResource("style.css").toExternalForm());
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setTitle("X4 Workshop Mod Unpacker");
        stage.setScene(scene);
        new JMetro(scene,Style.DARK);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}