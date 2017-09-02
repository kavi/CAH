package dk.javacode.cah.rest;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;

import dk.javacode.cah.chat.ChatFileStore;
import dk.javacode.cah.chat.ChatResource;
import dk.javacode.cah.configuration.ConfigValues;
import dk.javacode.cah.configuration.DbConf;
import dk.javacode.cah.database.connection.DbConnectionHandler;
import dk.javacode.cah.rest.admin.GameResource;
import dk.javacode.cah.rest.admin.GameSingleResource;
import dk.javacode.cah.rest.admin.PdfResource;
import dk.javacode.cah.rest.admin.UserResource;
import dk.javacode.cah.rest.game.ActiveCardGameResource;
import dk.javacode.cah.rest.game.GameScoreboardResource;
import dk.javacode.cah.rest.game.JoinGameActionResource;
import dk.javacode.cah.rest.game.NextRoundActionResource;
import dk.javacode.cah.rest.game.PlayWhiteActionResource;
import dk.javacode.cah.rest.game.PlayerGameResource;
import dk.javacode.cah.rest.game.StartGameActionResource;
import dk.javacode.cah.rest.game.UserStartedGamesResource;
import dk.javacode.cah.rest.game.VoteActionResource;
import dk.javacode.cah.util.ImageLoader;

public class CahWebServiceApp {

	private static Logger log = Logger.getLogger(CahWebServiceApp.class);
	// protected static final String ROOT_URI = "resources";
	private static final String[] confFiles = new String[] { "/etc/cards-against-humanity/configuration.properties" };

	private ConfigValues config;

	public static void main(String[] args) throws Exception {
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
		CahWebServiceApp app = new CahWebServiceApp();
		ConfigValues conf = ConfigValues.load(confFileName);
		log.info("Loaded config from: " + confFileName);
		app.setConfigValues(conf);
		app.runService();
	}

	public void runService() throws Exception {
		Component component = new Component();

		// Set listen port
		component.getServers().add(Protocol.HTTP, config.getInteger("rest.port", 8080));

//		component.getDefaultHost().attach("/static/", new FileApplication(StaticFolderResource.ROOT));

		// Chat service
		component.getDefaultHost().attach("/chat/{game_id}", ChatResource.class);
		
		
		// Game queries
		component.getDefaultHost().attach("/player/game/{id}/activecard", ActiveCardGameResource.class);
		component.getDefaultHost().attach("/player/game/{id}/player", PlayerGameResource.class);
		component.getDefaultHost().attach("/player/game/{id}/scoreboard", GameScoreboardResource.class);

		//
		component.getDefaultHost().attach("/user/games/started", UserStartedGamesResource.class);

		component.getDefaultHost().attach("/user", UserResource.class);

		component.getDefaultHost().attach("/action/player/game/{id}/nextround", NextRoundActionResource.class);
		component.getDefaultHost().attach("/action/player/game/{id}/vote", VoteActionResource.class);
		component.getDefaultHost().attach("/action/player/game/{id}/play-white", PlayWhiteActionResource.class);

		component.getDefaultHost().attach("/action/game/{id}/start", StartGameActionResource.class);
		component.getDefaultHost().attach("/action/game/{id}/join", JoinGameActionResource.class);

		// Management stuff (game create, list etc)
		component.getDefaultHost().attach("/game/{id}", GameSingleResource.class); // Get
																					// game
																					// details
		component.getDefaultHost().attach("/game", GameResource.class); // List
																		// games+create
																		// game

		// Admin stuff
		component.getDefaultHost().attach("/admin/pdf", PdfResource.class);
//		component.getDefaultHost().attach("/admin/white-card/{id}", WhiteCardSingleResource.class);
		component.getDefaultHost().attach("/admin/deck/{deck_id}/white-cards", WhiteCardResource.class);
//		component.getDefaultHost().attach("/admin/black-card/{id}", BlackCardSingleResource.class);
		component.getDefaultHost().attach("/admin/deck/{deck_id}/black-cards", BlackCardResource.class);
		
		// New Admin Stuff
		component.getDefaultHost().attach("/admin/languages", LanguageResource.class);
		component.getDefaultHost().attach("/public/language/{language_id}/decks", PublicDeckResource.class);
		component.getDefaultHost().attach("/admin/language/{language_id}/decks", DeckResource.class);
		//component.getDefaultHost().attach("/admin/language/{language_id}", LanguageSingleResource.class);
		component.getDefaultHost().attach("/admin/deck/{id}", DeckSingleResource.class);
		component.getDefaultHost().attach("/admin/white-card/{id}", WhiteCardSingleResource.class);
		component.getDefaultHost().attach("/admin/black-card/{id}", BlackCardSingleResource.class);
		

		// component.getDefaultHost().attach("/static",
		// DirectoryServerResource.class);

		TemplateRoute route = component.getDefaultHost().attach("/cah/{file}", StaticFolderResource.class);
		route.getTemplate().getVariables().put("file", new Variable(Variable.TYPE_URI_PATH));

		// Now, let's start the component!
		// Note that the HTTP server connector is also automatically started.
		component.start();
	}

	public void setConfigValues(ConfigValues p) {
		config = p;

		// Set Database configuration
		DbConf conf = DbConf.load(p);
		log.info("Loaded database configuration: " + conf);
		DbConnectionHandler.conf = conf;

		ImageLoader.ROOT_FOLDER = p.getString("image.folder", "resources/images");
		ChatFileStore.ROOT_FOLDER = p.getString("filestore.folder");

		// Set static folder resource root configuration.
		StaticFolderResource.ROOT = p.getString("rest.static.folder", "resources/web");
	}

//	public class FileApplication extends Application {
//		// use file://[your path]
//		// on windows, make sure that you add enough slashes at the beginning,
//		// e.g. file:///C:/[your path]
//		public String rootUri = "file:///[path to your restlet]/docs/api/";
//
//		public FileApplication(String root) {
//			super();
//			try {
//				rootUri = "file://" + new File(root).getCanonicalPath();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		// Create an inbound root Restlet that will receive all incoming calls
//		@Override
//		public Restlet createInboundRoot() {
//			// return the directory of local resources
//			// an instance of Directory is used as initial application Restlet
//			return new Directory(getContext(), rootUri);
//		}
//	}
}
