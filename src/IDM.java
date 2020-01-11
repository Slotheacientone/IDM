import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IDM extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("idmview.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("IDM");
		primaryStage.setScene(new Scene(root, 770, 335));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}