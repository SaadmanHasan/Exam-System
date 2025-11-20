module comp3111.examsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.base;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens comp3111.examsystem to javafx.fxml;
    exports comp3111.examsystem;
    opens comp3111.examsystem.controller to javafx.fxml;
    exports comp3111.examsystem.controller;
    opens comp3111.examsystem.Classes to com.fasterxml.jackson.databind, javafx.base;

    exports comp3111.examsystem.Utils;
    //added for the JavaDoc --> Testing
    requires org.junit.jupiter.api;
    requires java.management; // Add this line

}