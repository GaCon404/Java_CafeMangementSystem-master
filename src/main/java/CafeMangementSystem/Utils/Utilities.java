package CafeMangementSystem.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

public class Utilities {
    private static Utilities instance;

    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    private Utilities() {}

    public boolean showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);

        if (alertType == Alert.AlertType.CONFIRMATION) {
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                return true;
            } else if (option.get() == ButtonType.CANCEL) {
                return false;
            }
        }
        else {
            alert.show();
        }

        return true;
    }

    public void logout(ActionEvent actionEvent) {
        // clean sessionUser
        SessionUser.getInstance().cleanSession();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
        Parent loginFormParent = null;

        try {
            loginFormParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node source = (Node) actionEvent.getSource();
        Stage loginStage = (Stage) source.getScene().getWindow();

        Scene loginScene = new Scene(loginFormParent);
        loginStage.setScene(loginScene);
        loginStage.show();
    }
}
