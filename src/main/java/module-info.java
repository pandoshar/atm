module org.example.atmsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens org.example.atmsimulator to javafx.fxml;
    exports org.example.atmsimulator;
    exports org.example.atmsimulator.Controllers;
    opens org.example.atmsimulator.Controllers to javafx.fxml;
}