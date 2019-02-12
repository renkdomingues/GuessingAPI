package com.markozajc.akiwrapper.core;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.markozajc.akiwrapper.core.entities.AkiwrapperMetadata;
import com.markozajc.akiwrapper.core.entities.Server;
import com.markozajc.akiwrapper.core.entities.Status;
import com.markozajc.akiwrapper.core.entities.Status.Level;
import com.markozajc.akiwrapper.core.entities.impl.immutable.StatusImpl;
import com.markozajc.akiwrapper.core.exceptions.ServerUnavailableException;
import com.markozajc.akiwrapper.core.exceptions.StatusException;
import com.markozajc.akiwrapper.core.utils.HTTPUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A class defining various API endpoints (routes).
 *
 * @author Marko Zajc
 */
public class Route {

	private static final String BASE_AKINATOR_URL = "https://en.akinator.com";
	// The base Akinator URL, used for scraping various elements (and not for the API
	// calls)

	private static String apiKey;

	/**
	 * @return the current API key scraped from Akinator. A new key can be scraped using
	 *         {@link #scrapApiKey()}
	 */
	public static String getApiKey() {
		return apiKey;
	}

	private static final Pattern API_KEY_PATTERN = Pattern.compile("(var uid_ext_session = ')(.*)(')");

	static {
		try {
			scrapApiKey();
		} catch (IOException e) {
			apiKey = "";
			Logger.getLogger("Akiwrapper")
					.severe("Couldn't scrape the API key. Code that uses Akiwrapper might not work correctly. "
							+ "Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.");
		}
	}

	/**
	 * Scraps the API key from Akinator's website and stores it for later use.
	 *
	 * @throws IOException
	 */
	public static void scrapApiKey() throws IOException {
		Matcher matcher = API_KEY_PATTERN.matcher(
			new String(HTTPUtils.read(new URL(BASE_AKINATOR_URL + "/game").openConnection()), StandardCharsets.UTF_8));
		if (matcher.find()) {
			apiKey = matcher.group(2);

		} else {
			throw new IOException(
					"Couldn't scrap the API key! Please consider opening a new ticket at https://github.com/markozajc/Akiwrapper/issues.");
		}
	}

	/**
	 * Whether to run status checks on {@link Request#getJSON()} by default. Setting this
	 * to false may result in unpredicted exceptions! <b>You usually don't need to alter
	 * this value</b>
	 */
	@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
	public static boolean defaultRunChecks = true; // NOSONAR

	/**
	 * Creates a new session for further gameplay. Parameters:
	 * <ol>
	 * <li>Player's name</li>
	 * </ol>
	 */
	public static final Route NEW_SESSION = new Route(
			"new_session?partner=5&player=%s&constraint=ETAT%%3C%%3E%%27AV%%27&frontaddr=NDYuMTA1LjExMC40NQ%%3D%%3D&uid_ext_session={API_KEY}",
			"&soft_constraint=ETAT=%27EN%27&question_filter=cat=1", 1);

	/**
	 * Answers a question. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * <li>Answer's ID</li>
	 * </ol>
	 */
	public static final Route ANSWER = new Route("answer?session=%s&signature=%s&step=%s&answer=%s",
			"&question_filter=cat=1", 4);

	/**
	 * Cancels (undoes) an answer. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route CANCEL_ANSWER = new Route("cancel_answer?session=%s&signature=%s&step=%s&answer=-1",
			"&question_filter=cat=1", 3);

	/**
	 * Lists all available guesses. Parameters:
	 * <ol>
	 * <li>Session's ID</li>
	 * <li>Session's signature</li>
	 * <li>Current step</li>
	 * </ol>
	 */
	public static final Route LIST = new Route("list?session=%s&signature=%s&mode_question=0&step=%s", 3);

	/**
	 * Tests whether a response is a successful or a failed one.
	 *
	 * @param response
	 *            the response to test
	 * @param server
	 *            the {@link Server} to include in a {@link ServerUnavailableException},
	 *            if it occurs
	 * @throws ServerUnavailableException
	 *             throws if the status is equal to {@link Level#ERROR} and the error
	 *             message hints that the server is down
	 * @throws StatusException
	 *             thrown if the status is equal to {@link Level#ERROR}
	 */
	public static void testResponse(JSONObject response, Server server) {
		Status compl = new StatusImpl(response);
		if (compl.getLevel().equals(Level.ERROR)) {
			if (compl.getReason().equalsIgnoreCase("server down")) {
				throw new ServerUnavailableException(server);
			}

			throw new StatusException(compl);
		}
	}

	private final String path;
	private final String filteredAppendix;
	private String userAgent;

	private final int parametersQuantity;

