package com.markozajc.akiwrapper;

import static com.markozajc.akiwrapper.core.entities.Server.GuessType.CHARACTER;
import static com.markozajc.akiwrapper.core.entities.Server.Language.ENGLISH;
import static com.markozajc.akiwrapper.core.utils.Servers.findServers;

import javax.annotation.*;

import org.slf4j.*;

import com.markozajc.akiwrapper.core.entities.*;
import com.markozajc.akiwrapper.core.entities.Server.*;
import com.markozajc.akiwrapper.core.exceptions.*;
import com.markozajc.akiwrapper.core.impl.AkiwrapperImpl;
import com.markozajc.akiwrapper.core.utils.*;

import kong.unirest.UnirestInstance;

/**
 * A class used to build an {@link Akiwrapper} object. It allows you to set various
 * values before building it in a method chaining fashion. Note that
 * {@link Language}, {@link GuessType}, and {@link Server} configuration are
 * connected - {@link Language} and {@link GuessType} are used to find a suitable
 * {@link Server}, but they will only be used if a {@link Server} is not manually
 * set. It is not recommended to set the {@link Server} manually (unless for
 * debugging purposes or as some kind of workaround where Akiwrapper's server finder
 * fails) as Akiwrapper already does its best to find the most suitable one.
 *
 * @author Marko Zajc
 */
