module com.example.xworkshopunpackgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.xworkshopunpackgui to javafx.fxml;
    exports com.example.xworkshopunpackgui;
}