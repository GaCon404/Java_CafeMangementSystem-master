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

    // Phần hiện giá tiền
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

        maHoadonLabel.setText("Mã hóa đơn: " + (HoadonDAO.getInstance().getMaxId() + 1));

        Nhanvien nhanvien = SessionUser.getInstance().getNhanvien(); // get session user
        tenNhanvienLabel.setText("Mã " + nhanvien.getManv() + " - " + nhanvien.getTennv());

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
     * Tạo menu chọn món
     * @param observableList danh sách các món được sẽ được hiển thị
     */
    private void initMenu(ObservableList<Monmenu> observableList) {
        menuTilePane.getChildren().clear(); // xóa các món trong menu cũ để chuẩn bị tạo menu

        System.out.println("Danh sách tất cả các món sẽ được hiển thị:\n");

        for (int i = 0; i < observableList.size(); i++) {
            Monmenu monmenu = observableList.get(i);

            // tương ứng với một món, ta tạo ra một đối tượng món khi đưa vào bill
            MonOnBill monOnBill = new MonOnBill(monmenu.getMamon(), monmenu.getTenmon(),monmenu.getGiaban(),1);

            // Tạo ra từng tile cho từng món tương ứng
            MonTileController monTileController = new MonTileController(monmenu);

            menuTilePane.getChildren().add(monTileController.getMonTileGridPane()); // thêm món vào tile pane
            menuTilePane.getChildren().get(i).setOnMouseClicked(mouseEvent -> {
                // Cập nhật các món đã chọn lên bill table
                if (!checkIfContains(selectedMon, monOnBill.getMamon())) {
                    // Nếu chưa nằm trong danh sách chọn thì đưa thẳng lên (SL: 1)
                    selectedMon.add(monOnBill);
                    billTableView.setItems(selectedMon);
                } else {
                    // Nếu đã tồn tại trong danh sách chọn thì tăng số lượng
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
            // Cập nhật lại giá tạm tính
            tempPrice = tempPrice.subtract(tempPrice); // 0
            for (MonOnBill monOnBill : selectedMon) {
                tempPrice = tempPrice.add((monOnBill.getGiaban()).multiply(new BigDecimal(monOnBill.getSoluong())));
            }
            tempPriceTextField.setText(tempPrice.toString());

            // Cập nhật lại tiền thối nếu có tiền nhận
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
                final Button removeMonButton = new Button("Xóa");
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
        createBillButton.setDisable(true); // Vô hiệu hóa nút lập hóa đơn

        // Tùy chỉnh spinner cho phần khuyến mãi
        discountSpinner.setEditable(true); // Cho phép hiệu chỉnh
        // Danh sách các phần tử
        ObservableList<Integer> items = FXCollections.observableArrayList(
                0, 5, 10, 15, 20,
                25, 30, 35, 40, 45,
                50, 55, 60, 65, 70,
                75, 80, 85, 90, 100
        );

        // Value Factory:
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(items);

        // Bộ chuyển đổi giữa text hiển thị trên Spinner và đối tượng item
        MyConverter converter = new MyConverter();
        valueFactory.setConverter(converter);

        discountSpinner.setValueFactory(valueFactory);
        // xong tùy chỉnh spinner

        // thay đổi số tiền tạm tính -> thay đổi tổng tiền
        tempPriceTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            totalPrice = tempPrice.multiply(new BigDecimal(100 - discountSpinner.getValue())).divide(new BigDecimal(100));
            totalPriceTextField.setText(totalPrice.toString());
        }));

        // thay đổi khuyến mãi -> thay đổi tổng tiền
        discountSpinner.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
            // cập nhật tổng cộng sau khi áp dụng khuyến mãi
            // totalPrice = tempPrice * (100 - discount)/100;
            totalPrice = tempPrice.multiply(new BigDecimal(100 - discountSpinner.getValue())).divide(new BigDecimal(100));
            totalPriceTextField.setText(totalPrice.toString());
        }));

        // thay đổi tiền nhận từ khách -> thay đổi khả năng lập hóa đơn, thay đổi tiền thối
        receivedAmountTextField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                BigDecimal change = new BigDecimal(newValue).subtract(totalPrice);
                changeAmountTextField.setText(change.toString());

                // Số tiền khách trả nhỏ hơn số tiền cần trả thì KHÔNG cho phép thanh toán
                createBillButton.setDisable(BigDecimal.valueOf(Float.parseFloat(newValue.trim())).compareTo(totalPrice) < 0);
            } else {
                changeAmountTextField.clear();
            }

            // Nhận từ khách thì không còn được chỉnh khuyến mãi, chưa nhận thì được chỉnh
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

        // cập nhật label hiện mã hóa đơn
        maHoadonLabel.setText("Mã hóa đơn: " + (HoadonDAO.getInstance().getMaxId() + 1));

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
        // tạo ra hóa đơn
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
            // cấu hình chi tiết hóa đơn
            List<MonOnBill> paidMon = billTableView.getItems();

            for (MonOnBill monOnBill : paidMon) {
                // Tạo một chi tiết hóa đơn
                ChitietHoadon chitietHoadon = new ChitietHoadon();
                chitietHoadon.setMahoadon(newHoadon.getMahoadon());
                chitietHoadon.setMamon(monOnBill.getMamon());
                chitietHoadon.setSoluong(monOnBill.getSoluong());
                chitietHoadon.setGiaban(monOnBill.getGiaban());
                chitietHoadon.setTongtien(monOnBill.getGiaban().multiply(new BigDecimal(monOnBill.getSoluong()))); // tổng tiền = sl * đơn giá

                ChitietHoadonDAO.getInstance().insert(chitietHoadon);
            }

            createBillButton.setDisable(true);
            billTableView.setDisable(true);
            menuTilePane.setDisable(true);

            Utilities.getInstance().showAlert(Alert.AlertType.INFORMATION, orderRoot.getScene().getWindow(), "Thành công", "Ghi nhận hóa đơn thành công!");
        } else {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, orderRoot.getScene().getWindow(), "Thất bại", "Ghi nhận hóa đơn không thành công.");
            System.out.println("Có lỗi trong quá trình ghi lên cơ sở dữ liệu");
        }
    }

    @FXML
    private void logout(ActionEvent actionEvent) {
        Utilities.getInstance().logout(actionEvent);
    }
}
