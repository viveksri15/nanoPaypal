package com.nanoPaypal.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FileLogger: appends logs in a file
 * @author vivek
 *
 */
public class FileLogger {
	private class LogWriter implements Runnable {

		FileWriter fw;
		ArrayList<Object> data;

		public LogWriter(FileWriter fileName, ArrayList<Object> data) {
			this.fw = fileName;
			this.data = data;
		}

		@Override
		public void run() {
			try {
				StringBuffer toWrite = new StringBuffer();
				for (Object k : data) {
					toWrite.append(k + ",");
				}
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				fw.append(dateFormat.format(Calendar.getInstance().getTime())
						+ "," + toWrite.toString() + "\n");
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<String, ExecutorService> poolMap = new HashMap<String, ExecutorService>();

	private static Map<String, FileWriter> fileWriterPool = new HashMap<String, FileWriter>();

	private ArrayList<Object> data = new ArrayList<Object>();

	private String trackType = null;

	public FileLogger(String trackType) {
		this.trackType = trackType;
	}

	public FileLogger(String trackType, boolean retainInsertionOrder) {
		this.trackType = trackType;
		if (retainInsertionOrder)
			data = new ArrayList<Object>();
	}

	/**
	 * Appends log
	 */
	public FileLogger addLog(Object value) {
		data.add(value);
		return this;
	}

	/**
	 * Finally, write in the file
	 */
	public void writeInfile() {
		String dir = "logs";
		File dirf = new File(dir);
		if (!dirf.exists())
			dirf.mkdir();
		if (dir.endsWith("/"))
			dir = dir.substring(0, dir.length() - 1);

		String mnth = (Calendar.getInstance().get(Calendar.MONTH) + 1) + "";
		String dt = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "";

		if (mnth.length() < 2)
			mnth = "0" + mnth;
		if (dt.length() < 2)
			dt = "0" + dt;

		String fileName = dir + "/" + trackType + "_"
				+ Calendar.getInstance().get(Calendar.YEAR) + "_" + mnth + "_"
				+ dt + ".log";
		ExecutorService pool = poolMap.get(fileName);
		FileWriter fw = fileWriterPool.get(fileName);
		if (pool == null || fw == null) {
			synchronized (poolMap) {
				Iterator<String> it = poolMap.keySet().iterator();
				ArrayList<String> removalList = new ArrayList<String>();
				String key;
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.startsWith(dir)
							&& key.contains(trackType.toString())
							&& key.length() == fileName.length()) {
						removalList.add(key);
					}
				}
				for (String s : removalList) {
					poolMap.remove(s);
					FileWriter fw_remove = fileWriterPool.get(s);
					if (fw_remove != null) {
						try {
							fw_remove.flush();
							fw_remove.close();
						} catch (IOException e) {
							e.printStackTrace();
							e.printStackTrace(System.out);
							e.printStackTrace(System.out);
						}
					}
					fileWriterPool.remove(s);
				}
			}
			File f = new File(fileName);
			try {
				fw = new FileWriter(f, true);
				pool = Executors.newSingleThreadExecutor();
				poolMap.put(fileName, pool);
				fileWriterPool.put(fileName, fw);
			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace(System.out);
				e.printStackTrace(System.out);
			}
		}
		if (fw != null)
			pool.execute(new LogWriter(fw, data));
	}
}