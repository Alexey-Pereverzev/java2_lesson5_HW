module com.example.java2_lesson5_hw {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java2_lesson5_hw to javafx.fxml;
    exports com.example.java2_lesson5_hw;
    exports com.example.java2_lesson5_hw.controllers;
    opens com.example.java2_lesson5_hw.controllers to javafx.fxml;
}