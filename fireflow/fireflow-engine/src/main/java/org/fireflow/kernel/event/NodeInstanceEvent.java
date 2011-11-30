/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.kernel.event;

import java.util.EventObject;

import org.fireflow.kernel.IToken;

/**
 * node监听器
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class NodeInstanceEvent extends EventObject {
	public static final int NODEINSTANCE_TOKEN_ENTERED = 1;
	public static final int NODEINSTANCE_FIRED = 2;
	public static final int NODEINSTANCE_COMPLETED = 3;
	public static final int NODEINSTANCE_LEAVING = 4;
        
	int eventType = -1;
	private IToken token = null;

	private NodeInstanceEvent(){
		super(null);
	}
	public NodeInstanceEvent(Object source){
		super(source);
	}
	
	public IToken getToken() {
		return token;
	}

	public void setToken(IToken tk) {
		this.token = tk;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	
	
}
