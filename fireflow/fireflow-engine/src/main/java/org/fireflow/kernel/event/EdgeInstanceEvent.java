/**
 * Copyright 2007-2008 非也
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
 * edge监听器
 *
 * @author chennieyun
 */
@SuppressWarnings("serial")
public class EdgeInstanceEvent extends EventObject {

    public static final int ON_TAKING_THE_TOKEN = 1;
    int eventType = -1;
    private IToken token = null;

	private EdgeInstanceEvent() {
        super(null);
    }

    public EdgeInstanceEvent(Object source) {
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
