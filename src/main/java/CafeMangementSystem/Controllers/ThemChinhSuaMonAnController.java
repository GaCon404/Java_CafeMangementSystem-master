package CafeMangementSystem.Controllers;

import CafeMangementSystem.DAOs.MonmenuDAO;
import CafeMangementSystem.Entities.Monmenu;
import CafeMangementSystem.Utils.BigDecimalTextFormatter;
import CafeMangementSystem.Utils.ThreadPool;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ThemChinhSuaMonAnController implements Initializable {
    FXMLLoader loader;
    Monmenu Monmenu_beingEditted;
    Image defaultImage;
    FileChooser fileChooser;
    Stage stageForFileChooser;

    @FXML
    private BorderPane ThemMonAn_BorderPane;
    @FXML
    private Button Add_Edit_MonAnButton;
    @FXML
    private Label pageTitle;
    @FXML
    private TextField TenMonAn_Field;
    @FXML
    private TextField GiaBan_Field;
    @FXML
    private VBox outsideImageView_VBox;
    @FXML
    private TextField imageLink_Field;
    @FXML
    private ImageView MonAnImage_Display;
    @FXML
    private VBox loadingOverlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loader = new FXMLLoader();
        MonAnImage_Display.fitWidthProperty().bind(outsideImageView_VBox.widthProperty());
        MonAnImage_Display.fitHeightProperty().bind(outsideImageView_VBox.heightProperty());

        defaultImage = new Image("/Default_Images/fast-food.png");
        stageForFileChooser = new Stage();
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        BigDecimalTextFormatter textFormatter = new BigDecimalTextFormatter(BigDecimalTextFormatter.defaultConverter, new BigDecimal("0.0"), BigDecimalTextFormatter.defaultFilter1);
        GiaBan_Field.setTextFormatter(textFormatter);
        TenMonAn_Field.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            TenMonAn_Field.setText(newValue.substring(0, Math.min(newValue.length(), 30)));
        });
    }

    public void setMonmenu(Monmenu m){
        Monmenu_beingEditted = m;
        toggleLoadingMode(true);
        if(Monmenu_beingEditted == null){
            pageTitle.setText("Thêm món ăn mới");
            Add_Edit_MonAnButton.setText("Thêm mới món ăn này");
            MonAnImage_Display.setImage(defaultImage);
            imageLink_Field.setText(null);
            TenMonAn_Field.clear();
            GiaBan_Field.clear();
            toggleLoadingMode(false);
        }else{
            if(m.getMamon() == null){
                pageTitle.setText("Thêm món ăn mới");
                Add_Edit_MonAnButton.setText("Thêm mới món ăn này");
                MonAnImage_Display.setImage(defaultImage);
                imageLink_Field.setText(null);
                TenMonAn_Field.clear();
                GiaBan_Field.clear();
                toggleLoadingMode(false);
                return;
            }
            Task<Monmenu> getRefreshedInfo = new Task<Monmenu>() {
                @Override
                protected Monmenu call() throws Exception {
                    return MonmenuDAO.getInstance().get(m.getMamon());
                }
            };
            getRefreshedInfo.setOnSucceeded(t->{
                Monmenu temp = getRefreshedInfo.getValue();
                if(temp == null){
                    pageTitle.setText("Thêm món ăn mới");
                    Add_Edit_MonAnButton.setText("Thêm mới món ăn này");
                    MonAnImage_Display.setImage(defaultImage);
                    imageLink_Field.setText(null);
                    TenMonAn_Field.clear();
                    GiaBan_Field.clear();
                    toggleLoadingMode(false);
                    return;
                }
                pageTitle.setText("Chỉnh sửa món ăn");
                Add_Edit_MonAnButton.setText("Sửa thông tin món ăn này");
                TenMonAn_Field.setText(temp.getTenmon());
                GiaBan_Field.setText(temp.getGiaban().toString());

                String linkString = temp.getHinhanh();
                Image imgFromlinkString;
                boolean isError = false;
                if(linkString == null || linkString.length() == 0){
                    imgFromlinkString = defaultImage;
                }else {
                    try {
                        imgFromlinkString = new Image(linkString);
                    } catch (Exception ex) {
                        imgFromlinkString = defaultImage;
                        imageLink_Field.setText(null);
                        isError = true;
                    }
                    if (imgFromlinkString.isError()) {
                        imgFromlinkString = defaultImage;
                        imageLink_Field.setText(null);
                        isError = true;
                    }
                }
                MonAnImage_Display.setImage(imgFromlinkString);
                imageLink_Field.setText(linkString);
                if(isError){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Hình ảnh  không thể được tải về");
                    alert.setContentText("Đường dẫn đến hình ảnh có lỗi, không thể hiển thị ảnh.");
                    alert.show();
                }

                toggleLoadingMode(false);
            });
            ThreadPool.getInstance().submitTask(getRefreshedInfo);
        }
    }

    public void toggleLoadingMode(boolean isLoading){
        ThemMonAn_BorderPane.setDisable(isLoading);
        loadingOverlay.setVisible(isLoading);
    }

    @FXML
    private void AddEditButton_OnClicked(MouseEvent mouseEvent) {
        if(TenMonAn_Field.getText().length() == 0 || GiaBan_Field.getText().length() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Thiếu thông tin cần thiết");
            alert.setContentText("Xin điền đủ các trường có đánh dấu (*)");
            alert.show();
            return;
        }
        Monmenu m;
        if(Monmenu_beingEditted == null){
            m = new Monmenu();
            m.setTrangthai(true);
        }else{
            m = Monmenu_beingEditted;
        }
        m.setTenmon(TenMonAn_Field.getText());
        m.setGiaban(new BigDecimal(GiaBan_Field.getText()));
        String linkImage = imageLink_Field.getText() == null ? "" : imageLink_Field.getText();
        m.setHinhanh(linkImage);

        Task<Void> insertOrUpdate = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            if(Monmenu_beingEditted == null)
                MonmenuDAO.getInstance().insert(m);
            else
                MonmenuDAO.getInstance().update(m.getMamon(), m);
            return null;
            }
        };

        insertOrUpdate.setOnSucceeded(t->{
            Stage s = (Stage)((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene scene;
            try{
//                loader.setLocation(getClass().getClassLoader().getResource("FXML\\QuanLyMonAn.fxml"));
//                loader.setRoot(null);
//                loader.setController(null);
//                Parent temp = loader.load();
//                scene = new Scene(temp, 800, 600);
//                scene.getStylesheets().add("global.css");
//                s.setResizable(false);
                loader.setLocation(getClass().getClassLoader().getResource("FXML\\AdminScene.fxml"));
                loader.setController(new AdminSceneController());
                Parent parent = loader.load();
                scene = new Scene(parent);
//                scene.getStylesheets().add("global.css");
                s.setResizable(false);
            }catch(IOException e){
                e.printStackTrace(System.err);
                toggleLoadingMode(false);
                return;
            }
            s.setScene(scene);
            toggleLoadingMode(false);
        });

        toggleLoadingMode(true);
        ThreadPool.getInstance().submitTask(insertOrUpdate);
    }

    @FXML
    private void GoBack_OnClicked(MouseEvent mouseEvent) {
        toggleLoadingMode(true);
        Stage s = (Stage)((Node) mouseEvent.getSource()).getScene().getWindow();
        Scene scene;
        try{
//            loader.setLocation(getClass().getClassLoader().getResource("FXML\\QuanLyMonAn.fxml"));
//            loader.setRoot(null);
//            loader.setController(null);
//            Parent temp = loader.load();
//            scene = new Scene(temp, 800, 600);
//            scene.getStylesheets().add("global.css");
//            s.setResizable(false);
            loader.setLocation(getClass().getClassLoader().getResource("FXML\\AdminScene.fxml"));
            loader.setController(new AdminSceneController());
            Parent parent = loader.load();
            scene = new Scene(parent);
        }catch(IOException e){
            e.printStackTrace(System.err);
            toggleLoadingMode(false);
            return;
        }
        toggleLoadingMode(false);
        s.setScene(scene);
    }

    @FXML
    private void BrowseImage_OnClicked(MouseEvent mouseEvent) {
        File chosenFile = fileChooser.showOpenDialog(stageForFileChooser);
        if(chosenFile != null){
            toggleLoadingMode(true);
            String linkString = chosenFile.toURI().toString();
            if(linkString.length() > 260){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Lỗi đường dẫn file quá dài");
                alert.setContentText("Đường dẫn file quá dài, xin cung cấp đường dẫn khác");
                alert.show();
                toggleLoadingMode(false);
                return;
            }
            imageLink_Field.setText(linkString);
            Image imgFromlinkString;
            boolean isError = false;
            try{
                imgFromlinkString = new Image(linkString);
            } catch (Exception ex){
                imgFromlinkString = defaultImage;
                imageLink_Field.setText(null);
                isError = true;
            }
            if(imgFromlinkString.isError()) {
                imgFromlinkString = defaultImage;
                imageLink_Field.setText(null);
                isError = true;
            }
            MonAnImage_Display.setImage(imgFromlinkString);
            if(isError){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Hình ảnh  không thể được tải về");
                alert.setContentText("Đường dẫn đến hình ảnh có lỗi, không thể hiển thị ảnh.");
                alert.show();
            }

            toggleLoadingMode(false);
        }
    }

    @FXML
    private void ResetDefaultImage_OnClicked(MouseEvent mouseEvent) {
        toggleLoadingMode(true);

        imageLink_Field.clear();
        MonAnImage_Display.setImage(defaultImage);

        toggleLoadingMode(false);
    }
}
