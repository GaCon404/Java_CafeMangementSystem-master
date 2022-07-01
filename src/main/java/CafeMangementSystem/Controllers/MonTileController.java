package CafeMangementSystem.Controllers;

import CafeMangementSystem.Entities.Monmenu;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MonTileController implements Initializable {
    private Monmenu monmenu;

    @FXML
    private GridPane monTileGridPane;

    @FXML
    private ImageView hinhanhImageView;

    @FXML
    private Label tenmonLabel;

    @FXML
    private Label ngungBanLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getMonTileGridPane().setOnMouseClicked(mouseEvent -> System.out.println(monmenu.getTenmon() + " is cliked!"));
    }

    public MonTileController() {
    }

    public MonTileController(Monmenu monmenu) {
        this.monmenu = monmenu;
        this.load();
        setComponents();
    }

    private void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MonTile.fxml"));
        try {
            if (loader.getController() == null) {
                loader.setController(this);
            }
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setComponents() {
        String url = this.monmenu.getHinhanh();

        if ( url.isEmpty()) {
            this.hinhanhImageView.setImage(new Image("food-images\\no-image-food.jpg"));
        } else {
            this.hinhanhImageView.setImage(new Image(monmenu.getHinhanh()));
        }

        this.tenmonLabel.setText(monmenu.getTenmon());

        // Món hiện đang ngừng bán
        if (!this.monmenu.getTrangthai()) {
            this.monTileGridPane.setDisable(true);
            this.ngungBanLabel.setVisible(true); // show thẻ ngừng bán
            this.hinhanhImageView.setOpacity(0.3);
        } else {
            this.ngungBanLabel.setVisible(false); // ẩn thẻ ngừng bán
        }
    }



    public GridPane getMonTileGridPane() {
        return monTileGridPane;
    }

    public void setMonTileGridPane(GridPane monTileGridPane) {
        this.monTileGridPane = monTileGridPane;
    }

    public ImageView getHinhanhImageView() {
        return hinhanhImageView;
    }

    public void setHinhanhImageView(ImageView hinhanhImageView) {
        this.hinhanhImageView = hinhanhImageView;
    }

    public Label getTenmonLabel() {
        return tenmonLabel;
    }

    public void setTenmonLabel(Label tenmonLabel) {
        this.tenmonLabel = tenmonLabel;
    }

    public Label getNgungBanLabel() {
        return ngungBanLabel;
    }

    public void setNgungBanLabel(Label ngungBanLabel) {
        this.ngungBanLabel = ngungBanLabel;
    }
}
