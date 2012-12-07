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
package com.kdcloud.engine.embedded;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="workflow")
public class WorkflowDescription {
	
	@XmlElement(name="node")
	List<NodeDescription> nodes = new LinkedList<NodeDescription>();
	
	public Node[] getInstance(NodeLoader nodeLoader) throws IOException {
		Node[] workflow = new Node[nodes.size()];
		int i = 0;
		for (NodeDescription factory : nodes) {
			workflow[i] = factory.create(nodeLoader);
			i++;
		}
		return workflow;
	}
	

}
