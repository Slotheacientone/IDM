import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DownloadController {
	@FXML
	private TextFlow info;

	@FXML
	private TextFlow console;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Button resumeButton;

	@FXML
	private Button pauseButton;
	@FXML
	private Button startButton;
	private ObservableList listTextConsole;
	private ObservableList listTextInfo;
	private ExecutorService pool;
	private List<ChannelDownload> list;
	private long length;
	private long progress = 0;
	public void updateProgressBar(long progressThread) {
		progress= progress+ progressThread;
		progressBar.setProgress(progress/(double)length);
	}
	public void updateConsole(Text text) {
		listTextConsole.add(text);
		
	}

	public void showInfo(String stringUrl, String thread) throws IOException, InterruptedException {
		Download d = new Download();
		d.setDownloadController(this);
		list = d.getList();
		pool = d.getPool();
		listTextInfo = info.getChildren();
		listTextConsole = console.getChildren();
		listTextInfo.add(new Text("Địa chỉ: " + stringUrl + "\n"));
		listTextInfo.add(new Text("Số luồng: " + thread + "\n"));
		URL url = new URL(stringUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		length = urlConnection.getContentLengthLong();
		listTextInfo.add(new Text("Kích thước file: " + length + "\n"));
		// check whether server support partial content retrieval
		boolean support = urlConnection.getHeaderField("Accept-Ranges").equals("bytes");
		// System.out.println("Partial content retrieval support = " + (support ? "Yes"
		// : "No"));
		Text text1;
		if (support) {
			text1 = new Text("Hỗ trợ tải xuống đa phần: có" + "\n");
		} else {
			text1 = new Text("Hỗ trợ tải xuống đa phần: không" + "\n");
		}
		listTextInfo.add(text1);

		pauseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
							d.pauseAllThread();
						
				
					listTextConsole.add(new Text("pause \n"));
					System.out.println("pause");
				

			}
		});
		resumeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				d.pauseAllThread();
				listTextConsole.add(new Text("resume \n"));
				System.out.println("resume");
			}
		});
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				d.setStringUrl(stringUrl);
				d.setThread(thread);	
				d.start();
				
			}
		});
	}

}
