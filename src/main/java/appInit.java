import Controllers.HandleJarFileWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class appInit extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("HandleJarFileWindow.fxml"));
        Parent root = fxmlLoader.load();
        stage.getIcons().add(new Image("images/icon.png"));
        stage.setTitle("jarApp");
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();

        HandleJarFileWindowController controllerToSend = fxmlLoader.getController();
        controllerToSend.setController(controllerToSend);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
