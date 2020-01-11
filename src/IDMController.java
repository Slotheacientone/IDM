import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class IDMController implements Initializable {
	@FXML
	private TextField url;

	@FXML
	private TextField thread;

	@FXML
	private Button startButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String stringThread = thread.getText();
				String stringUrl = url.getText();
				if (!stringThread.isEmpty() && !stringUrl.isEmpty()) {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("downloadView.fxml"));
						Parent root;
						root = loader.load();
						Stage stage = new Stage();
						stage.setScene(new Scene(root, 770, 460));
						stage.setTitle("Download");
						stage.show();
						DownloadController downloadController = loader.getController();
						downloadController.showInfo(stringUrl, stringThread);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

	}
}
