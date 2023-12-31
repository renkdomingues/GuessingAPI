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

import javax.annotation.Nonnull;

/**
 * A representation of an object with a numeric identifier. Some objects in the API
 * have an ID appended to them.
 *
 * @author Marko Zajc
 */
public interface Identifiable {

	/**
	 * @return ID of that object. Each object has an unique ID
	 */
	@Nonnull
	String getId();

	/**
	 * @return ID as a long
	 *
	 * @see #getId()
	 */
	default long getIdLong() {
		return Long.parseLong(getId());
	}

}
