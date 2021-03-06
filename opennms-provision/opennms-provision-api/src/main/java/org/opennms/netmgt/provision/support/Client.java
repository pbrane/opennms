/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.provision.support;

import java.io.IOException;
import java.net.InetAddress;

/**
 * <p>Client interface.</p>
 *
 * @author brozow
 * @version $Id: $
 */
public interface Client<Request, Response> {
    
    /**
     * <p>connect</p>
     *
     * @param address a {@link java.net.InetAddress} object.
     * @param port a int.
     * @param timeout a int.
     * @param <Request> a Request object.
     * @param <Response> a Response object.
     * @throws java.io.IOException if any.
     * @throws java.lang.Exception if any.
     */
    public void connect(InetAddress address, int port, int timeout) throws IOException, Exception;
    
    /**
     * <p>receiveBanner</p>
     *
     * @return a Response object.
     * @throws java.io.IOException if any.
     * @throws java.lang.Exception if any.
     */
    Response receiveBanner() throws IOException, Exception;
    
    /**
     * <p>sendRequest</p>
     *
     * @param request a Request object.
     * @return a Response object.
     * @throws java.io.IOException if any.
     * @throws java.lang.Exception if any.
     */
    Response sendRequest(Request request) throws IOException, Exception; 
    
    /**
     * <p>close</p>
     */
    public void close();

}
