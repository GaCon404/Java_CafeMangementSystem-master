package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.NhanvienDAO;
import CafeMangementSystem.Entities.ChucVu;
import CafeMangementSystem.Entities.Nhanvien;
import CafeMangementSystem.Utils.SessionUser;
import CafeMangementSystem.Utils.Utilities;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.ResourceBundle;

public class QLNhanVienController implements Initializable {
    @FXML
    private GridPane quanLyNhanVienGridPane; // root

    // Search
    @FXML
    private TextField searchEmpTextField;

    @FXML
    private Button searchEmpButton;

    @FXML
    private Button deleteEmpButton;

    @FXML
    private Button refreshButton;

    // Empl table view
    @FXML
    private TableView<Nhanvien> nhanvienTableView;

    @FXML
    private TableColumn<Nhanvien, Integer> maNvCol;

    @FXML
    private TableColumn<Nhanvien, String> tenNvCol;

    @FXML
    private TableColumn<Nhanvien, String> dienThoaiCol;

    @FXML
    private TableColumn<Nhanvien, String> diaChiCol;

    @FXML
//    private TableColumn<Nhanvien, Date> ngaySinhCol; // custom DatePickerTableCell later!
    private TableColumn<Nhanvien, String> ngaySinhCol;

    @FXML
    private TableColumn<Nhanvien, ChucVu> chucVuCol;

    @FXML
    private TableColumn<Nhanvien, Boolean> trangThaiCol;

    private ObservableList<Nhanvien> nhanvienObservableList; // nhanvien data

    // Change password pane
    @FXML
    private TextField tenDangNhapTextField;

    @FXML
    private TextField matKhauMoiTextField;

    @FXML
    private TextField matKhauMoi2TextField;

    @FXML
    private Button updatePwButton;

    @FXML
    private Label changePwMsgLabel;

    // Tạo mới tài khoản
    @FXML
    private TextField newTenNvTextField;

    @FXML
    private TextField newDientThoaiTextField;

    @FXML
    private TextField newDiaChiTextField;

    @FXML
    private DatePicker newNgaySinhDatePicker;

    @FXML
    private ComboBox<ChucVu> newChucVuComboBox;

    @FXML
    private TextField newTenDangNhapTextField;

    @FXML
    private PasswordField newMatKhauPwField;

    @FXML
    private PasswordField newMatKhau2PwField;

    @FXML
    private Button createAccountButton;

//    @FXML
//    private Label newAccountInfoStatusLabel;

    @FXML
    private Label tenChuQuanLabel;

    @FXML
    private Button logoutButton;

    public Node getRoot() {
        return this.quanLyNhanVienGridPane;
    }

    public QLNhanVienController() {
        this.load();
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/QuanLyNhanVien.fxml"));
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
        initEmpTable();
    }

    private void initEmpTable() {
        // Hiển thị các dòng dữ liệu
        nhanvienObservableList = FXCollections.observableList(NhanvienDAO.getInstance().getAll());
        System.out.println("Danh sách nhân viên:");
        for (Nhanvien nv : nhanvienObservableList) {
            System.out.println(nv);
        }
        nhanvienTableView.setItems(nhanvienObservableList);

        /* Truyền dữ liệu vào "Đổi mật khẩu" tab */
        ObservableList<Nhanvien> selectedItems = nhanvienTableView.getSelectionModel().getSelectedItems();
        selectedItems.addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends Nhanvien> change) {
                if (change.getList().size() > 0) {
                    System.out.println("Selection changed: " + change.getList());
                    Nhanvien selectedInfo = change.getList().get(0);
                    setNhanvienInfoFromSelectedRow(selectedInfo); // hiển thị thông tin sang bên phải
                }
            }

