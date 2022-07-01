package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.HoadonDAO;
import CafeMangementSystem.DAOs.NhanvienDAO;
import CafeMangementSystem.Entities.Hoadon;
import CafeMangementSystem.Utils.SessionUser;
import CafeMangementSystem.Utils.Utilities;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;


public class ThongKeControllers implements Initializable {
    @FXML
    private GridPane root;

    @FXML
    private DatePicker fromDayDatePicker;

    @FXML
    private DatePicker toDayDatePicker;

    @FXML
    private Button reloadButton;

    @FXML
    private TableView<Hoadon> hoadonTableView;

    ObservableList<Hoadon> hoadonObservableList;

    @FXML
    private TableColumn<Hoadon,Integer> maHoaDonCol;

    @FXML
    private TableColumn<Hoadon,Integer> giamGiaCol;

    @FXML
    private TableColumn<Hoadon,Float> thanhTienCol;

    @FXML
    private TableColumn<Hoadon, Float> tienTraCol;

    @FXML
    private TableColumn<Hoadon, Float> tienThoiCol;

    @FXML
    private TableColumn<Hoadon, String> ngayGiaoDichCol;

    @FXML
    private TableColumn<Hoadon, String> nhanVienTaoCol;

    @FXML
    private TextField totalRevenueTextField;

    @FXML
    private Label tenChuQuanLabel;

    public ThongKeControllers() {
        this.load();
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ThongKe.fxml"));
        try {
            if (loader.getController() == null) {
                loader.setController(this);
            }
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getRoot() {
        return this.root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        tenChuQuanLabel.setText(SessionUser.getInstance().getNhanvien().getTennv());
        initTable();
    }

    private void initTable() {
        hoadonTableView.setEditable(false);
        //setCellValueFactory: Định nghĩa cách để lấy dữ liệu cho mỗi ô
        maHoaDonCol.setCellValueFactory(new PropertyValueFactory<>("mahoadon"));
        giamGiaCol.setCellValueFactory(new PropertyValueFactory<>("mucgiamgia"));
        thanhTienCol.setCellValueFactory(new PropertyValueFactory<>("thanhtien"));
        tienTraCol.setCellValueFactory(new PropertyValueFactory<>("tientra"));
        tienThoiCol.setCellValueFactory(new PropertyValueFactory<>("tienthoi"));
        ngayGiaoDichCol.setCellValueFactory(new PropertyValueFactory<>("ngaygiaodich"));
        // gán một giá trị nào đó mà không cần thiết phải là một thuộc tính của entity đang đưa lên tableview
        nhanVienTaoCol.setCellValueFactory((prop -> {
            StringProperty stringProperty = new SimpleStringProperty();
            Hoadon hoadon = prop.getValue();
            stringProperty.setValue(NhanvienDAO.getInstance().get(hoadon.getNvlaphoadon()).getTennv());
            return stringProperty;
        }));

        // lấy dữ liệu các hóa đơn
        hoadonObservableList = FXCollections.observableList(HoadonDAO.getInstance().getAll());
        BigDecimal totalRevenue = new BigDecimal(0);
        System.out.println("Danh sách hóa đơn:");
        for (Hoadon hoadon : hoadonObservableList) {
            System.out.println(hoadon);
            totalRevenue = totalRevenue.add(hoadon.getThanhtien());
        }
        hoadonTableView.setItems(hoadonObservableList);
        totalRevenueTextField.setText(String.valueOf(totalRevenue));
        totalRevenueTextField.setEditable(false);
    }

    public void reload(ActionEvent actionEvent) {
        if (fromDayDatePicker.getValue() == null || fromDayDatePicker.getValue().toString().isEmpty()) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, root.getScene().getWindow(), "Xảy ra lỗi", "Bạn chưa chọn ngày bắt đầu");
        } else if (toDayDatePicker.getValue() == null || toDayDatePicker.getValue().toString().isEmpty()) {
            Utilities.getInstance().showAlert(Alert.AlertType.ERROR, root.getScene().getWindow(), "Xảy ra lỗi", "Bạn chưa chọn ngày kết thúc");
            return;
        }

        LocalDateTime fromDate = LocalDateTime.from(fromDayDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()));
        LocalDateTime toDate = LocalDateTime.from(toDayDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()));

        ObservableList<Hoadon> contextHoaDonList = FXCollections.observableList(HoadonDAO.getInstance().getAll(fromDate, toDate));
        BigDecimal totalRevenue = new BigDecimal(0);
        System.out.println("Danh sách hóa đơn từ ngày " + fromDate + " đến ngày " + toDate + ":");

        for (Hoadon hoadon : contextHoaDonList) {
            totalRevenue = totalRevenue.add(hoadon.getThanhtien());
        }

        totalRevenueTextField.setText(totalRevenue.toString());
        hoadonTableView.setItems(contextHoaDonList);
    }

    @FXML
    private void logout(ActionEvent actionEvent) {
        Utilities.getInstance().logout(actionEvent);
    }
}
