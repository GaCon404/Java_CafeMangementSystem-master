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

    // T???o m???i t??i kho???n
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
        // Hi???n th??? c??c d??ng d??? li???u
        nhanvienObservableList = FXCollections.observableList(NhanvienDAO.getInstance().getAll());
        System.out.println("Danh s??ch nh??n vi??n:");
        for (Nhanvien nv : nhanvienObservableList) {
            System.out.println(nv);
        }
        nhanvienTableView.setItems(nhanvienObservableList);

        /* Truy???n d??? li???u v??o "?????i m???t kh???u" tab */
        ObservableList<Nhanvien> selectedItems = nhanvienTableView.getSelectionModel().getSelectedItems();
        selectedItems.addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends Nhanvien> change) {
                if (change.getList().size() > 0) {
                    System.out.println("Selection changed: " + change.getList());
                    Nhanvien selectedInfo = change.getList().get(0);
                    setNhanvienInfoFromSelectedRow(selectedInfo); // hi???n th??? th??ng tin sang b??n ph???i
                }
            }

            private void setNhanvienInfoFromSelectedRow(Nhanvien selectedInfo) {
                tenDangNhapTextField.setText(selectedInfo.getTendangnhap());
                changePwMsgLabel.setText("M???i ??i???n m???t kh???u m???i");
                changePwMsgLabel.setTextFill(Color.BLACK);
            }
        });

        /* Set up "?????i m???t kh???u" tab */
        updatePwButton.setDisable(true);
        matKhauMoiTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (tenDangNhapTextField.getText().isEmpty()) {
                changePwMsgLabel.setText("C???n ch???n m???t nh??n vi??n ????? thay ?????i m???t kh???u");
                changePwMsgLabel.setTextFill(Color.DARKORANGE);
            } else {
                boolean changePwStatus = validateConfirmPw(matKhauMoi2TextField.getText(), newValue);
                if (!changePwStatus) {
                    changePwMsgLabel.setTextFill(Color.RED);
                } else {
                    // H???p l???
                    changePwMsgLabel.setText("M???t kh???u h???p l???");
                    changePwMsgLabel.setTextFill(Color.GREEN);
                }
                updatePwButton.setDisable(!changePwStatus);
            }
        }));
        matKhauMoi2TextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (tenDangNhapTextField.getText().isEmpty()) {
                changePwMsgLabel.setText("C???n ch???n m???t nh??n vi??n ????? thay ?????i m???t kh???u");
                changePwMsgLabel.setTextFill(Color.DARKORANGE);
            } else {
                boolean changePwStatus = validateConfirmPw(matKhauMoiTextField.getText(), newValue);
                if (!changePwStatus) {
                    changePwMsgLabel.setTextFill(Color.RED);
                } else {
                    // H???p l???
                    changePwMsgLabel.setText("M???t kh???u h???p l???");
                    changePwMsgLabel.setTextFill(Color.GREEN);
                }
                updatePwButton.setDisable(!changePwStatus);
            }
        }));

        initCustomTableCells();

        /* Set up "T???o m???i t??i kho???n tab" */
        newNgaySinhDatePicker.setValue(LocalDate.of(2000,1,1));
        newChucVuComboBox.setItems(FXCollections.observableArrayList(ChucVu.values()));

        // Set disable cho n??t t???o t??i kho???n khi ch??a ??i???n ????? th??ng tin
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
         * setCellValueFactory: ?????nh ngh??a c??ch ????? l???y d??? li???u cho m???i ??
         * setCellFactory: ?????nh ngh??a compoent v?? d??? li???u c???a component s??? hi???n th??? khi ?? ???????c hi???u ch???nh
         * onCellEditCommit: ?????nh ngh??a c??ch d??? li???u m???i s??? ???????c c???p nh???p v??o Model
         */

        // M?? NV - maNvCol - no edit
        maNvCol.setCellValueFactory(new PropertyValueFactory<>("manv"));
        maNvCol.setEditable(false);

        // T??n NV - tenNvCol - TextField
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

        // S??T - dienThoaiCol - TextField
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

        // ?????a ch??? - diaChiCol - TextField
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

        // Ng??y sinh - ngaySinhCol - TextField/DatePicker?
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaysinh"));
//        ngaySinhCol.setCellFactory(TextFieldTableCell.<Nhanvien>forTableColumn());
        ngaySinhCol.setEditable(false); // t???m th???i

        // Ch???c v??? - chucVuCol - ComboBox
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


        // T???m kh??a - trangThaiCol - CheckBox
