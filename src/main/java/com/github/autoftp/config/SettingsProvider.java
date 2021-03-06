package com.github.autoftp.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jftp.client.ClientFactory.ClientType;
import jftp.exception.FileConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.joda.time.DateTime;

public class SettingsProvider {

	private static final String INTERVAL = "interval";
	private static final String HOST_FILE_DIR = "host.file-dir";
	private static final String HOST_PORT = "host.port";
	private static final String HOST_TYPE = "host.type";
	private static final String HOST_PASSWORD = "host.password";
	private static final String HOST_USER = "host.user";
	private static final String HOST_NAME = "host.name";
	private static final String LAST_RUN = "last-run";
	private static final String APP_DOWNLOAD_DIR = "download-dir";
	private static final String FILE_FILTER_LIST = "filters.expression";
	private static final String MOVE_ENABLED = "move.enabled";
	private static final String MOVE_DIRECTORY = "move.directory";
	private static final String PUSHBULLET_API_KEY = "pushbullet.api.key";
	private static final String PUSHBULLET_NOTIFICATIONS_ENABLED = "pushbullet.notify.enabled";

	private String configFileLocation;

	private PropertiesConfiguration propertiesConfiguration;

	public SettingsProvider(String configFileLocation) {

		this.configFileLocation = configFileLocation;
		
		loadConfig();
	}

	protected void loadConfig() {

		File xmlConfigFile = new File(configFileLocation);

		try {

			if (!xmlConfigFile.exists())
				xmlConfigFile.createNewFile();

			propertiesConfiguration = new PropertiesConfiguration(xmlConfigFile);
			propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());

		} catch (ConfigurationException e) {
			throw new FileConfigurationException("Unable to load config file", e);
		} catch (IOException e) {
			throw new FileConfigurationException("Unable to load config file", e);
		}
	}

	public void setDownloadDirectory(String directory) {

		propertiesConfiguration.setProperty(APP_DOWNLOAD_DIR, directory);

		saveConfig();
	}

	public String getDownloadDirectory() {
		return propertiesConfiguration.getString(APP_DOWNLOAD_DIR);
	}

	public boolean isMoveEnabled() {
		return propertiesConfiguration.getBoolean(MOVE_ENABLED);
	}

	public void setMoveEnabled(boolean moveEnabled) {

		propertiesConfiguration.setProperty(MOVE_ENABLED, moveEnabled);

		saveConfig();
	}

	public String getMoveDirectory() {
		return propertiesConfiguration.getString(MOVE_DIRECTORY);
	}

	public String getPushbulletApiKey() {
		return propertiesConfiguration.getString(PUSHBULLET_API_KEY);
	}

	public void setPushbulletApiKey(String key) {

		propertiesConfiguration.setProperty(PUSHBULLET_API_KEY, key);

		saveConfig();
	}
	
	public boolean isPushbulletNotificationEnabled() {
		return propertiesConfiguration.getBoolean(PUSHBULLET_NOTIFICATIONS_ENABLED);
	}
	
	public void setPushbulletNotificationEnabled(boolean enabled) {
		
		propertiesConfiguration.setProperty(PUSHBULLET_NOTIFICATIONS_ENABLED, enabled);

		saveConfig();
	}

	public void setMoveDirectory(String moveDirectory) {

		propertiesConfiguration.setProperty(MOVE_DIRECTORY, moveDirectory);

		saveConfig();
	}

	public void setFilterExpressions(List<String> filters) {

		propertiesConfiguration.setProperty(FILE_FILTER_LIST, filters);

		saveConfig();
	}

	public List<String> getFilterExpressions() {

		List<Object> configObjects = propertiesConfiguration.getList(FILE_FILTER_LIST);
		List<String> filters = new ArrayList<String>();

		for (Object object : configObjects)
			filters.add(object.toString());

		return filters;
	}

	public void setLastRunDate(DateTime date) {

		propertiesConfiguration.setProperty(LAST_RUN, date.getMillis());

		saveConfig();
	}

	public DateTime getLastRunDate() {

		long timeAsOfNowInMilliseconds = DateTime.now().getMillis();
		long lastRunInMilliseconds = propertiesConfiguration.getLong(LAST_RUN, timeAsOfNowInMilliseconds);

		return new DateTime(lastRunInMilliseconds);
	}

	public void setHost(HostConfig hostConfig) {

		propertiesConfiguration.setProperty(HOST_NAME, hostConfig.getHostname());
		propertiesConfiguration.setProperty(HOST_PASSWORD, hostConfig.getPassword());
		propertiesConfiguration.setProperty(HOST_PORT, hostConfig.getPort());
		propertiesConfiguration.setProperty(HOST_TYPE, hostConfig.getClientType().toString());
		propertiesConfiguration.setProperty(HOST_USER, hostConfig.getUsername());
		propertiesConfiguration.setProperty(HOST_FILE_DIR, hostConfig.getFileDirectory());

		saveConfig();
	}

	public HostConfig getHost() {

		HostConfig hostConfig = new HostConfig();

		hostConfig.setHostname(propertiesConfiguration.getString(HOST_NAME));
		hostConfig.setPort(propertiesConfiguration.getInt(HOST_PORT));
		hostConfig.setUsername(propertiesConfiguration.getString(HOST_USER));
		hostConfig.setPassword(propertiesConfiguration.getString(HOST_PASSWORD));
		hostConfig.setClientType(ClientType.valueOf(propertiesConfiguration.getString(HOST_TYPE)));
		hostConfig.setFileDirectory(propertiesConfiguration.getString(HOST_FILE_DIR));

		return hostConfig;
	}

	public void setConnectionInterval(int minutes) {

		propertiesConfiguration.setProperty(INTERVAL, minutes);

		saveConfig();
	}

	public int getConnectionInterval() {
		return propertiesConfiguration.getInt(INTERVAL);
	}

	private void saveConfig() {

		try {
			propertiesConfiguration.save();

		} catch (ConfigurationException e) {

			throw new FileConfigurationException("Unable to save new configuration property.", e);
		}
	}
}
