import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.scene.text.Text;

public class Download extends Thread {

	private List<ChannelDownload> list = new ArrayList<ChannelDownload>();
	private ExecutorService pool;
	private String stringUrl;
	private String thread;
	private DownloadController downloadController;
	
	public void setDownloadController(DownloadController downloadController) {
		this.downloadController = downloadController;
	}

	public void setStringUrl(String stringUrl) {
		this.stringUrl = stringUrl;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public List<ChannelDownload> getList() {
		return list;
	}

	public ExecutorService getPool() {
		return pool;
	}
	public void pauseAllThread() {
		for(ChannelDownload channel: list) {
			channel.pauseThread();
		}
	}
	public void resumeAllThread() throws IOException {
		for(ChannelDownload channel: list) {
			channel.resumeThread();
		}
	}

	@Override
	public void run() {
		try {
			URL url = new URL(stringUrl);
			int numberChannels = Integer.parseInt(thread);
			pool = Executors.newFixedThreadPool(numberChannels);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			long length = urlConnection.getContentLengthLong();
			long lengthPerChannel = length / numberChannels;
			long lengthLastChannel = length - (lengthPerChannel * (numberChannels - 1));

			// get file name
			String raw = urlConnection.getHeaderField("Content-Disposition");
			String fileName = "";
			// raw = "attachment; filename=abc.jpg"
			if (raw != null && raw.indexOf("=") != -1) {
				fileName = raw.split("=")[1]; // getting value after '='
			} else {
				String[] s = url.getFile().split("/");
				fileName = s[s.length - 1];
			}
			File file = new File("/home/slo/" + fileName);
			if (file.exists()) {
				String[] s = fileName.split("\\.");
				file = new File("/home/slo/" + s[0] + "(1)." + s[1]);
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			FileChannel fileChannel = raf.getChannel();
			long start = System.currentTimeMillis();
			for (int i = 0; i < numberChannels; i++) {
				if (i == (numberChannels - 1)) {

					MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,
							i * lengthPerChannel, lengthLastChannel);
					ChannelDownload c = new ChannelDownload(mappedByteBuffer, url, (i * lengthPerChannel),
							((i * lengthPerChannel) + lengthLastChannel - 1), downloadController);
					list.add(c);
					pool.execute(c);

				} else {
					MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,
							i * lengthPerChannel, lengthPerChannel);
					ChannelDownload c = new ChannelDownload(mappedByteBuffer, url, (i * lengthPerChannel),
							((i * lengthPerChannel) + lengthPerChannel - 1), downloadController);
					list.add(c);
					pool.execute(c);
				}
			}
			pool.shutdown(); // Disable new tasks from being submitted
			while (!pool.isTerminated()) {

			}
			
			
			  long finish = System.currentTimeMillis(); String s = "Thời gian download: " +
			  (finish - start) + "ms"; System.out.println(s);
			 
			// listTextConsole.add(new Text(s));
			fileChannel.close();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
