/**
 * Copyright (C) 2012, Vincenzo Pirrone <pirrone.v@gmail.com>

 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 */
package com.kdcloud.server.rest.resource;

import java.util.Arrays;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.kdcloud.lib.domain.GroupSpecification;
import com.kdcloud.lib.rest.api.GroupResource;
import com.kdcloud.server.entity.Group;

public class GroupServerResource extends BasicServerResource<Group> implements
		GroupResource {

	@Override
	public void editGroup(Representation rep) {
		createOrUpdate(rep);
	}

	@Override
	public GroupSpecification getSpecification() {
		Group group = read();
		GroupSpecification spec = new GroupSpecification(group.getMetadata(), group.getInputSpecification());
		if (group.getOwner().equals(user))
			spec.setInvitationMessage(group.getInvitationMessage());
		return spec;
	}

	@Override
	public Group find() {
		return getEntityMapper().findByName(Group.class, getResourceIdentifier());
	}

	@Override
	public void save(Group e) {
		getEntityMapper().save(e);
	}

	@Override
	public void delete(Group e) {
		e.getData().clear();
		getEntityMapper().delete(e);
	}

	@Override
	public Group create() {
		return new Group(getResourceIdentifier(), user);
	}

	@Override
	public void update(Group group, Representation rep) {
		if (!user.isOwner(group))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		if (rep != null && !rep.isEmpty()) {
			group.setOwner(user);
			GroupSpecification spec = unmarshal(GroupSpecification.class, rep);
			group.setInputSpecification(spec.getDataSpecification());
			group.getMetadata().update(spec.getMetadata());
			group.setInvitationMessage(spec.getInvitationMessage());
		}

	}

	@Override
	public void deleteGroup() {
		super.remove();
	}

	@Override
	public void setProperties(Form form) {
		Group group = read();
		if (!user.isOwner(group))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

		String values = form.getValues(Group.PROPERTY_ENROLLED);
		if (values != null) {
			String[] enrolled = values.split(",");
			group.getEnrolled(user).addAll(Arrays.asList(enrolled));
		}

		values = form.getValues(Group.PROPERTY_MEMBERS);
		if (values != null) {
			String[] members = values.split(",");
			group.getMembers(user).addAll(Arrays.asList(members));
		}

		save(group);
	}

}
