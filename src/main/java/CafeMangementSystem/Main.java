package CafeMangementSystem;

import CafeMangementSystem.Utils.HibernateUtils;
import CafeMangementSystem.Utils.ThreadPool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        HibernateUtils.getSessionFactory();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/FXML/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("global.css");
        stage.setTitle("Quản lý quán café - Cafe Mangement");
        stage.getIcons().add(
                new Image(
                        getClass().getResourceAsStream( "/Default_Images/app-icon.png" )));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop(){
        HibernateUtils.shutdown();
        ThreadPool.getInstance().shutdownPool();
    }
}
