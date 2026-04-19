module club.hsspace.hs.motorcontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.locationtech.jts;
    requires com.fazecast.jSerialComm;

    opens club.hsspace.hs.motorcontrol to javafx.fxml;
    exports club.hsspace.hs.motorcontrol;
}