            private void setNhanvienInfoFromSelectedRow(Nhanvien selectedInfo) {
                tenDangNhapTextField.setText(selectedInfo.getTendangnhap());
                changePwMsgLabel.setText("Mời điền mật khẩu mới");
                changePwMsgLabel.setTextFill(Color.BLACK);
            }
        });

        /* Set up "Đổi mật khẩu" tab */
        updatePwButton.setDisable(true);
        matKhauMoiTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (tenDangNhapTextField.getText().isEmpty()) {
                changePwMsgLabel.setText("Cần chọn một nhân viên để thay đổi mật khẩu");
                changePwMsgLabel.setTextFill(Color.DARKORANGE);
            } else {
                boolean changePwStatus = validateConfirmPw(matKhauMoi2TextField.getText(), newValue);
                if (!changePwStatus) {
                    changePwMsgLabel.setTextFill(Color.RED);
                } else {
                    // Hợp lệ
                    changePwMsgLabel.setText("Mật khẩu hợp lệ");
                    changePwMsgLabel.setTextFill(Color.GREEN);
                }
                updatePwButton.setDisable(!changePwStatus);
            }
        }));
        matKhauMoi2TextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (tenDangNhapTextField.getText().isEmpty()) {
                changePwMsgLabel.setText("Cần chọn một nhân viên để thay đổi mật khẩu");
                changePwMsgLabel.setTextFill(Color.DARKORANGE);
            } else {
                boolean changePwStatus = validateConfirmPw(matKhauMoiTextField.getText(), newValue);
                if (!changePwStatus) {
                    changePwMsgLabel.setTextFill(Color.RED);
                } else {
                    // Hợp lệ
                    changePwMsgLabel.setText("Mật khẩu hợp lệ");
                    changePwMsgLabel.setTextFill(Color.GREEN);
                }
                updatePwButton.setDisable(!changePwStatus);
            }
        }));

        initCustomTableCells();

        /* Set up "Tạo mới tài khoản tab" */
        newNgaySinhDatePicker.setValue(LocalDate.of(2000,1,1));
        newChucVuComboBox.setItems(FXCollections.observableArrayList(ChucVu.values()));

        // Set disable cho nút tạo tài khoản khi chưa điền đủ thông tin
        createAccountButton.setDisable(true);

        newTenNvTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newDientThoaiTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newDiaChiTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newNgaySinhDatePicker.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newChucVuComboBox.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newTenDangNhapTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newMatKhauPwField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

        newMatKhau2PwField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            boolean confirmStatus = checkNewEmpInfoFields(
                    newTenNvTextField,
                    newDientThoaiTextField,
                    newDiaChiTextField,
                    newNgaySinhDatePicker,
                    newChucVuComboBox,
                    newTenDangNhapTextField,
                    newMatKhauPwField,
                    newMatKhau2PwField
            );
            createAccountButton.setDisable(!confirmStatus);
        }));

    }

    private boolean checkNewEmpInfoFields(TextField newTenNvTextField, TextField newDientThoaiTextField,
                                          TextField newDiaChiTextField, DatePicker newNgaySinhDatePicker,
                                          ComboBox<ChucVu> newChucVuComboBox, TextField newTenDangNhapTextField,
                                          PasswordField newMatKhauPwField, PasswordField newMatKhau2PwField) {
        int invalidCount = 0;

        if (newTenNvTextField.getText().trim().isEmpty()) {
            invalidCount++;
        }
        if (newDientThoaiTextField.getText().trim().isEmpty()) {
            invalidCount++;
        }
        if (newDiaChiTextField.getText().trim().isEmpty()) {
            invalidCount++;
        }
        if (newNgaySinhDatePicker.toString().trim().isEmpty()) {
            invalidCount++;
        }if (newChucVuComboBox.getSelectionModel().getSelectedItem() == null) {
            invalidCount++;
        }
        if (newTenDangNhapTextField.getText().trim().isEmpty()) {
            invalidCount++;
        }
        if (newMatKhauPwField.getText().trim().isEmpty()) {
            invalidCount++;
        }
        if (newMatKhau2PwField.getText().trim().isEmpty()) {
            invalidCount++;
        }

        return invalidCount == 0;
    }

    private void initCustomTableCells() {
        /* Custom TableView
         * setCellValueFactory: Định nghĩa cách để lấy dữ liệu cho mỗi ô
         * setCellFactory: Định nghĩa compoent và dữ liệu của component sẽ hiển thị khi ô được hiệu chỉnh
         * onCellEditCommit: định nghĩa cách dữ liệu mới sẽ được cập nhập vào Model
         */

        // Mã NV - maNvCol - no edit
        maNvCol.setCellValueFactory(new PropertyValueFactory<>("manv"));
        maNvCol.setEditable(false);

        // Tên NV - tenNvCol - TextField
        tenNvCol.setCellValueFactory(new PropertyValueFactory<>("tennv"));
        tenNvCol.setCellFactory(TextFieldTableCell.forTableColumn());

        tenNvCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Nhanvien, String> event) -> {
                    TablePosition<Nhanvien, String> pos = event.getTablePosition();
                    String newTenNv = event.getNewValue();

                    int row = pos.getRow();
                    Nhanvien nhanvien = event.getTableView().getItems().get(row);
                    nhanvien.setTennv(newTenNv);
                    NhanvienDAO.getInstance().update(nhanvien.getManv(),nhanvien);
                }
        );

        // SĐT - dienThoaiCol - TextField
        dienThoaiCol.setCellValueFactory(new PropertyValueFactory<>("dienthoai"));
        dienThoaiCol.setCellFactory(TextFieldTableCell.forTableColumn());

        dienThoaiCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Nhanvien, String> event) -> {
                    TablePosition<Nhanvien, String> pos = event.getTablePosition();
                    String newDienThoai = event.getNewValue();

                    int row = pos.getRow();
                    Nhanvien nhanvien = event.getTableView().getItems().get(row);
                    nhanvien.setDienthoai(newDienThoai);
                    NhanvienDAO.getInstance().update(nhanvien.getManv(),nhanvien);
                }
        );

        // Địa chỉ - diaChiCol - TextField
        diaChiCol.setCellValueFactory(new PropertyValueFactory<>("diachi"));
        diaChiCol.setCellFactory(TextFieldTableCell.forTableColumn());

        diaChiCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Nhanvien, String> event) -> {
                    TablePosition<Nhanvien, String> pos = event.getTablePosition();
                    String newDiaChi = event.getNewValue();

                    int row = pos.getRow();
                    Nhanvien nhanvien = event.getTableView().getItems().get(row);
                    nhanvien.setDiachi(newDiaChi);
                    NhanvienDAO.getInstance().update(nhanvien.getManv(),nhanvien);
                }
        );

        // Ngày sinh - ngaySinhCol - TextField/DatePicker?
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaysinh"));
//        ngaySinhCol.setCellFactory(TextFieldTableCell.<Nhanvien>forTableColumn());
        ngaySinhCol.setEditable(false); // tạm thời

        // Chức vụ - chucVuCol - ComboBox
