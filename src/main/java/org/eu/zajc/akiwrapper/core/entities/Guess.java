//SPDX-License-Identifier: GPL-3.0
/*
 * Akiwrapper, the Java API wrapper for Akinator
 * Copyright (C) 2017-2023 Marko Zajc
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.eu.zajc.akiwrapper.core.entities;

import java.net.URL;

import javax.annotation.*;

import org.eu.zajc.akiwrapper.*;
import org.eu.zajc.akiwrapper.core.entities.Server.GuessType;

/**
 * A representation of Akinator's guess. A guess may span different types of subject,
 * depending on what was set for the {@link GuessType} in the
 * {@link AkiwrapperBuilder} (default is {@link GuessType#CHARACTER}). A guess
 * consists of four parts - subject name, description (both localized), a URL to the
 * image of the subject, and the probability that the guess is correct. Note that
 * image URL and description are optional, and may be {@code null}. {@link Guess}es
 * implement {@link Comparable} and are by default sorted by probability - the lower
 * the index, the higher the probability.
 *
 * @author Marko Zajc
 */
public interface Guess extends Identifiable, Comparable<Guess> {

	/**
	 * Returns the name of the guessed subject. This is provided in the language that was
	 * specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return guessed characer's name.
	 */
	@Nonnull
	String getName();

	/**
	 * Returns the approximate probability that the answer is the one user has in mind
	 * (as a double).<br>
	 * The value ranges between 0 and 1.
	 *
	 * @return probability that this is the right answer.
	 */
	double getProbability();

	/**
	 * Returns the description of this subject. As a description is optional and thus not
	 * always present, this may be {@code null}. It is provided in the language that was
	 * specified using the {@link AkiwrapperBuilder}.
	 *
	 * @return description of the guessed subject.
	 */
	@Nullable
	String getDescription();

	/**
	 * Returns the URL to an image of this subject. As an image of the subject is
	 * optional and thus not always present, this may be {@code null}.
	 *
	 * @return URL to picture or null if no picture is attached
	 */
	@Nullable
	URL getImage();

	/**
	 * <b>Important:</b> Akinator for some reason flags certain perfectly SFW guesses as
	 * explicit. Akiwrapper's code for this reason doesn't rely on this parameter -
	 * {@link Akiwrapper#suggestGuess} will return guesses marked as explicit even when
	 * profanity filtering is on. Using this value to filter content is optional but not
	 * advised.
	 *
	 * @return whether or not the guess is explicit (often incorrect).
	 */
	boolean isExplicit();

}