public class AkiwrapperBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(AkiwrapperBuilder.class);

	@Nullable
	private UnirestInstance unirest;
	@Nullable
	private Server server;
	private boolean filterProfanity;
	@Nonnull
	private Language language;
	@Nonnull
	private GuessType guessType;

	/**
	 * The default profanity filter preference for new {@link Akiwrapper} instances.
	 */
	public static final boolean DEFAULT_FILTER_PROFANITY = false;

	/**
	 * The default {@link Language} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final Language DEFAULT_LOCALIZATION = ENGLISH;

	/**
	 * The default {@link GuessType} for new {@link Akiwrapper} instances.
	 */
	@Nonnull
	public static final GuessType DEFAULT_GUESS_TYPE = CHARACTER;

	private AkiwrapperBuilder(@Nullable UnirestInstance unirest, @Nullable Server server, boolean filterProfanity,
							  @Nonnull Language language, @Nonnull GuessType guessType) {
		this.unirest = unirest;
		this.server = server;
		this.filterProfanity = filterProfanity;
		this.language = language;
		this.guessType = guessType;
	}

	/**
	 * Creates a new AkiwrapperBuilder object.
	 */
	public AkiwrapperBuilder() {
		this(null, null, DEFAULT_FILTER_PROFANITY, DEFAULT_LOCALIZATION, DEFAULT_GUESS_TYPE);
	}

	// TODO javadoc
	@Nonnull
	public AkiwrapperBuilder setUnirestInstance(@Nullable UnirestInstance unirest) {
		this.unirest = unirest;
		return this;
	}

	// TODO javadoc
	@Nullable
	public UnirestInstance getUnirestInstance() {
		return this.unirest;
	}

	/**
	 * Sets the {@link Server} or (recommended) a {@link ServerList}. It is not
	 * recommended to set the {@link Server} manually (unless for debugging purposes or
	 * as some kind of workaround where Akiwrapper's server finder fails) as Akiwrapper
	 * already does its best to find the most suitable one. <br>
	 * <b>Caution!</b> Setting the server to a non-null value overwrites the
	 * {@link Language} and the {@link GuessType} with the given {@link Server}'s values.
	 *
	 * @param server
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getServer()
	 * @see Servers#findServers(UnirestInstance, Language, GuessType)
	 */
	@Nonnull
	public AkiwrapperBuilder setServer(@Nullable Server server) {
		this.server = server;
		if (server != null) {
			this.language = server.getLanguage();
			this.guessType = server.getGuessType();
		}
		return this;
	}

	/**
	 * Returns the {@link Server} that requests will be sent to. Might also return a
	 * {@link ServerList} (which extends {@link Server}).
	 *
	 * @return server.
	 */
	@Nullable
	public Server getServer() {
		return this.server;
	}

	/**
	 * Sets the "filter profanity" mode.
	 *
	 * @param filterProfanity
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #doesFilterProfanity()
	 */
	@Nonnull
	public AkiwrapperBuilder setFilterProfanity(boolean filterProfanity) {
		this.filterProfanity = filterProfanity;
		return this;
	}

	/**
	 * Returns the profanity filter preference. Profanity filtering is done by Akinator
	 * and not by Akiwrapper.
	 *
	 * @return profanity filter preference.
	 */
	public boolean doesFilterProfanity() {
		return this.filterProfanity;
	}

	/**
	 * Sets the {@link Language}.<br>
	 * <b>Caution!</b> Setting the {@link Language} will set the {@link Server} to
	 * {@code null} (meaning it will be automatically selected).
	 *
	 * @param language
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public AkiwrapperBuilder setLanguage(@Nonnull Language language) {
		this.language = language;
		this.server = null;
		return this;
	}

	/**
	 * Returns the {@link Language} preference. {@link Language} impacts what language
	 * {@link Question}s and {@link Guess}es are in.<br>
	 * {@link #getGuessType()} and {@link #getLanguage()} decide what {@link Server} will
	 * be used if it's not set manually.
	 *
	 * @return language preference.
	 */
	@Nonnull
	public Language getLanguage() {
		return this.language;
	}

	/**
	 * Sets the {@link GuessType}.<br>
	 * <b>Caution!</b> Setting the {@link Language} will set the {@link Server} to
	 * {@code null} (meaning it will be automatically selected).
	 *
	 * @param guessType
	 *
	 * @return current instance, used for chaining
	 *
	 * @see #getLanguage()
	 */
	@Nonnull
	public AkiwrapperBuilder setGuessType(@Nonnull GuessType guessType) {
		this.guessType = guessType;
		this.server = null;
		return this;
	}

	/**
	 * Returns the {@link GuessType} preference. {@link GuessType} impacts what kind of
	 * subject {@link Question}s and {@link Guess}es are about.<br>
	 * {@link #getGuessType()} and {@link #getLanguage()} decide what {@link Server} will
	 * be used if it's not set manually.
	 *
	 * @return guess type preference.
	 */
	@Nonnull
	public GuessType getGuessType() {
		return this.guessType;
	}

	/**
	 * Creates a new {@link Akiwrapper} instance from your preferences. If no
	 * {@link UnirestInstance} was set (with
	 * {@link #setUnirestInstance(UnirestInstance)}), a singleton instance will be
	 * acquired from {@link UnirestUtils#getInstance()}. This instance must be shut down
	 * after you're done using Akiwrapper with {@link UnirestUtils#shutdownInstance()}.
	 * If no server was set (with {@link #setServer(Server)}), Akiwrapper will find one
	 * based on {@link #getLanguage()} and {@link #getGuessType()} for you.
	 *
	 * @return a new {@link Akiwrapper} instance that will use all set preferences
	 *
	 * @throws ServerNotFoundException
	 *             if no server with that {@link Language} and {@link GuessType} is
	 *             available.
	 */
	@Nonnull
	@SuppressWarnings("resource")
	public Akiwrapper build() throws ServerNotFoundException {
		UnirestInstance unirest = this.unirest != null ? this.unirest : UnirestUtils.getInstance();

		var server = this.server != null ? this.server : findServers(unirest, this.getLanguage(), this.getGuessType());
		if (server instanceof ServerList) {
			ServerList serverList = (ServerList) server;
			int count = serverList.getRemainingSize() + 1;
			do {
				LOG.debug("Using server {} out of {} from the list.", count - serverList.getRemainingSize(), count);
				try {
					return new AkiwrapperImpl(unirest, server, this.filterProfanity);

				} catch (ServerUnavailableException e) {
					LOG.debug("Server seems to be down.");

				} catch (RuntimeException e) {
					LOG.warn("Failed to construct an instance, trying the next available server", e);
				}
			} while (serverList.next());
			throw new ServerUnavailableException("KO - NO SERVER AVAILABLE");
		} else {
			LOG.debug("Given Server is not a ServerList, only attempting to build once.");
			return new AkiwrapperImpl(unirest, server, this.filterProfanity);
		}
	}

}
