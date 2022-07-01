package CafeMangementSystem.Controllers;

import CafeMangementSystem.Utils.SessionUser;
import CafeMangementSystem.Utils.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminSceneController implements Initializable {
    @FXML
    private GridPane adminSceneRoot;

    @FXML
    private Pane containerPane;

    @FXML
    private Label tenChuQuanLabel;

    @FXML
    private Button goToQLMonButton;

    @FXML
    private Button goToQLThongKeButton;

    @FXML
    private Button goToQLNhanvienButton;

    @FXML
    private Button logoutButton;

    public AdminSceneController() {
        this.load();
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AdminScene.fxml"));
        try {
            if (loader.getController() == null) {
                loader.setController(this);
            }
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tenChuQuanLabel.setText(SessionUser.getInstance().getNhanvien().getTennv());

        // mặc định là giao diện quản lý món
        goToQLMonButton.setDisable(true); // tắt
        goToQLNhanvienButton.setDisable(false); // bật
        goToQLThongKeButton.setDisable(false); // bật

        containerPane.getChildren().clear();

        QuanLyMonAnController quanLyMonAnController = new QuanLyMonAnController();
        containerPane.getChildren().add(quanLyMonAnController.getRoot());
    }

    @FXML
    private void goToQLMon() {
        goToQLMonButton.setDisable(true); // tắt
        goToQLNhanvienButton.setDisable(false); // bật
        goToQLThongKeButton.setDisable(false); // bật

        containerPane.getChildren().clear();

        QuanLyMonAnController quanLyMonAnController = new QuanLyMonAnController();
        containerPane.getChildren().add(quanLyMonAnController.getRoot());
    }

    @FXML
    private void goToQLNhanvien() {
        goToQLNhanvienButton.setDisable(true); // tắt
        goToQLMonButton.setDisable(false); // bật
        goToQLThongKeButton.setDisable(false); // bật

        containerPane.getChildren().clear();

        QLNhanVienController qlNhanVienController = new QLNhanVienController();
        containerPane.getChildren().add(qlNhanVienController.getRoot());
    }

    @FXML
    private void goToQLThongKe() {
        goToQLThongKeButton.setDisable(true); // tắt
        goToQLNhanvienButton.setDisable(false); // bật
        goToQLMonButton.setDisable(false); // bật

        containerPane.getChildren().clear();

        ThongKeControllers thongKeControllers = new ThongKeControllers();
        containerPane.getChildren().add(thongKeControllers.getRoot());

    }

    @FXML
    private void logout(ActionEvent actionEvent) {
        Utilities.getInstance().logout(actionEvent);
    }

}