//        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        trangThaiCol.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Nhanvien, Boolean> nhanvienBooleanCellDataFeatures) {
                Nhanvien nhanvien = nhanvienBooleanCellDataFeatures.getValue();
                // trangthai == true: is Active, trangthai == false: is Blocked
                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(nhanvien.getTrangthai());

                //! trangThaiCol.setOnEditCommit(): Kh??ng l??m vi???c v???i CheckBoxTableCell

                // Khi c???t T???m kh??a (Tr???ng th??i) thay ?????i
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
            // Th??m dialog x??c nh???n v??? vi???c th??m c??c th??ng tin tr??n
            boolean confirm = Utilities.getInstance().showAlert(Alert.AlertType.CONFIRMATION,
                    quanLyNhanVienGridPane.getScene().getWindow(),
                    "X??c nh???n th??m nh??n vi??n", "B???n ch???c ch???n mu???n th??m nh??n vi??n v???i nh???ng th??ng tin tr??n?\n");

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
//                newAccountInfoStatusLabel.setText("T???o th??nh c??ng");
//                newAccountInfoStatusLabel.setTextFill(Color.GREEN);
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        this.getRoot().getScene().getWindow(), "Th??nh c??ng",
                        "Nh??n vi??n " + newNhanvien.getTennv() +
                                " (M?? nh??n vi??n: " + newNhanvien.getManv() + ") ???? ???????c th??m v??o danh s??ch!");

                // C???p nh???t view l??n table
                nhanvienObservableList.add(newNhanvien);
            } else {
//                newAccountInfoStatusLabel.setText("T???o th???t b???i");
//                newAccountInfoStatusLabel.setTextFill(Color.RED);
                Utilities.getInstance().showAlert(Alert.AlertType.ERROR,
                        this.getRoot().getScene().getWindow(), "Th???t b???i",
                        "Kh??ng th??? th??m nh??n vi??n n??y! Xin h??y ki???m tra l???i!");
            }
        } else {
            System.out.println("Th??ng tin nh???p v??o ch??a h???p l??? ????? t???o t??i kho???n!");
        }
    }

    private boolean validateNewNhanvienInfo(String tenNv, Date ngaySinh, String dienThoai, String diaChi,
                                         String tenDangNhap, String matKhau, String chucVu) {
        for (Nhanvien nv : nhanvienObservableList) {
            if (nv.getTendangnhap().equals(tenDangNhap)) {
                Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Th??ng tin kh??ng h???p l???", "T??n ????ng nh???p ???? t???n t???i");
                return false;
            }
        }
        if (dienThoai.trim().length() < 10) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Th??ng tin kh??ng h???p l???", "S??? ??i???n tho???i kh??ng h???p l???");
            return false;
        }
        if (!tenDangNhap.trim().matches("^[A-z_](\\w|\\.|_){5,32}$")) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Th??ng tin kh??ng h???p l???", "T??n ????ng nh???p ph???i c?? ??t nh???t 6 k?? t???" +
                    "\nKh??ng b???t ?????u b???ng s???" +
                    "\n???????c ph??p ch???a k?? t??? \"_\"" +
                    "\nKh??ng ch???a c??c k?? t??? ?????c bi???t kh??c nh?? !,@,#,$,%,^,&,*,(,),-,+, ...");
            return false;
        }
        if (matKhau.trim().length() < 6) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(), "Th??ng tin kh??ng h???p l???", "M???t kh???u ph???i c?? ??t nh???t 6 k?? t???");
            return false;
        }
        return true;
    }


    private boolean validateConfirmPw(String newPw, String confirmNewPw) {
        if (newPw.trim().isEmpty() || confirmNewPw.trim().isEmpty()) {
            changePwMsgLabel.setText("Ch??a ??i???n ????? c??c th??ng tin");
            return false;
        }
        if (newPw.trim().length() < 6) {
            changePwMsgLabel.setText("M???t kh???u ph???i ch???a ??t nh???t 6 k?? t???");
            return false;
        }
        // X??c nh???n l???i m???t kh???u ph???i tr??ng
        if (!newPw.equals(confirmNewPw)) {
            changePwMsgLabel.setText("M???t kh???u x??c nh???n kh??ng gi???ng");
            return false;
        }
        return true;
    }

    @FXML
    private void updatePassword (ActionEvent actionEvent) {
        if (!tenDangNhapTextField.getText().isEmpty()) {
            Nhanvien selectedNhanvien = nhanvienTableView.getSelectionModel().getSelectedItem();

            boolean confirm = Utilities.getInstance().showAlert(Alert.AlertType.CONFIRMATION,
                    quanLyNhanVienGridPane.getScene().getWindow(), "X??c nh???n", "X??c nh???n ?????i m???t kh???u?");
            if (!confirm) {
                return;
            }

            String hashedPassword = BCrypt.hashpw(matKhauMoi2TextField.getText(), BCrypt.gensalt(10));
            selectedNhanvien.setMatkhau(hashedPassword);

            boolean updated = NhanvienDAO.getInstance().update(selectedNhanvien.getManv(), selectedNhanvien);

            if (updated) {
                System.out.println("C???p nh???t m???t kh???u th??nh c??ng");
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        quanLyNhanVienGridPane.getScene().getWindow(), "Th??ng b??o", "?????i m???t kh???u th??nh c??ng!");
            } else {
                System.out.println("C???p nh???t m???t kh???u th???t b???i");
                Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION,
                        quanLyNhanVienGridPane.getScene().getWindow(), "Th??ng b??o", "?????i m???t kh???u th???t b???i!");
            }
        } else {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, this.getRoot().getScene().getWindow(),
                    "L???i", "T??n ????ng nh???p kh??ng ???????c b??? tr???ng!");
        }
    }

    @FXML
    private void deleteEmp(ActionEvent actionEvent) {
        Nhanvien selectedItem = nhanvienTableView.getSelectionModel().getSelectedItem();

        if (selectedItem!=null) {
            boolean deleted = NhanvienDAO.getInstance().delete(selectedItem);

            if (deleted) {
                // X??a th??nh c??ng
                System.out.println("X??a th??nh c??ng nh??n vi??n: " + selectedItem);
                nhanvienObservableList.remove(selectedItem);
            } else {
                System.out.println("X??a th???t nh??n vi??n: " + selectedItem);
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
        // Hi???n th??? c??c d??ng d??? li???u
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