	private Route(String path, int parameters) {
		this(path, "", parameters);
	}

	private Route(String path, String filteredAppendix, int parameters) {
		this(path, filteredAppendix, parameters, AkiwrapperMetadata.DEFAULT_USER_AGENT);
	}

	private Route(String path, String filteredAppendix, int parameters, String userAgent) {
		this.path = path;
		this.filteredAppendix = filteredAppendix;
		this.parametersQuantity = parameters;
		this.userAgent = userAgent;
	}

	/**
	 * Creates a request for this route that can later be called and converted into a
	 * {@link JSONObject}.
	 *
	 * @param baseUrl
	 *            base (API's) URL
	 * @param filterProfanity
	 *            whether to filter profanity. Akinator's website will automatically
	 *            enable that if you choose an age below 16
	 * @param parameters
	 *            parameters to pass to the route (parameters are specified in that
	 *            Route's JavaDoc)
	 * @return a callable request
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if you have passed too little parameters
	 */
	public Request getRequest(String baseUrl, boolean filterProfanity, String... parameters) throws IOException {
		if (parameters.length < this.parametersQuantity)
			throw new IllegalArgumentException(
					"Insufficient parameters; Expected " + this.parametersQuantity + ", got " + parameters.length);

		String[] encodedParams = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			encodedParams[i] = URLEncoder.encode(parameters[i], "UTF-8");

		return new Request(
				new URL(baseUrl + String.format(this.path.replace("{API_KEY}", apiKey), (Object[]) encodedParams)
						+ (filterProfanity ? this.filteredAppendix : "")),
				this.userAgent);
	}

	/**
	 * Sets the user-agent that will be used in requests for this route. If no user-agent
	 * is specified, {@link AkiwrapperMetadata#DEFAULT_USER_AGENT} will be used.
	 *
	 * @param userAgent
	 * @return self, useful for chaining
	 */
	public Route setUserAgent(String userAgent) {
		this.userAgent = userAgent;

		return this;
	}

	/**
	 * @return route's path (unformatted)
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return minimal quantity of parameters you would have to pass to
	 *         {@link #getRequest(String, boolean, String...)}
	 */
	public int getParametersQuantity() {
		return this.parametersQuantity;
	}

	/**
	 * @return user-agent for this route
	 * @see #setUserAgent(String)
	 */
	public String getClientBuilder() {
		return this.userAgent;
	}

	/**
	 * A callable request.
	 *
	 * @author Marko Zajc
	 */
	public static class Request {

		/**
		 * The connection timeout in milliseconds. Set this to something lower if you're
		 * going to send a lot of request to not-confirmed servers. Set this to {@code -1} to
		 * use {@link URLConnection}'s default timeout setting. <b>You usually don't need to
		 * alter this value</b>
		 */
		@SuppressFBWarnings({
				"MS_CANNOT_BE_FINAL", "MS_SHOULD_BE_FINAL"
		})
		public static int connectionTimeout = 2500; // NOSONAR

		URLConnection connection;
		private byte[] bytes = null;

		Request(URL url, String userAgent) throws IOException {
			this.connection = url.openConnection();
			if (connectionTimeout != -1)
				this.connection.setConnectTimeout(connectionTimeout);

			this.connection.setRequestProperty("User-Agent", userAgent);
		}

		/**
		 * Reads content of the request's URL into an array of bytes.
		 *
		 * @return content as a byte array
		 * @throws IOException
		 * @see String#String(byte[], String)
		 */
		public byte[] read() throws IOException {
			if (this.bytes == null) {
				byte[] newBytes = HTTPUtils.read(this.connection);
				this.bytes = newBytes;
			}

			return this.bytes.clone();
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 *
		 * @return route's content
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             in case the server has went down (very unlikely to ever happen)
		 */
		public JSONObject getJSON() throws IOException {
			return getJSON(defaultRunChecks);
		}

		/**
		 * Requests the server and returns the route's content as a {@link JSONObject}.
		 *
		 * @param runChecks
		 *            whether to run checks for error status codes.
		 * @return route's content
		 * @throws IOException
		 * @throws ServerUnavailableException
		 *             thrown if the server has gone down
		 * @throws StatusException
		 *             thrown if the server returns an error response
		 *
		 */
		public JSONObject getJSON(boolean runChecks) throws IOException {
			JSONObject result = new JSONObject(new String(read(), StandardCharsets.UTF_8));

			if (runChecks)
				testResponse(result, new Server() {

					@Override
					public Language getLocalization() {
						return null; // testResponse() does not need to know the language
					}

					@Override
					public String getHost() {
						return Request.this.connection.getURL().getHost() + ":"
								+ Request.this.connection.getURL().getPort();
					}

				});

			return result;
		}

	}
}