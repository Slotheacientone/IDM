import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import javafx.scene.text.Text;

public class ChannelDownload extends Thread {
	private MappedByteBuffer mappedByteBuffer;
	private BufferedInputStream bis;
	private URL url;
	private long start;
	private long finish;
	private DownloadController downloadController;

	public ChannelDownload(MappedByteBuffer mappedByteBuffer, URL url, long start, long finish,
			DownloadController downloadController) throws IOException {
		this.mappedByteBuffer = mappedByteBuffer;
		this.url = url;
		this.downloadController = downloadController;
		this.start = start;
		this.finish = finish;
		HttpURLConnection uConnection = (HttpURLConnection) url.openConnection();
		uConnection.setRequestProperty("Range", "bytes=" + start + "-" + finish);
		uConnection.connect();
		bis = new BufferedInputStream(uConnection.getInputStream());
	}

	@Override
	public void run() {
		//downloadController.updateConsole(new Text("Start"));
		System.out.println("start");
		byte[] data = new byte[1024];
		int length;
		try {
			while ((length = bis.read(data)) != -1) {
				mappedByteBuffer.put(data, 0, length);
				System.out.println(data);
				downloadController.updateProgressBar(length);
			}
		} catch (IOException e) {
			return;
		}
//downloadController.updateConsole(new Text("finish"));
		System.out.println("finish");
	}

	public void pauseThread() {
		try {
			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resumeThread() throws IOException {
		HttpURLConnection uConnection = (HttpURLConnection) url.openConnection();
		uConnection.setRequestProperty("Range", "bytes=" + start+(mappedByteBuffer.capacity()-mappedByteBuffer.remaining()) + "-" + finish);
		uConnection.connect();
		bis = new BufferedInputStream(uConnection.getInputStream());
		run();
	}
}
