module cloud.application {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.codec;
    requires cloud.model;

    opens com.example.cloudapplication;
    exports com.example.cloudapplication;
}