//        chucVuCol.setCellValueFactory(new PropertyValueFactory<>("chucvu"));
        ObservableList<ChucVu> chucVuObservableList = FXCollections.observableArrayList(ChucVu.values());
        chucVuCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<ChucVu> call(TableColumn.CellDataFeatures<Nhanvien, ChucVu> nhanvienChucVuCellDataFeatures) {
                Nhanvien nhanvien = nhanvienChucVuCellDataFeatures.getValue();
                String chucVuName = nhanvien.getChucvu();
                ChucVu chucVu = ChucVu.fromName(chucVuName); // tao chuc vu tu thong tin lay duoc
                return new SimpleObjectProperty<>(chucVu);
            }
        });
//        chucVuCol.setCellFactory(ComboBoxTableCell.forTableColumn());
        chucVuCol.setCellFactory(ComboBoxTableCell.forTableColumn(chucVuObservableList));
        chucVuCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Nhanvien, ChucVu> event) -> {
                    TablePosition<Nhanvien, ChucVu> pos = event.getTablePosition();
                    ChucVu chucVu = event.getNewValue();

                    int row = pos.getRow();
                    Nhanvien nhanvien = event.getTableView().getItems().get(row);

                    nhanvien.setChucvu(chucVu.getName());
                    NhanvienDAO.getInstance().update(nhanvien.getManv(),nhanvien);
                }
        );


        // Tạm khóa - trangThaiCol - CheckBox
