package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.ChitietHoadonDAO;
import CafeMangementSystem.DAOs.HoadonDAO;
import CafeMangementSystem.DAOs.MonmenuDAO;
import CafeMangementSystem.Entities.ChitietHoadon;
import CafeMangementSystem.Entities.Hoadon;
import CafeMangementSystem.Entities.Monmenu;
import CafeMangementSystem.Entities.Nhanvien;
import CafeMangementSystem.Utils.MonOnBill;
import CafeMangementSystem.Utils.SessionUser;
import CafeMangementSystem.Utils.Utilities;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    @FXML
    private GridPane orderRoot;

    @FXML
    private Label tenNhanvienLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label maHoadonLabel;

    @FXML
    private Label todayLabel;

    // main components
    @FXML
    private Button searchMon;

    @FXML
    private ScrollPane menuContainerScrollPane;

    @FXML
    private TilePane menuTilePane;

    ObservableList<Monmenu> monmenuObservableList;

    @FXML
    private TableView<MonOnBill> billTableView;

    @FXML
    private TableColumn<MonOnBill, Integer> mamonCol;

    @FXML
    private TableColumn<MonOnBill, String> tenmonCol;

    @FXML
    private TableColumn<MonOnBill, Integer> soluongCol;

    @FXML
    private TableColumn<MonOnBill, BigDecimal> giabanCol;

    @FXML
    private TableColumn<MonOnBill, Void> removeButtonCol;

    private final ObservableList<MonOnBill> selectedMon;

    // end main components

    // Ph???n hi???n gi?? ti???n
    @FXML
    private TextField tempPriceTextField;

    private BigDecimal tempPrice = new BigDecimal(0);

    @FXML
    private Spinner<Integer> discountSpinner;

    @FXML
    private TextField totalPriceTextField;

    private BigDecimal totalPrice = new BigDecimal(0);

    @FXML
    private TextField receivedAmountTextField;

    @FXML
    private TextField changeAmountTextField;

    @FXML
    private Button refreshButton;

    @FXML
    private Button createBillButton;


    public OrderController() {
        selectedMon = FXCollections.observableList(new ArrayList<>());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        todayLabel.setText(LocalDate.now(ZoneId.systemDefault()).toString());

        maHoadonLabel.setText("M?? h??a ????n: " + (HoadonDAO.getInstance().getMaxId() + 1));

        Nhanvien nhanvien = SessionUser.getInstance().getNhanvien(); // get session user
        tenNhanvienLabel.setText("M?? " + nhanvien.getManv() + " - " + nhanvien.getTennv());

        monmenuObservableList = FXCollections.observableList(MonmenuDAO.getInstance().getAll());

        initMenu(monmenuObservableList);
        initHoaDonTable();
        initCountFields();
    }

    public boolean checkIfContains(ObservableList<MonOnBill> monOnBillObservableList, int mamon) {
        for (MonOnBill mon : monOnBillObservableList) {
            if (mon.getMamon() == mamon) {
                return true;
            }
        }

        return false;
    }

    /**
     * T???o menu ch???n m??n
     * @param observableList danh s??ch c??c m??n ???????c s??? ???????c hi???n th???
     */
    private void initMenu(ObservableList<Monmenu> observableList) {
        menuTilePane.getChildren().clear(); // x??a c??c m??n trong menu c?? ????? chu???n b??? t???o menu

        System.out.println("Danh s??ch t???t c??? c??c m??n s??? ???????c hi???n th???:\n");

        for (int i = 0; i < observableList.size(); i++) {
            Monmenu monmenu = observableList.get(i);

            // t????ng ???ng v???i m???t m??n, ta t???o ra m???t ?????i t?????ng m??n khi ????a v??o bill
            MonOnBill monOnBill = new MonOnBill(monmenu.getMamon(), monmenu.getTenmon(),monmenu.getGiaban(),1);

            // T???o ra t???ng tile cho t???ng m??n t????ng ???ng
            MonTileController monTileController = new MonTileController(monmenu);

            menuTilePane.getChildren().add(monTileController.getMonTileGridPane()); // th??m m??n v??o tile pane
            menuTilePane.getChildren().get(i).setOnMouseClicked(mouseEvent -> {
                // C???p nh???t c??c m??n ???? ch???n l??n bill table
                if (!checkIfContains(selectedMon, monOnBill.getMamon())) {
                    // N???u ch??a n???m trong danh s??ch ch???n th?? ????a th???ng l??n (SL: 1)
                    selectedMon.add(monOnBill);
                    billTableView.setItems(selectedMon);
                } else {
                    // N???u ???? t???n t???i trong danh s??ch ch???n th?? t??ng s??? l?????ng
                    MonOnBill monTangSL = getMonOnBillByMamon(selectedMon, monOnBill.getMamon());
                    if (monTangSL != null) {
                        monTangSL.setSoluong(monTangSL.getSoluong() + 1);
                        selectedMon.set(selectedMon.indexOf(monTangSL), monTangSL);
                    }
                }
            });
        }
    }

    public MonOnBill getMonOnBillByMamon(ObservableList<MonOnBill> observableList, int mamon) {
        for (MonOnBill mon : observableList) {
            if (mon.getMamon() == mamon) {
                return mon;
            }
        }
        return null;
    }

    private void initHoaDonTable() {
        selectedMon.addListener((ListChangeListener<MonOnBill>) change -> {
            // C???p nh???t l???i gi?? t???m t??nh
            tempPrice = tempPrice.subtract(tempPrice); // 0
            for (MonOnBill monOnBill : selectedMon) {
                tempPrice = tempPrice.add((monOnBill.getGiaban()).multiply(new BigDecimal(monOnBill.getSoluong())));
            }
            tempPriceTextField.setText(tempPrice.toString());

            // C???p nh???t l???i ti???n th???i n???u c?? ti???n nh???n
            if (!changeAmountTextField.getText().trim().isEmpty()) {
                // changeAmount = receivedAmount - totalPrice
                BigDecimal receivedAmountBD = new BigDecimal(receivedAmountTextField.getText());
                changeAmountTextField.setText(receivedAmountBD.subtract(totalPrice).toString());
            }
        });
        mamonCol.setCellValueFactory(new PropertyValueFactory<>("mamon"));
        tenmonCol.setCellValueFactory(new PropertyValueFactory<>("tenmon"));
        giabanCol.setCellValueFactory(new PropertyValueFactory<>("giaban"));
        soluongCol.setCellValueFactory(new PropertyValueFactory<>("soluong"));
        removeButtonCol.setReorderable(false);
        removeButtonCol.setCellFactory(t->{
            TableCell<MonOnBill, Void> cell = new TableCell<>(){
                final Button removeMonButton = new Button("X??a");
                {
                    removeMonButton.setOnAction(t->{
                        MonOnBill monToRemove = billTableView.getItems().get(getIndex());
                        billTableView.getItems().remove(monToRemove);
                        selectedMon.remove(monToRemove);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    setGraphic(empty ? null : removeMonButton);
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
    }

    private void initCountFields() {
        createBillButton.setDisable(true); // V?? hi???u h??a n??t l???p h??a ????n

        // T??y ch???nh spinner cho ph???n khuy???n m??i
        discountSpinner.setEditable(true); // Cho ph??p hi???u ch???nh
        // Danh s??ch c??c ph???n t???
        ObservableList<Integer> items = FXCollections.observableArrayList(
                0, 5, 10, 15, 20,
                25, 30, 35, 40, 45,
                50, 55, 60, 65, 70,
                75, 80, 85, 90, 100
        );

        // Value Factory:
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(items);

        // B??? chuy???n ?????i gi???a text hi???n th??? tr??n Spinner v?? ?????i t?????ng item
        MyConverter converter = new MyConverter();
        valueFactory.setConverter(converter);

        discountSpinner.setValueFactory(valueFactory);
        // xong t??y ch???nh spinner

        // thay ?????i s??? ti???n t???m t??nh -> thay ?????i t???ng ti???n
        tempPriceTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            totalPrice = tempPrice.multiply(new BigDecimal(100 - discountSpinner.getValue())).divide(new BigDecimal(100));
            totalPriceTextField.setText(totalPrice.toString());
        }));

        // thay ?????i khuy???n m??i -> thay ?????i t???ng ti???n
        discountSpinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            // c???p nh???t t???ng c???ng sau khi ??p d???ng khuy???n m??i
            // totalPrice = tempPrice * (100 - discount)/100;
            totalPrice = tempPrice.multiply(new BigDecimal(100 - discountSpinner.getValue())).divide(new BigDecimal(100));
            totalPriceTextField.setText(totalPrice.toString());
        }));

        // thay ?????i ti???n nh???n t??? kh??ch -> thay ?????i kh??? n??ng l???p h??a ????n, thay ?????i ti???n th???i
        receivedAmountTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                BigDecimal change = new BigDecimal(newValue).subtract(totalPrice);
                changeAmountTextField.setText(change.toString());

                // S??? ti???n kh??ch tr??? nh??? h??n s??? ti???n c???n tr??? th?? KH??NG cho ph??p thanh to??n
                createBillButton.setDisable(BigDecimal.valueOf(Float.parseFloat(newValue.trim())).compareTo(totalPrice) < 0);
            } else {
                changeAmountTextField.clear();
            }

            // Nh???n t??? kh??ch th?? kh??ng c??n ???????c ch???nh khuy???n m??i, ch??a nh???n th?? ???????c ch???nh
            discountSpinner.setDisable(!newValue.trim().isEmpty());
        }));



    }

    class MyConverter extends StringConverter<Integer> {

        @Override
        public String toString(Integer object) {
            return object + "";
        }

        @Override
        public Integer fromString(String string) {
            return Integer.parseInt(string);
        }

    }

    public void refresh(ActionEvent actionEvent) {
        searchTextField.clear();
        billTableView.getItems().clear();
        tempPriceTextField.clear();
        discountSpinner.getEditor().clear();
        totalPriceTextField.clear();
        receivedAmountTextField.clear();

        monmenuObservableList = FXCollections.observableList(MonmenuDAO.getInstance().getAll());
        initMenu(monmenuObservableList);

        // c???p nh???t label hi???n m?? h??a ????n
        maHoadonLabel.setText("M?? h??a ????n: " + (HoadonDAO.getInstance().getMaxId() + 1));

        billTableView.setDisable(false);
        menuTilePane.setDisable(false);
    }

    public void search(ActionEvent actionEvent) {
        String searchText = searchTextField.getText();
        monmenuObservableList.clear();

        List<Monmenu> monmenuList = MonmenuDAO.getInstance().getAll(searchText);
        initMenu(FXCollections.observableList(monmenuList));
    }

    public void createBill(ActionEvent actionEvent) {
        // t???o ra h??a ????n
        Hoadon newHoadon = new Hoadon(
                HoadonDAO.getInstance().getMaxId() + 1,
                discountSpinner.getValue(),
                totalPrice,
                BigDecimal.valueOf(Float.parseFloat(receivedAmountTextField.getText().trim())),
                BigDecimal.valueOf(Float.parseFloat(changeAmountTextField.getText().trim())),
                LocalDateTime.now(ZoneId.systemDefault()),
                SessionUser.getInstance().getNhanvien().getManv()
        );

        boolean inserted = HoadonDAO.getInstance().insert(newHoadon);

        if (inserted) {
            // c???u h??nh chi ti???t h??a ????n
            List<MonOnBill> paidMon = billTableView.getItems();

            for (MonOnBill monOnBill : paidMon) {
                // T???o m???t chi ti???t h??a ????n
                ChitietHoadon chitietHoadon = new ChitietHoadon();
                chitietHoadon.setMahoadon(newHoadon.getMahoadon());
                chitietHoadon.setMamon(monOnBill.getMamon());
                chitietHoadon.setSoluong(monOnBill.getSoluong());
                chitietHoadon.setGiaban(monOnBill.getGiaban());
                chitietHoadon.setTongtien(monOnBill.getGiaban().multiply(new BigDecimal(monOnBill.getSoluong()))); // t???ng ti???n = sl * ????n gi??

                ChitietHoadonDAO.getInstance().insert(chitietHoadon);
            }

            createBillButton.setDisable(true);
            billTableView.setDisable(true);
            menuTilePane.setDisable(true);

            Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION, orderRoot.getScene().getWindow(), "Th??nh c??ng", "Ghi nh???n h??a ????n th??nh c??ng!");
        } else {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, orderRoot.getScene().getWindow(), "Th???t b???i", "Ghi nh???n h??a ????n kh??ng th??nh c??ng.");
            System.out.println("C?? l???i trong qu?? tr??nh ghi l??n c?? s??? d??? li???u");
        }
    }

    @FXML
    private void logout(ActionEvent actionEvent) {
        Utilities.getInstance().logout(actionEvent);
    }
}
