package com.github.autoftp.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jftp.client.ClientFactory.ClientType;
import jftp.exception.FileConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class SettingsProviderTest {

	private static final String CONFIG_FILE = "build/autoftp.conf";
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

	private static final String THIS_IS_A_DOWNLOAD_DIR = "this/is/a/download/dir";

	@InjectMocks
	private SettingsProvider settingsProvider = new SettingsProvider("build/autoftp.conf");

	@Mock
	private PropertiesConfiguration propertiesConfiguration;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		
		initMocks(this);
		
		when(propertiesConfiguration.getList(FILE_FILTER_LIST)).thenReturn(initFilterList());
	}

	@Test
	public void setDownloadDirectoryShouldCallPropertiesConfigurationToSetDownloadDirectory() throws ConfigurationException {

		settingsProvider.setDownloadDirectory(THIS_IS_A_DOWNLOAD_DIR);

		verify(propertiesConfiguration).setProperty(APP_DOWNLOAD_DIR, THIS_IS_A_DOWNLOAD_DIR);
		verify(propertiesConfiguration).save();
	}

	@Test
	public void ifConfigUnableToSaveThenShouldThrowConfigCorruptedException() throws ConfigurationException {

		doThrow(new ConfigurationException()).when(propertiesConfiguration).save();

		expectedException.expect(FileConfigurationException.class);
		expectedException.expectMessage(is(equalTo("Unable to save new configuration property.")));

		settingsProvider.setDownloadDirectory(THIS_IS_A_DOWNLOAD_DIR);
	}
	
	@Test
	public void setPushbulletNotificationEnabledShouldCallPropertiesConfigurationToSetMoveEnabled() throws ConfigurationException {
		
		settingsProvider.setPushbulletNotificationEnabled(true);
		
		verify(propertiesConfiguration).setProperty(PUSHBULLET_NOTIFICATIONS_ENABLED, true);
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void isPushbulletNotificationEnabledShouldReturnValueFromProperties() {
		
		when(propertiesConfiguration.getBoolean(PUSHBULLET_NOTIFICATIONS_ENABLED)).thenReturn(true);
		
		assertThat(settingsProvider.isPushbulletNotificationEnabled(), is(equalTo(true)));
	}

	@Test
	public void setMoveEnabledShouldCallPropertiesConfigurationToSetMoveEnabled() throws ConfigurationException {
		
		settingsProvider.setMoveEnabled(true);
		
		verify(propertiesConfiguration).setProperty(MOVE_ENABLED, true);
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void isMoveEnabledShouldReturnValueFromProperties() {
		
		when(propertiesConfiguration.getBoolean(MOVE_ENABLED)).thenReturn(true);
		
		assertThat(settingsProvider.isMoveEnabled(), is(equalTo(true)));
	}
	
	@Test
	public void setMoveDirectoryShouldCallPropertiesConfigurationToSetMoveEnabled() throws ConfigurationException {
		
		settingsProvider.setMoveDirectory("dir");
		
		verify(propertiesConfiguration).setProperty(MOVE_DIRECTORY, "dir");
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void getMoveDirectoryShouldReturnValueFromProperties() {
		
		when(propertiesConfiguration.getString(MOVE_DIRECTORY)).thenReturn("movedir");
		
		assertThat(settingsProvider.getMoveDirectory(), is(equalTo("movedir")));
	}
	
	@Test
	public void setPushbulletApiKeyShouldCallPropertiesConfigurationToSetMoveEnabled() throws ConfigurationException {
		
		settingsProvider.setPushbulletApiKey("apikey");
		
		verify(propertiesConfiguration).setProperty(PUSHBULLET_API_KEY, "apikey");
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void getPushbulletApiKeyShouldReturnValueFromProperties() {
		
		when(propertiesConfiguration.getString(PUSHBULLET_API_KEY)).thenReturn("apikey");
		
		assertThat(settingsProvider.getPushbulletApiKey(), is(equalTo("apikey")));
	}
	
	@Test
	public void getDownloadDirectoryShouldCalLPropertiesConfigurationToGetDownloadDirectory() {

		settingsProvider.getDownloadDirectory();

		verify(propertiesConfiguration).getString(APP_DOWNLOAD_DIR);
	}

	@Test
	public void getDownloadDirectoryShouldReturnValueFromPropertiesConfiguration() {

		when(propertiesConfiguration.getString(APP_DOWNLOAD_DIR)).thenReturn(THIS_IS_A_DOWNLOAD_DIR);

		assertThat(settingsProvider.getDownloadDirectory(), is(equalTo(THIS_IS_A_DOWNLOAD_DIR)));
	}

	@Test
	public void getFilterExpressionsShouldCallPropertiesConfigurationToReturnListOfStrings() {

		settingsProvider.getFilterExpressions();

		verify(propertiesConfiguration).getList(FILE_FILTER_LIST);
	}

	@Test
	public void objectListReturnedFromGetListMethodInPropertiesConfigurationShouldBeConvertedToStringList() {

		List<String> filters = settingsProvider.getFilterExpressions();

		assertThat(filters, hasItems("Filter 1", "Filter 2", "Filter 3"));
		
	}

	@Test
	public void listSizeReturnedFromGetFilterExpressionsShouldMatchListSizeReturnedFromPropertiesConfiguration() {

		assertThat(settingsProvider.getFilterExpressions().size(), is(equalTo(3)));
	}
	
	@Test
	public void setFilterExpressionsShouldTakeInListOfStringsAndCallPropertiesConfigurationsetProperty() {
		
		List<String> filters = new ArrayList<String>();
		filters.add("New String");
		
		settingsProvider.setFilterExpressions(filters);
		
		verify(propertiesConfiguration).setProperty(FILE_FILTER_LIST, filters);
	}
	
	@Test
	public void setConnectionIntervalShouldCallPropertiesConfiguration() throws ConfigurationException {
		
		settingsProvider.setConnectionInterval(5);
		
		verify(propertiesConfiguration).setProperty(INTERVAL, 5);
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void getConnectionIntervalShouldCallPropertiesConfigurationAndReturnValue() {
		
		when(propertiesConfiguration.getInt(INTERVAL)).thenReturn(3);
		
		assertThat(settingsProvider.getConnectionInterval(), is(equalTo(3)));
	}
	
	@Test
	public void setFilterExpressionsShouldSaveConfig() throws ConfigurationException {
		
		List<String> filters = new ArrayList<String>();
		filters.add("New String");
		
		settingsProvider.setFilterExpressions(filters);
		
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void setLastRunDateShouldAddNewPropertyToPropertiesConfiguration() throws ConfigurationException {
		
		DateTime dateTime = new DateTime(123456789);
		
		settingsProvider.setLastRunDate(dateTime);
		
		verify(propertiesConfiguration).setProperty(LAST_RUN, 123456789l);
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void getLastRunDateShouldGetLastRunMillisAndConvertToDateTimeThenReturn() {
		
		DateTime dateTime = new DateTime(123456789);
		
		when(propertiesConfiguration.getLong(eq(LAST_RUN), anyLong())).thenReturn(123456789l);
		
		assertThat(settingsProvider.getLastRunDate(), is(equalTo(dateTime)));
	}
	
	@Test
	public void getHostConfigShouldCompileMultipleObjectValuesFromConfigIntoHostConfigObject() {
		
		when(propertiesConfiguration.getString(HOST_NAME)).thenReturn("hostname");
		when(propertiesConfiguration.getString(HOST_USER)).thenReturn("a user");
		when(propertiesConfiguration.getString(HOST_PASSWORD)).thenReturn("a password");
		when(propertiesConfiguration.getInt(HOST_PORT)).thenReturn(80);
		when(propertiesConfiguration.getString(HOST_TYPE)).thenReturn("SFTP");
		when(propertiesConfiguration.getString(HOST_FILE_DIR)).thenReturn("remote/directory");
		
		HostConfig hostConfig = settingsProvider.getHost();
		
		verify(propertiesConfiguration).getString(HOST_NAME);
		verify(propertiesConfiguration).getString(HOST_USER);
		verify(propertiesConfiguration).getString(HOST_PASSWORD);
		verify(propertiesConfiguration).getString(HOST_TYPE);
		verify(propertiesConfiguration).getInt(HOST_PORT);
		verify(propertiesConfiguration).getString(HOST_FILE_DIR);
		
		assertThat(hostConfig.getHostname(), is(equalTo("hostname")));
		assertThat(hostConfig.getPassword(), is(equalTo("a password")));
		assertThat(hostConfig.getUsername(), is(equalTo("a user")));
		assertThat(hostConfig.getPort(), is(equalTo(80)));
		assertThat(hostConfig.getClientType(), is(equalTo(ClientType.SFTP)));
		assertThat(hostConfig.getFileDirectory(), is(equalTo("remote/directory")));
	}
	
	@Test
	public void setHostConfigShouldTakeHostConfigObjectAndWriteToPropertiesForEveryField() throws ConfigurationException {
		
		HostConfig config = new HostConfig();
		
		config.setClientType(ClientType.SFTP);
		config.setHostname("hostname");
		config.setPassword("password");
		config.setPort(80);
		config.setUsername("username");
		config.setFileDirectory("remote/directory");
		
		settingsProvider.setHost(config);
		
		verify(propertiesConfiguration).setProperty(HOST_NAME, "hostname");
		verify(propertiesConfiguration).setProperty(HOST_PASSWORD, "password");
		verify(propertiesConfiguration).setProperty(HOST_PORT, 80);
		verify(propertiesConfiguration).setProperty(HOST_TYPE, "SFTP");
		verify(propertiesConfiguration).setProperty(HOST_USER, "username");
		verify(propertiesConfiguration).setProperty(HOST_FILE_DIR, "remote/directory");
		
		verify(propertiesConfiguration).save();
	}
	
	@Test
	public void ifConfigFileDoesNotExistThenConstructorShouldCreateIt() {
		
		File file = new File(CONFIG_FILE);
		file.delete();
		
		new SettingsProvider("build/autoftp.conf");
		
		assertThat(new File(CONFIG_FILE).exists(), is(equalTo(true)));
	}

	private List<Object> initFilterList() {

		List<Object> list = new ArrayList<Object>();

		list.add("Filter 1");
		list.add("Filter 2");
		list.add("Filter 3");

		return list;
	}

}
