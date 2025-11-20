package comp3111.examsystem.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


/**
 * Utility class for displaying messages and confirmation dialogs.
 */
public class MsgSender {

    /**
     * Displays an informational message in an alert dialog.
     *
     * @param msg the message to display
     */
    static public void showMsg(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("Hint");
        alert.headerTextProperty().set(msg);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with a specified title and message.
     * If the user confirms, the provided callback is executed.
     *
     * @param title    the title of the confirmation dialog
     * @param msg      the message to display in the confirmation dialog
     * @param callback the callback to execute if the user confirms
     */
    static public void showConfirm(String title, String msg, Runnable callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(msg);
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            callback.run();
        }
    }
}
