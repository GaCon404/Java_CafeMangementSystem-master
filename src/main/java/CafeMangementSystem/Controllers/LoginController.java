package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.NhanvienDAO;
import CafeMangementSystem.Entities.ChucVu;
import CafeMangementSystem.Entities.Nhanvien;
import CafeMangementSystem.Utils.SessionUser;
import CafeMangementSystem.Utils.Utilities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setDisable(true);
        passwordPasswordField.textProperty().addListener(((observableValue, oldvalue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        } ));
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        Window owner = loginButton.getScene().getWindow();

        if (usernameTextField.getText().isEmpty()) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, owner, "Đăng nhập thất bại", "Mời điền tên đăng nhập!");
            return;
        }

        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        // get hashedPassword to check if password was typed is right
        String hashedPassword = NhanvienDAO.getInstance().getHashedPassword(username);

        if (hashedPassword == null) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, owner, "Đăng nhập thất bại", "Tài khoản không tồn tại!");
            return;
        }

        boolean chk = checkPassword(password, hashedPassword);

        if (chk) {
            // password matches
            Nhanvien nhanvien = NhanvienDAO.getInstance().getAccountForSession(username, hashedPassword);

            if (!nhanvien.getTrangthai()) {
                Utilities.getInstance().showAlert(Alert.AlertType.WARNING, owner, "Đăng nhập thất bại", "Tài khoản hiện đang bị khóa!" +
                        "\nConnect to admin for more information!");
            } else {
                SessionUser.getInstance().setNhanvien(nhanvien); // set user's session

                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION, owner, "Đăng nhập thành công", "Chào mừng bạn!");

                // load home screen
                Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

                if (nhanvien.getChucvu().equals(ChucVu.ChuQuan.getName())) {
                    // load UI for Chủ quán
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AdminScene.fxml"));
                    loader.setController(new AdminSceneController());
                    Parent parent = null;
                    try {
                        parent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                } else if (nhanvien.getChucvu().equals(ChucVu.NVBanHang.getName())) {
                    // load UI for Nhân viên bán hàng
                    System.out.println("ĐĂNG NHẬP THÀNH CÔNG - LOAD MÀN HÌNH NHÂN VIÊN BÁN HÀNG");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Order.fxml"));
                    Parent parent = null;
                    try {
                        parent = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                }

                stage.show();
            }
        } else {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, owner, "Đăng nhập thất bại!", "Mật khẩu không đúng!");
        }

    }

    private boolean validateInfoFields() {
        if (usernameTextField.getText().trim().isEmpty()) {
            System.out.println("Mời điền tên đăng nhập");
            Utilities.getInstance().showAlert(Alert.AlertType.WARNING,
                    loginButton.getScene().getWindow(),"Thông tin chưa hợp lệ", "Mời điền tên đăng nhập");
            return false;
        }
        return true;
    }

    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (BCrypt.checkpw(plainTextPassword, hashedPassword)) {
            return true; // Password matches!
        }

        return false;
    }
}
