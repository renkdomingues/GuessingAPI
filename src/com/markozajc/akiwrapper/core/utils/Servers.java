package com.markozajc.akiwrapper.core.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.json.JSONObject;

import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.Route;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Server.Language;
import com.markozajc.akiwrapper.core.entities.ServerGroup;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerGroupImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.ServerImpl;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerGroupUnavailableException;

/**
 * A class containing various API server utilities.
 * 
 * @author Marko Zajc
 */
public class Servers {

	/**
	 * A list of known Akinator's API servers
	 */
	public static final Map<Language, ServerGroup> SERVER_GROUPS;

	static {

		Map<Language, ServerGroup> servers = new HashMap<>();

		// Arabic
		servers.put(Language.ARABIC, new ServerGroupImpl(Language.ARABIC, new Server[] {
				new ServerImpl("api-ar2.akinator.com", Language.ARABIC),
				new ServerImpl("api-ar3.akinator.com", Language.ARABIC),
		}));

		// Chinese
		servers.put(Language.CHINESE, new ServerGroupImpl(Language.CHINESE, new Server[] {
				new ServerImpl("api-cn1.akinator.com", Language.CHINESE),
				new ServerImpl("api-cn3.akinator.com", Language.CHINESE),
		}));

		// Dutch
		servers.put(Language.DUTCH, new ServerGroupImpl(Language.DUTCH, new Server[] {
				new ServerImpl("api-nl2.akinator.com", Language.DUTCH),
				new ServerImpl("api-nl3.akinator.com", Language.DUTCH),
		}));

		// English
		servers.put(Language.ENGLISH, new ServerGroupImpl(Language.ENGLISH, new Server[] {
				new ServerImpl("api-en1.akinator.com", Language.ENGLISH),
				new ServerImpl("api-en3.akinator.com", Language.ENGLISH),
				new ServerImpl("api-en4.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa1.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa3.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa4.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa5.akinator.com", Language.ENGLISH),
				new ServerImpl("api-usa6.akinator.com", Language.ENGLISH),
				new ServerImpl("api-us3.akinator.com", Language.ENGLISH),
				new ServerImpl("api-us4.akinator.com", Language.ENGLISH),
				new ServerImpl("ns623133.ovh.net:8014", Language.ENGLISH),
		}));

		// French
		servers.put(Language.FRENCH, new ServerGroupImpl(Language.FRENCH, new Server[] {
				new ServerImpl("api-obj-fr1.akinator.com", Language.FRENCH),
				new ServerImpl("api-obj-fr3.akinator.com", Language.FRENCH),
				new ServerImpl("ns623133.ovh.net:8030", Language.FRENCH),
		}));

		// German
		servers.put(Language.GERMAN, new ServerGroupImpl(Language.GERMAN, new Server[] {
				new ServerImpl("api-de3.akinator.com ", Language.GERMAN),
				new ServerImpl("ns623133.ovh.net:8005", Language.GERMAN),
		}));

		// Hindi
		servers.put(Language.HINDI, new ServerGroupImpl(Language.HINDI, new Server[] {
				new ServerImpl("api-in1.akinator.com", Language.HINDI),
				new ServerImpl("api-in2.akinator.com", Language.HINDI),
		}));

		// Hebrew
		servers.put(Language.HEBREW, new ServerGroupImpl(Language.HEBREW, new Server[] {
				new ServerImpl("ns623133.ovh.net:8006", Language.HEBREW),
		}));

		// Italian
		servers.put(Language.ITALIAN, new ServerGroupImpl(Language.ITALIAN, new Server[] {
				new ServerImpl("api-it2.akinator.com", Language.ITALIAN),
				new ServerImpl("api-it3.akinator.com", Language.ITALIAN),
		}));

		// Japanese
		servers.put(Language.JAPANESE, new ServerGroupImpl(Language.JAPANESE, new Server[] {
				new ServerImpl("api-jp2.akinator.com", Language.JAPANESE),
				new ServerImpl("api-jp3.akinator.com", Language.JAPANESE),
				new ServerImpl("ns623133.ovh.net:8012", Language.JAPANESE),
		}));

		// Korean
		servers.put(Language.KOREAN, new ServerGroupImpl(Language.KOREAN, new Server[] {
				new ServerImpl("api-kr1.akinator.com", Language.KOREAN),
				new ServerImpl("api-kr4.akinator.com", Language.KOREAN),
		}));

		// Polish
		servers.put(Language.POLISH, new ServerGroupImpl(Language.POLISH, new Server[] {
				new ServerImpl("api-pl1.akinator.com", Language.POLISH),
				new ServerImpl("api-pl3.akinator.com", Language.POLISH),
		}));

		// Portuguese
		servers.put(Language.PORTUGUESE, new ServerGroupImpl(Language.PORTUGUESE, new Server[] {
				new ServerImpl("api-pt3.akinator.com", Language.PORTUGUESE),
				new ServerImpl("api-pt4.akinator.com", Language.PORTUGUESE),
		}));

		// Russian
		servers.put(Language.RUSSIAN, new ServerGroupImpl(Language.RUSSIAN, new Server[] {
				new ServerImpl("api-ru1.akinator.com", Language.RUSSIAN),
				new ServerImpl("api-ru3.akinator.com", Language.RUSSIAN),
				new ServerImpl("api-ru4.akinator.com", Language.RUSSIAN),
		}));

		// Spanish
		servers.put(Language.SPANISH, new ServerGroupImpl(Language.SPANISH, new Server[] {
				new ServerImpl("api-es3.akinator.com", Language.SPANISH),
				new ServerImpl("api-es4.akinator.com", Language.SPANISH),
				new ServerImpl("ns623133.ovh.net:8013", Language.SPANISH),
		}));

		// Turkish
		servers.put(Language.TURKISH, new ServerGroupImpl(Language.TURKISH, new Server[] {
				new ServerImpl("api-tr1.akinator.com", Language.TURKISH),
				new ServerImpl("api-tr3.akinator.com", Language.TURKISH),
		}));

		SERVER_GROUPS = Collections.unmodifiableMap(servers);
	}

	/**
	 * Checks if an API server is online.
	 * 
	 * @param server
	 *            a server to check
	 * @return true if a new session can be created on the provided server, false if not
	 */
	public static boolean isUp(Server server) {
		try {
			JSONObject question = Route.NEW_SESSION
					.getRequest(server.getBaseUrl(), AkiwrapperBuilder.DEFAULT_FILTER_PROFANITY,
							AkiwrapperBuilder.DEFAULT_NAME)
					.getJSON();

			if (new StatusImpl(question).getLevel().equals(Level.OK)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Searches for an available server for the given localization language. If there is
	 * no available server, this will throw a {@link ServerGroupUnavailableException}.
	 * 
	 * @param localization
	 *            language of the server to search for
	 * @return the first server available for that language
	 * @throws UnsupportedOperationException
	 *             if language {@code localization} is not supported by the Akinator's
	 *             API
	 * @throws ServerGroupUnavailableException
	 *             if there are no available servers for language {@code localization}
	 */
	@Nonnull
	public static Server getFirstAvailableServer(@Nonnull Language localization)
			throws UnsupportedOperationException, ServerGroupUnavailableException {
		ServerGroup sg = SERVER_GROUPS.get(localization);
		if (sg == null)
			throw new UnsupportedOperationException(
					"Language " + localization.toString() + " is not supported by the Akinator's API.");

		Server result = sg.getFirstAvailableServer();
		if (result == null)
			throw new ServerGroupUnavailableException(sg);

		return result;
	}

}
