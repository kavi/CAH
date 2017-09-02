package dk.javacode;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import dk.javacode.cah.configuration.ConfigValues;
import dk.javacode.cah.rest.CahWebServiceApp;

public class WebServiceAdHocTest {

	private static Logger log = Logger.getLogger(WebServiceAdHocTest.class);
	// protected static final String ROOT_URI = "resources";
	private static final String[] confFiles = new String[] { "test-data/configuration.properties" };

	public static void main(String[] args) throws Exception {
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getLogger("dk.javacode.cah").setLevel(Level.INFO);
		Logger.getLogger("dk.javacode.srsm").setLevel(Level.DEBUG);
		
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		
		ConsoleAppender console = new ConsoleAppender(); // create appender
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		
		FileAppender file = new FileAppender(); // create appender
		file.setFile("webservice.log");
		file.setLayout(new PatternLayout(PATTERN));
		file.setThreshold(Level.DEBUG);
		file.activateOptions();

		Logger.getRootLogger().addAppender(file);
		Logger.getRootLogger().addAppender(console);

		log.info("Starting...");
		String confFileName = null;
		for (String cf : confFiles) {
			if (new File(cf).exists()) {
				log.info("Found configuration file: " + cf);
				if (new File(cf).canRead()) {
					log.info("Selecting configuration file: " + cf);
					confFileName = cf;
				} else {
					log.info(cf + " not readable");
				}
			}
		}
		if (args.length > 0) {
			confFileName = args[0];
		}
		ConfigValues config = ConfigValues.load(confFileName);
		CahWebServiceApp app = new CahWebServiceApp();
		app.setConfigValues(config);

		// Set listen port
		app.runService();
//		GameDao gameDao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
//		UserDao userDao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
//		new MySqlGameDaoTest(gameDao, userDao).testCreateGame();

		
	}

	@Test // I'm here to prevent JUnit from reporting an error for "No Tests found"
	public void notest() {
		// DO NOTHING 
	}
}
