package com.github.autoftp.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jftp.client.Client;
import jftp.client.ClientFactory;
import jftp.connection.Connection;
import jftp.connection.FtpFile;
import jftp.exception.ClientDisconnectionException;
import jftp.exception.ConnectionInitialisationException;
import jftp.exception.DownloadFailedException;
import jftp.exception.FileListingException;
import jftp.exception.NoSuchDirectoryException;

import org.joda.time.DateTime;

import com.github.autoftp.PatternBuilder;
import com.github.autoftp.config.HostConfig;
import com.github.autoftp.config.SettingsProvider;

public class ConnectionSchedule extends ConnectionNotifier implements Runnable {

	private Client client;
	private Connection connection;
	private ClientFactory clientFactory;
	private SettingsProvider settingsProvider;
	private PatternBuilder patternBuilder;

	public ConnectionSchedule() {

		clientFactory = new ClientFactory();
		settingsProvider = new SettingsProvider("/etc/autoftp/autoftp.conf");
		patternBuilder = new PatternBuilder();
	}

	@Override
	public void run() {

		HostConfig host = settingsProvider.getHost();

		if (openConnectionToHost(host)) {

			try {

				moveToRemoteDownloadFolder(host.getFileDirectory());

				downloadFilteredFiles(retrieveFilesAfterLastScan());

			} catch (NoSuchDirectoryException e) {
				notifyOfError(e.getMessage());
			}

			closeConnectionToHost();
		}
	}

	protected boolean openConnectionToHost(HostConfig host) {

		client = clientFactory.createClient(host.getClientType());

		client.setHost(host.getHostname());
		client.setPort(host.getPort());
		client.setCredentials(host.getUsername(), host.getPassword());

		try {

			connection = client.connect();

			notifyOfConnectionOpening();

			return true;

		} catch (ConnectionInitialisationException e) {
			notifyOfError(e.getMessage());
		}

		return false;
	}

	protected void closeConnectionToHost() {

		try {

			client.disconnect();

			notifyOfConnectionClosing();

		} catch (ClientDisconnectionException e) {
			notifyOfError(e.getMessage());
		}
	}

	protected List<FtpFile> retrieveFilesAfterLastScan() {

		List<FtpFile> files = new ArrayList<FtpFile>();

		try {

			DateTime lastRun = settingsProvider.getLastRunDate();

			files = connection.listFiles();

			Iterator<FtpFile> fileIterator = files.iterator();

			while (fileIterator.hasNext()) {

				FtpFile currentFile = fileIterator.next();

				if (currentFile.getLastModified().isBefore(lastRun) || currentFile.isDirectory())
					fileIterator.remove();
			}

		} catch (FileListingException e) {
			notifyOfError(e.getMessage());
		}

		return files;
	}

	protected List<FtpFile> filterFilesToCreateDownloadQueue(List<FtpFile> filesToFilter) {

		List<FtpFile> filteredFiles = new ArrayList<FtpFile>();
		List<String> filterExpressions = settingsProvider.getFilterExpressions();

		for (FtpFile file : filesToFilter) {

			for (String expression : filterExpressions) {

				String expressionRegex = patternBuilder.buildFromFilterString(expression);

				if (file.getName().toLowerCase().matches(expressionRegex.toLowerCase()))
					filteredFiles.add(file);
			}
		}

		if (!filteredFiles.isEmpty())
			notifyOfFilesToDownload(filteredFiles);

		settingsProvider.setLastRunDate(DateTime.now());

		return filteredFiles;
	}

	protected void downloadFile(FtpFile fileToDownload) {

		String downloadDirectory = settingsProvider.getDownloadDirectory();

		notifyOnDownloadStart(fileToDownload.getName());

		try {

			connection.download(fileToDownload, downloadDirectory);

			notifyOnDownloadFinished(fileToDownload.getName());

		} catch (DownloadFailedException e) {
			notifyOfError(e.getMessage() + (null == e.getCause() ? "" : e.getCause().getMessage()));
		}
	}

	private void moveToRemoteDownloadFolder(String remoteDirectory) {
		connection.setRemoteDirectory(remoteDirectory);
	}

	private void downloadFilteredFiles(List<FtpFile> files) {

		if (!files.isEmpty()) {

			List<FtpFile> filtered = filterFilesToCreateDownloadQueue(files);

			for (FtpFile file : filtered)
				downloadFile(file);
		}
	}
}
