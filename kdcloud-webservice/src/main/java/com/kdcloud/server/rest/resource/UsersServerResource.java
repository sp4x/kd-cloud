/**
 * Copyright (C) 2012 Vincenzo Pirrone
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.kdcloud.server.rest.resource;

import java.util.LinkedList;
import java.util.List;

import org.restlet.Application;
import org.restlet.representation.Representation;

import com.kdcloud.lib.domain.UserIndex;
import com.kdcloud.lib.rest.api.UsersResource;
import com.kdcloud.server.entity.DataTable;
import com.kdcloud.server.entity.Group;

public class UsersServerResource extends KDServerResource implements
		UsersResource {
	
	
	private Group group;
	
	public UsersServerResource() {
		super();
	}

	public UsersServerResource(Application application, Group group) {
		super(application, null);
		this.group = group;
	}

	
	@Override
	public Representation handle() {
		String groupName = getResourceIdentifier();
		group = groupDao.findByName(groupName);
		if (group == null)
			return notFound();
		return super.handle();
	}

	@Override
	public UserIndex getSubscribedUsers() {
		List<String> names = new LinkedList<String>();
		for (DataTable t : group.getData()) {
			names.add(t.getName());
		}
		return new UserIndex(names);
	}

}
