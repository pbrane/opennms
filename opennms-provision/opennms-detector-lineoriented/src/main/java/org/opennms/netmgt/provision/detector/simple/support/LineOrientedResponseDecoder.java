/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.opennms.netmgt.provision.detector.simple.support;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import org.opennms.netmgt.provision.detector.simple.response.LineOrientedResponse;

import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * <p>LineOrientedResponseDecoder class.</p>
 *
 * @author Seth
 */
public class LineOrientedResponseDecoder extends MessageToMessageDecoder<Object> {

    /**
     * This method decodes {@link String} objects into {@link LineOrientedResponse} instances
     * that contain the byte representation of the response.
     */
    @Override
    public void decode(final ChannelHandlerContext ctx, final Object msg, final List<Object> messages) throws Exception {
        messages.add(new LineOrientedResponse((String)msg));
    }
}
