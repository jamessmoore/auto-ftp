package com.github.autoftp.schedule;

import java.util.List;

import jftp.connection.FtpFile;

import com.github.autoftp.ConnectionListener;

public class TestRunner implements ConnectionListener {

	public void run() {
		
		ConnectionSchedule connectionScheduler = new ConnectionSchedule();
		connectionScheduler.registerListener(this);
		
		Thread thread = new Thread(connectionScheduler);
		thread.start();
	}
	
	@Override
	public void onConnection() {
		System.out.println("Connected to server.");
	}

	@Override
	public void onDisconnection() {
		System.out.println("Disconnected from server");
	}

	@Override
	public void onFilterListObtained(List<FtpFile> files) {
		System.out.println("Found files to download: ");
		
		for (FtpFile file : files)
			System.out.println("\t" + file.getName());
	}

	@Override
	public void onError(String errorMessage) {
		System.out.println("There was an error: " + errorMessage);
	}

	@Override
	public void onDownloadStarted(String filename) {
		System.out.println("Started downloading " + filename);
	}

	@Override
	public void onDownloadFinished(String filename) {
		System.out.println("Download complete.");
	}

}
