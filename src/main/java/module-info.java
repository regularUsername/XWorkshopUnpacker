module com.example.xworkshopunpackgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;

    opens com.example.xworkshopunpackgui to javafx.fxml;
    exports com.example.xworkshopunpackgui;
}