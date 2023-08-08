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
package org.eu.zajc.akiwrapper.core.exceptions;

import javax.annotation.Nonnull;

import org.eu.zajc.akiwrapper.core.entities.Server;
import org.eu.zajc.akiwrapper.core.entities.Server.*;

/**
 * An exception indicating that no {@link Server} could be found for the given
 * combination of {@link Language} and {@link GuessType}.
 *
 * @author Marko Zajc
 */
public class ServerNotFoundException extends AkinatorException {

	@SuppressWarnings("javadoc") // internal
	public ServerNotFoundException(@Nonnull String message) {
		super(message);
	}

}
