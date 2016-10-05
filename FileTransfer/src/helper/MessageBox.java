package helper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MessageBox
{

    public static void Show(String infoMessage, String titleBar)
    {
        Show(infoMessage, titleBar, null);
    }

    public static void Show(String infoMessage, String titleBar, String headerMessage)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
}