//        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        trangThaiCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Nhanvien, Boolean> nhanvienBooleanCellDataFeatures) {
                Nhanvien nhanvien = nhanvienBooleanCellDataFeatures.getValue();
                // trangthai == true: is Active, trangthai == false: is Blocked
                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(nhanvien.getTrangthai());

                //! trangThaiCol.setOnEditCommit(): Không làm việc với CheckBoxTableCell

                // Khi cột Tạm khóa (Trạng thái) thay đổi
                booleanProperty.addListener((observableValue, oldValue, newValue) -> {
                    // newValue == true: is active, newValue == false: is NOT active
                    nhanvien.setTrangthai(newValue);
                    NhanvienDAO.getInstance().update(nhanvien.getManv(), nhanvien);
                });

                return booleanProperty;
            }
        });

        trangThaiCol.setCellFactory(nhanvienBooleanTableColumn -> {
            CheckBoxTableCell<Nhanvien, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
    }

    @FXML
    private void createAccount(ActionEvent actionEvent) {
        String tenNv = newTenNvTextField.getText();
        // cast java.util.Date -> java.sql.Date
        java.util.Date date = java.util.Date.from(newNgaySinhDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date ngaySinh = new Date(date.getTime());
        String dienThoai = newDientThoaiTextField.getText();
        String diacChi = newDiaChiTextField.getText();
        String tenDangNhap = newTenDangNhapTextField.getText();
        String matKhau = newMatKhauPwField.getText();
        String chucVu = newChucVuComboBox.getSelectionModel().getSelectedItem().getName();

        if (validateNewNhanvienInfo(tenNv, ngaySinh, dienThoai, diacChi, tenDangNhap, matKhau, chucVu)) {
            // Thêm dialog xác nhận về việc thêm các thông tin trên
            boolean confirm = Utilities.getInstance().showAlert(Alert.AlertType.CONFIRMATION,
                    quanLyNhanVienGridPane.getScene().getWindow(),
                    "Xác nhận thêm nhân viên", "Bạn chắc chắn muốn thêm nhân viên với những thông tin trên?\n");

            if (!confirm) { return; }

            // Hash password
            String hashedPassword = BCrypt.hashpw(matKhau, BCrypt.gensalt(10));

            // Create a new employee
            Nhanvien newNhanvien = new Nhanvien(
                    NhanvienDAO.getInstance().getMaxId() + 1,
                    newTenNvTextField.getText(),
                    ngaySinh,
                    newDientThoaiTextField.getText(),
                    newDiaChiTextField.getText(),
                    newTenDangNhapTextField.getText(),
                    hashedPassword,
                    newChucVuComboBox.getSelectionModel().getSelectedItem().getName(),
                    true // default is "active"
            );

            if (NhanvienDAO.getInstance().insert(newNhanvien)) {
                // Successfully
//                newAccountInfoStatusLabel.setText("Tạo thành công");
//                newAccountInfoStatusLabel.setTextFill(Color.GREEN);
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        this.getRoot().getScene().getWindow(), "Thành công",
                        "Nhân viên " + newNhanvien.getTennv() +
                                " (Mã nhân viên: " + newNhanvien.getManv() + ") đã được thêm vào danh sách!");

                // Cập nhật view lên table
                nhanvienObservableList.add(newNhanvien);
            } else {
//                newAccountInfoStatusLabel.setText("Tạo thất bại");
//                newAccountInfoStatusLabel.setTextFill(Color.RED);
                Utilities.getInstance().showAlert(Alert.AlertType.ERROR,
                        this.getRoot().getScene().getWindow(), "Thất bại",
                        "Không thể thêm nhân viên này! Xin hãy kiểm tra lại!");
            }
        } else {
            System.out.println("Thông tin nhập vào chưa hợp lệ để tạo tài khoản!");
        }
    }

    private boolean validateNewNhanvienInfo(String tenNv, Date ngaySinh, String dienThoai, String diaChi,
                                         String tenDangNhap, String matKhau, String chucVu) {
        for (Nhanvien nv : nhanvienObservableList) {
            if (nv.getTendangnhap().equals(tenDangNhap)) {
                Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Thông tin không hợp lệ", "Tên đăng nhập đã tồn tại");
                return false;
            }
        }
        if (dienThoai.trim().length() < 10) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Thông tin không hợp lệ", "Số điện thoại không hợp lệ");
            return false;
        }
        if (!tenDangNhap.trim().matches("^[A-z_](\\w|\\.|_){5,32}$")) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Thông tin không hợp lệ", "Tên đăng nhập phải có ít nhất 6 ký tự" +
                    "\nKhông bắt đầu bằng số" +
                    "\nĐược phép chứa ký tự \"_\"" +
                    "\nKhông chứa các ký tự đặc biệt khác như !,@,#,$,%,^,&,*,(,),-,+, ...");
            return false;
        }
        if (matKhau.trim().length() < 6) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Thông tin không hợp lệ", "Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }


    private boolean validateConfirmPw(String newPw, String confirmNewPw) {
        if (newPw.trim().isEmpty() || confirmNewPw.trim().isEmpty()) {
            changePwMsgLabel.setText("Chưa điền đủ các thông tin");
            return false;
        }
        if (newPw.trim().length() < 6) {
            changePwMsgLabel.setText("Mật khẩu phải chứa ít nhất 6 ký tự");
            return false;
        }
        // Xác nhận lại mật khẩu phải trùng
        if (!newPw.equals(confirmNewPw)) {
            changePwMsgLabel.setText("Mật khẩu xác nhận không giống");
            return false;
        }
        return true;
    }

    @FXML
    private void updatePassword (ActionEvent actionEvent) {
        if (!tenDangNhapTextField.getText().isEmpty()) {
            Nhanvien selectedNhanvien = nhanvienTableView.getSelectionModel().getSelectedItem();

            boolean confirm = Utilities.getInstance().showAlert(Alert.AlertType.CONFIRMATION,
                    quanLyNhanVienGridPane.getScene().getWindow(), "Xác nhận", "Xác nhận đổi mật khẩu?");
            if (!confirm) {
                return;
            }

            String hashedPassword = BCrypt.hashpw(matKhauMoi2TextField.getText(), BCrypt.gensalt(10));
            selectedNhanvien.setMatkhau(hashedPassword);

            boolean updated = NhanvienDAO.getInstance().update(selectedNhanvien.getManv(), selectedNhanvien);

            if (updated) {
                System.out.println("Cập nhật mật khẩu thành công");
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        quanLyNhanVienGridPane.getScene().getWindow(), "Thông báo", "Đổi mật khẩu thành công!");
            } else {
                System.out.println("Cập nhật mật khẩu thất bại");
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        quanLyNhanVienGridPane.getScene().getWindow(), "Thông báo", "Đổi mật khẩu thất bại!");
            }
        } else {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(),
                    "Lỗi", "Tên đăng nhập không được bỏ trống!");
        }
    }

    @FXML
    private void deleteEmp(ActionEvent actionEvent) {
        Nhanvien selectedItem = nhanvienTableView.getSelectionModel().getSelectedItem();

        if (selectedItem!=null) {
            boolean deleted = NhanvienDAO.getInstance().delete(selectedItem);

            if (deleted) {
                // Xóa thành công
                System.out.println("Xóa thành công nhân viên: " + selectedItem);
                nhanvienObservableList.remove(selectedItem);
            } else {
                System.out.println("Xóa thất nhân viên: " + selectedItem);
            }
        }
    }

    @FXML
    private void searchEmp(ActionEvent actionEvent) {
        String searchStr = searchEmpTextField.getText();
        String pattern = "%" + searchStr + "%";

        ObservableList<Nhanvien> matched = FXCollections.observableList(NhanvienDAO.getInstance().findAnyLike(pattern));
        nhanvienTableView.setItems(matched);
    }

    @FXML
    private void refresh(ActionEvent actionEvent) {
        searchEmpTextField.clear();

        nhanvienTableView.getSelectionModel().clearSelection();
        // Hiển thị các dòng dữ liệu
        nhanvienTableView.setItems(nhanvienObservableList);

        tenDangNhapTextField.clear();
        matKhauMoiTextField.clear();
        matKhauMoi2TextField.clear();

        newTenNvTextField.clear();
        newDientThoaiTextField.clear();
        newDiaChiTextField.clear();
        //newNgaySinhDatePicker
        newChucVuComboBox.getSelectionModel().clearSelection();
        newTenDangNhapTextField.clear();
        newMatKhauPwField.clear();
        newMatKhau2PwField.clear();
    }

    @FXML
    private void logout(ActionEvent actionEvent) {
        Utilities.getInstance().logout(actionEvent);
    }
}
