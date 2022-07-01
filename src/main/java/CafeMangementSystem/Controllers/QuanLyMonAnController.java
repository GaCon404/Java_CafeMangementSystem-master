package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.LichsuGiamonDAO;
import CafeMangementSystem.DAOs.MonmenuDAO;
import CafeMangementSystem.Entities.LichsuGiamon;
import CafeMangementSystem.Entities.Monmenu;
import CafeMangementSystem.Utils.BigDecimalTextFormatter;
import CafeMangementSystem.Utils.Globals;
import CafeMangementSystem.Utils.ThreadPool;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class QuanLyMonAnController implements Initializable {
    ObservableList<String> SearchCriteriasList;
    ObservableList<Monmenu> usableMonAn;
    Image defaultImage;
    FXMLLoader loader;
    Monmenu selectedMonmenu;

    @FXML
    private StackPane root;
    @FXML
    private BorderPane QuanLyMonAn_BorderPane;
    @FXML
    private TableView<Monmenu> MonAns_Table;
    @FXML
    private TableColumn<Monmenu, Integer> clmMaMon;
    @FXML
    private TableColumn<Monmenu, String> clmTenMon;
    @FXML
    private TableColumn<Monmenu, BigDecimal> clmGiaBan;
   @FXML
    private TableColumn<Monmenu, Boolean> clmTrangThaiBan;
    @FXML
    private TableColumn<Monmenu, Boolean> clmActions;
    @FXML
    private VBox loadingOverlay;
    @FXML
    private TextField SearchBar;
    @FXML
    private ComboBox<String> SearchKeywordType_ComboBox;
    @FXML
    private Button SearchButton;
    @FXML
    private VBox SelectionPrompt_Overlay;
    @FXML
    private ScrollPane ChiTietMonAn_Pane;
    @FXML
    private Label ChiTietMonAn_TenMon;
    @FXML
    private ImageView ChiTietMonAn_ImageMon;
    @FXML
    private Label ChiTietMonAn_GiaBan;
    @FXML
    private Label ChiTietMonAn_TrangThaiBan;
    @FXML
    private Button DeselectButton;
    @FXML
    private HBox SearchArea;
    @FXML
    private Label selectPromptLabel;
    @FXML
    private TableView<LichsuGiamon> LichSuGia_Table;
    @FXML
    private TableColumn<LichsuGiamon, Timestamp> clmThoiDiem;
    @FXML
    private TableColumn<LichsuGiamon, BigDecimal> clmGiaTaiThoiDiem;
    @FXML
    private TableColumn<Monmenu, Void> clmSeeDetails;

    public QuanLyMonAnController() {
        this.load();
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/QuanLyMonAn.fxml"));
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
        loader = new FXMLLoader();

        MonAns_Table.addEventFilter(ScrollEvent.ANY, Event::consume);
        MonAns_Table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        SearchCriteriasList = FXCollections.observableArrayList("Theo mã món","Theo tên món");

        ChangeListener<String> byMamon = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches("\\d*")) {
                    t1 = t1.replaceAll("[^\\d]", "");
                }
                SearchBar.setText(t1.substring(0, Math.min(t1.length(), 9)));
            }
        };
        ChangeListener<String> byTenmon = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                SearchBar.setText(t1.substring(0, Math.min(t1.length(), 30)));
            }
        };
        SearchKeywordType_ComboBox.setItems(SearchCriteriasList);
        SearchKeywordType_ComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            SearchBar.clear();
            if(oldVal != null){
                switch(oldVal) {
                    case "Theo mã món":
                        SearchBar.textProperty().removeListener(byMamon); break;
                    case "Theo tên món":
                        SearchBar.textProperty().removeListener(byTenmon); break;
                }
            }
            switch(newVal) {
                case "Theo mã món":
                    SearchBar.textProperty().addListener(byMamon); break;
                case "Theo tên món":
                    SearchBar.textProperty().addListener(byTenmon); break;
            }
        });
        SearchKeywordType_ComboBox.getSelectionModel().select(0);

        defaultImage = new Image("/Default_Images/fast-food.png");

        InitMonanTableColumns();
        RefreshData();
    }

    private void InitMonanTableColumns(){
        clmMaMon.setCellValueFactory(new PropertyValueFactory<>("mamon"));
        clmMaMon.setReorderable(false);
        clmMaMon.setCellFactory(p -> {
            TableCell<Monmenu, Integer> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                }

                @Override
                protected void updateItem(Integer integer, boolean b) {
                    super.updateItem(integer, b);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                    text.textProperty().bind(Bindings.createStringBinding(() ->{
                        if(integer == null)
                            return null;
                        else
                            return integer.toString();
                    }, emptyProperty(), itemProperty()));
                    text.wrappingWidthProperty().bind(widthProperty());

                    if(integer == null || b){
                        setGraphic(null);
                    }else{
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        clmTenMon.setCellValueFactory(new PropertyValueFactory<>("tenmon"));
        clmTenMon.setReorderable(false);
        clmTenMon.setCellFactory(p -> {
            TableCell<Monmenu, String> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                    setGraphic(text);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                }

                @Override
                protected void updateItem(String s, boolean b) {
                    super.updateItem(s, b);
                    text.setText(s);
                    text.wrappingWidthProperty().bind(widthProperty());
                }
            };
            return cell;
        });

        clmGiaBan.setCellValueFactory(new PropertyValueFactory<>("giaban"));
        clmGiaBan.setReorderable(false);
        clmGiaBan.setCellFactory(p -> {
            TableCell<Monmenu, BigDecimal> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                }

                @Override
                protected void updateItem(BigDecimal item, boolean b) {
                    super.updateItem(item, b);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                    text.textProperty().bind(Bindings.createStringBinding(() ->{
                        if(item == null)
                            return null;
                        else
                            return Globals.numberFormat.format(item);
                    }, emptyProperty(), itemProperty()));
                    text.wrappingWidthProperty().bind(widthProperty());

                    if(item == null || b){
                        setGraphic(null);
                    }else{
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        clmTrangThaiBan.setCellValueFactory(p ->{
            Monmenu m = p.getValue();
            return m.TrangThaiBanProperty();
        });
        clmTrangThaiBan.setReorderable(false);
        clmTrangThaiBan.setCellFactory(p -> {
            TableCell<Monmenu, Boolean> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                }

                @Override
                protected void updateItem(Boolean item, boolean b) {
                    super.updateItem(item, b);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                    setAlignment(Pos.CENTER);

                    text.wrappingWidthProperty().bind(widthProperty());
                    text.textProperty().bind(Bindings.createStringBinding(() ->{
                        if (getItem() == null) {
                            return null;
                        } else {
                            Boolean boo = getItem();
                            String output = boo == null ? "?????" : boo ? "Đang bán" : "Đã ngừng";
                            return output;
                        }
                    }, emptyProperty(), itemProperty()));

                    if(item == null || b){
                        setGraphic(null);
                    }else{
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        clmActions.setCellValueFactory(p -> {
            Monmenu m = p.getValue();
            return m.TrangThaiBanProperty();
        });
        clmActions.setReorderable(false);
        clmActions.setCellFactory(p -> {
            TableCell<Monmenu, Boolean> cell = new TableCell<>(){
                Button editButton = new Button("Chỉnh sửa");
                Button ngungBanButton = new Button("???????");
                {
                    editButton.setOnAction((t) -> {
                        toggleLoadingMode(true);
                        Stage s = (Stage)((Node) t.getSource()).getScene().getWindow();
                        Scene scene;
                        try{
                            loader.setLocation(getClass().getClassLoader().getResource("FXML\\Them_ChinhSua_MonAn.fxml"));
                            loader.setRoot(null);
                            loader.setController(null);
                            Parent temp = loader.load();
                            scene = new Scene(temp, 600, 400);
                            s.setResizable(false);
                        }catch(IOException e){
                            e.printStackTrace(System.err);
                            toggleLoadingMode(false);
                            return;
                        }
                        ThemChinhSuaMonAnController controller = loader.getController();
                        controller.setMonmenu(getTableView().getItems().get(getIndex()));
                        toggleLoadingMode(false);
                        s.setScene(scene);
                    });
                    ngungBanButton.setOnAction((t)->{
                        toggleLoadingMode(true);
                        Monmenu m = getTableView().getItems().get(getIndex());
                        Task<Void> ngungBan = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                MonmenuDAO.getInstance().checkCurrentTrangThai_ThenUpdateTrangThai(m);
                                return null;
                            }
                        };
                        ngungBan.setOnSucceeded(t1->{
                            m.TrangThaiBanProperty().set(!m.TrangThaiBanProperty().get());
                            toggleLoadingMode(false);
                        });
                        ngungBan.setOnFailed(t1->{
                            Exception ex = (Exception) ngungBan.getException();
                            if(ex instanceof MonmenuDAO.MonmenuDAOException){
                                MonmenuDAO.MonmenuDAOException exc = (MonmenuDAO.MonmenuDAOException) ex;
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText(exc.getMessage());
                                alert.setContentText(exc.getDetails());
                                alert.show();
                                toggleLoadingMode(false);
                            }
                        });
                        ThreadPool.getInstance().submitTask(ngungBan);
                    });
                }

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    HBox temp = new HBox(editButton, ngungBanButton);
                    temp.spacingProperty().set(10);
                    temp.alignmentProperty().set(Pos.CENTER);

                    setGraphic(empty ? null : temp);
                    setAlignment(Pos.CENTER);

                    ngungBanButton.textProperty().bind(Bindings.createStringBinding(() -> {
                        Boolean b = getItem();
                        String output = b == null ? "?????" : b ? "Ngừng bán" : "Bán lại";
                        return output;
                    }, emptyProperty(), itemProperty()));
                }
            };
            return cell;
        });

        clmThoiDiem.setCellValueFactory(new PropertyValueFactory<>("thoidiemcapnhat"));
        clmThoiDiem.setReorderable(false);
        clmThoiDiem.setCellFactory(t->{
            TableCell<LichsuGiamon, Timestamp> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                }

                @Override
                protected void updateItem(Timestamp item, boolean b) {
                    super.updateItem(item, b);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                    setAlignment(Pos.CENTER);

                    text.wrappingWidthProperty().bind(widthProperty());
                    text.textProperty().bind(Bindings.createStringBinding(() ->{
                        if (getItem() == null) {
                            return null;
                        } else {
                            Timestamp time = getItem();
                            return time.toString();
                        }
                    }, emptyProperty(), itemProperty()));

                    if(item == null || b){
                        setGraphic(null);
                    }else{
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        clmGiaTaiThoiDiem.setCellValueFactory(new PropertyValueFactory<>("giacapnhat"));
        clmGiaTaiThoiDiem.setReorderable(false);
        clmGiaTaiThoiDiem.setCellFactory(t->{
            TableCell<LichsuGiamon, BigDecimal> cell = new TableCell<>(){
                Text text = new Text();
                {
                    text.setTextAlignment(TextAlignment.CENTER);
                }

                @Override
                protected void updateItem(BigDecimal item, boolean b) {
                    super.updateItem(item, b);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                    setAlignment(Pos.CENTER);

                    text.wrappingWidthProperty().bind(widthProperty());
                    text.textProperty().bind(Bindings.createStringBinding(() ->{
                        if (getItem() == null) {
                            return null;
                        } else {
                            BigDecimal bd = getItem();
                            return Globals.numberFormat.format(bd);
                        }
                    }, emptyProperty(), itemProperty()));

                    if(item == null || b){
                        setGraphic(null);
                    }else{
                        setGraphic(text);
                    }
                }
            };
            return cell;
        });

        clmSeeDetails.setReorderable(false);
        clmSeeDetails.setCellFactory(t->{
            TableCell<Monmenu, Void> cell = new TableCell<>(){
                Button selectButton = new Button("Chi tiết");
                {
                    selectButton.setOnAction(t->{
                        MonAns_Table.setDisable(true);
                        SearchArea.setDisable(true);
                        selectedMonmenu = getTableView().getItems().get(getIndex());
                        seeDetails_OfMonmenu(selectedMonmenu);
                        DeselectButton.setDisable(false);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    setGraphic(empty ? null : selectButton);
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
    }

    private void RefreshData(){
        MonAns_Table.getItems().clear();
        Task<List<Monmenu>> getAllMonmenus = new Task<List<Monmenu>>() {
            @Override
            protected List<Monmenu> call() throws Exception {
                return MonmenuDAO.getInstance().getAll();
            }
        };
        getAllMonmenus.setOnSucceeded((t)->{
            List<Monmenu> list = getAllMonmenus.getValue();
            System.out.println(list);
            usableMonAn = FXCollections.observableList(list);
            MonAns_Table.setItems(usableMonAn);
            toggleLoadingMode(false);
        });

        toggleLoadingMode(true);
        seeDetails_OfMonmenu(null);
        ThreadPool.getInstance().submitTask(getAllMonmenus);
    }

    private void toggleLoadingMode(boolean isLoading){
        QuanLyMonAn_BorderPane.setDisable(isLoading);
        QuanLyMonAn_BorderPane.setVisible(!isLoading);
        loadingOverlay.setVisible(isLoading);
    }

    private void seeDetails_OfMonmenu(Monmenu monmenu){
        if(monmenu == null){
            ChiTietMonAn_Pane.setVisible(false);
            SelectionPrompt_Overlay.setVisible(true);
        }else{
            LichSuGia_Table.getItems().clear();
            ChiTietMonAn_Pane.setVisible(true);
            SelectionPrompt_Overlay.setVisible(false);

            ChiTietMonAn_TenMon.setText(monmenu.getTenmon());
            ChiTietMonAn_GiaBan.setText(Globals.numberFormat.format(monmenu.getGiaban()));
            ChiTietMonAn_TrangThaiBan.setText(monmenu.getTrangthai() ? "Đang bán" : "Đã ngừng");

            boolean isError = false;
            String imageLink = monmenu.getHinhanh();
            Image testImage;
            if(imageLink == null || imageLink.length() == 0){
                testImage = defaultImage;
            }else{
                try{
                    testImage = new Image(monmenu.getHinhanh());
                } catch (Exception ex){
                    testImage = defaultImage;
                    isError = true;
                }
                if(testImage.isError()) {
                    testImage = defaultImage;
                    isError = true;
                }
            }
            ChiTietMonAn_ImageMon.setImage(testImage);
            if(isError){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Hình ảnh món ăn không thể được tải về");
                alert.setContentText("Đường dẫn đến hình ảnh có lỗi, không thể hiển thị ảnh.");
                alert.show();
            }
            toggleLoadingMode(true);
            if(monmenu.getMamon() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Không thể tìm thấy lịch sử với mã món");
                alert.setContentText("Không thể tìm thấy lịch sử giá ứng với mã món.");
                alert.show();
                toggleLoadingMode(false);
                return;
            }
            Task<List<LichsuGiamon>> getLSGia = new Task<List<LichsuGiamon>>() {
                @Override
                protected List<LichsuGiamon> call() throws Exception {
                    return LichsuGiamonDAO.getInstance().getLS_FromMonmenuID(monmenu.getMamon());
                }
            };
            getLSGia.setOnSucceeded(t->{
                List<LichsuGiamon> temp = getLSGia.getValue();
                if(temp == null || temp.size() == 0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Không tìm thấy kết quả phù hợp");
                    alert.setContentText("Không thể tìm thấy bất kì lịch sử giá nào của món");
                    alert.show();
                    toggleLoadingMode(false);
                    return;
                }
                LichSuGia_Table.setItems(FXCollections.observableList(temp));
                toggleLoadingMode(false);
            });
            ThreadPool.getInstance().submitTask(getLSGia);
        }
    }


    @FXML
    private void ReturnButton_OnClick(MouseEvent mouseEvent) {

    }

    @FXML
    private void AddNewButton_Clicked(MouseEvent mouseEvent) {
        toggleLoadingMode(true);
        Stage s = (Stage)((Node) mouseEvent.getSource()).getScene().getWindow();
        Scene scene;
        try{
            loader.setLocation(getClass().getClassLoader().getResource("FXML\\Them_ChinhSua_MonAn.fxml"));
            loader.setRoot(null);
            loader.setController(null);
            Parent temp = loader.load();
            scene = new Scene(temp, 600, 400);
            s.setResizable(false);
        }catch(IOException e){
            e.printStackTrace(System.err);
            toggleLoadingMode(false);
            return;
        }
        ThemChinhSuaMonAnController controller = loader.getController();
        controller.setMonmenu(null);
        toggleLoadingMode(false);
        s.setScene(scene);
    }

    @FXML
    private void RefreshButton_Clicked(MouseEvent mouseEvent) {
        selectedMonmenu = null;
        DeselectButton.setDisable(true);
        seeDetails_OfMonmenu(null);
        MonAns_Table.setDisable(false);
        SearchArea.setDisable(false);
        RefreshData();
    }

    @FXML
    private void DeselectButton_Clicked(MouseEvent mouseEvent) {
        selectedMonmenu = null;
        DeselectButton.setDisable(true);
        seeDetails_OfMonmenu(null);
        MonAns_Table.setDisable(false);
        SearchArea.setDisable(false);
    }

    @FXML
    private void SearchButton_OnClick(MouseEvent mouseEvent) {
        String searchStr = SearchBar.getText();
        if(searchStr == null || searchStr.length() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Không thể tìm với keyword trống");
            alert.setContentText("Vui lòng cung cấp keyword có độ dài > 1");
            alert.show();
            return;
        }
        toggleLoadingMode(true);
        switch(SearchKeywordType_ComboBox.getSelectionModel().getSelectedIndex()){
            case 0:
                Integer value = Integer.valueOf(searchStr);
                Task<List<Monmenu>> getByID = new Task<List<Monmenu>>() {
                    @Override
                    protected List<Monmenu> call() throws Exception {
                        return MonmenuDAO.getInstance().findMonmenuByID(value);
                    }
                };
                getByID.setOnSucceeded(t->{
                    List<Monmenu> list = getByID.getValue();
                    if(list == null || list.size() == 0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Không thể tìm thấy");
                        alert.setContentText("Không thể tìm thấy dữ liệu phù hợp keyword");
                        alert.show();
                    }
                    usableMonAn = FXCollections.observableList(list);
                    MonAns_Table.setItems(usableMonAn);
                    toggleLoadingMode(false);
                });
                ThreadPool.getInstance().submitTask(getByID);
                break;
            case 1:
                Task<List<Monmenu>> getByName = new Task<List<Monmenu>>() {
                    @Override
                    protected List<Monmenu> call() throws Exception {
                        return MonmenuDAO.getInstance().findMonmenuByName(searchStr);
                    }
                };
                getByName.setOnSucceeded(t->{
                    List<Monmenu> list = getByName.getValue();
                    if(list == null || list.size() == 0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Không thể tìm thấy");
                        alert.setContentText("Không thể tìm thấy dữ liệu phù hợp keyword");
                        alert.show();
                    }
                    usableMonAn = FXCollections.observableList(list);
                    MonAns_Table.setItems(usableMonAn);
                    toggleLoadingMode(false);
                });
                ThreadPool.getInstance().submitTask(getByName);
                break;
        }
    }
}
