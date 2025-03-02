module com.example.MasterWorker {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.MasterWorker to javafx.fxml;
    exports com.example.MasterWorker;
}