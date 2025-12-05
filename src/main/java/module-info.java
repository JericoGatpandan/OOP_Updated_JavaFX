module com.example.oop_updated_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.oop_updated_javafx to javafx.fxml;
    exports com.example.oop_updated_javafx;
}