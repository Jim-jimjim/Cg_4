module ru.vsu.cs.leonov_m.cg_4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.common;


    opens ru.vsu.cs.leonov_m.cg_4 to javafx.fxml;
    exports ru.vsu.cs.leonov_m.cg_4;
}