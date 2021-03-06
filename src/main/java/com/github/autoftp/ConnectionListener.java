package com.github.autoftp;

import java.util.List;

import jftp.connection.FtpFile;

public interface ConnectionListener {

	void onConnection();
	void onDisconnection();
	void onFilterListObtained(List<FtpFile> files);
	void onError(String errorMessage);
	void onDownloadStarted(String filename);
	void onDownloadFinished(String filename);
}
