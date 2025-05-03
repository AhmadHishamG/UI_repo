module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;

    // Gson (automatic module name)



    opens com.example.ui to javafx.fxml;
    exports com.example.ui;
}