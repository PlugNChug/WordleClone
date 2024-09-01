import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.stage.Stage;
 
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {  
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("scene.fxml"));
            Scene scene = new Scene(root);
        
            primaryStage.setTitle("Wordle Clone!");
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("resources/icon.png"));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}