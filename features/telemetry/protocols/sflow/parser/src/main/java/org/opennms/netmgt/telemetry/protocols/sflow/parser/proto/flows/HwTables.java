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
package org.opennms.netmgt.telemetry.protocols.sflow.parser.proto.flows;

import org.bson.BsonWriter;
import org.opennms.netmgt.telemetry.listeners.utils.BufferUtils;
import org.opennms.netmgt.telemetry.protocols.sflow.parser.SampleDatagramEnrichment;
import org.opennms.netmgt.telemetry.protocols.sflow.parser.InvalidPacketException;

import com.google.common.base.MoreObjects;

import io.netty.buffer.ByteBuf;

// struct hw_tables {
//   unsigned int host_entries;
//   unsigned int host_entries_max;
//   unsigned int ipv4_entries;
//   unsigned int ipv4_entries_max;
//   unsigned int ipv6_entries;
//   unsigned int ipv6_entries_max;
//   unsigned int ipv4_ipv6_entries;
//   unsigned int ipv6_ipv6_entries_max;
//   unsigned int long_ipv6_entries;
//   unsigned int long_ipv6_entries_max;
//   unsigned int total_routes;
//   unsigned int total_routes_max;
//   unsigned int ecmp_nexthops;
//   unsigned int ecmp_nexthops_max;
//   unsigned int mac_entries;
//   unsigned int mac_entries_max;
//   unsigned int ipv4_neighbors;
//   unsigned int ipv6_neighbors;
//   unsigned int ipv4_routes;
//   unsigned int ipv6_routes;
//   unsigned int acl_ingress_entries;
//   unsigned int acl_ingress_entries_max;
//   unsigned int acl_ingress_counters;
//   unsigned int acl_ingress_counters_max;
//   unsigned int acl_ingress_meters;
//   unsigned int acl_ingress_meters_max;
//   unsigned int acl_ingress_slices;
//   unsigned int acl_ingress_slices_max;
//   unsigned int acl_egress_entries;
//   unsigned int acl_egress_entries_max;
//   unsigned int acl_egress_counters;
//   unsigned int acl_egress_counters_max;
//   unsigned int acl_egress_meters;
//   unsigned int acl_egress_meters_max;
//   unsigned int acl_egress_slices;
//   unsigned int acl_egress_slices_max;
// };

public class HwTables implements CounterData {
    public final long host_entries;
    public final long host_entries_max;
    public final long ipv4_entries;
    public final long ipv4_entries_max;
    public final long ipv6_entries;
    public final long ipv6_entries_max;
    public final long ipv4_ipv6_entries;
    public final long ipv6_ipv6_entries_max;
    public final long long_ipv6_entries;
    public final long long_ipv6_entries_max;
    public final long total_routes;
    public final long total_routes_max;
    public final long ecmp_nexthops;
    public final long ecmp_nexthops_max;
    public final long mac_entries;
    public final long mac_entries_max;
    public final long ipv4_neighbors;
    public final long ipv6_neighbors;
    public final long ipv4_routes;
    public final long ipv6_routes;
    public final long acl_ingress_entries;
    public final long acl_ingress_entries_max;
    public final long acl_ingress_counters;
    public final long acl_ingress_counters_max;
    public final long acl_ingress_meters;
    public final long acl_ingress_meters_max;
    public final long acl_ingress_slices;
    public final long acl_ingress_slices_max;
    public final long acl_egress_entries;
    public final long acl_egress_entries_max;
    public final long acl_egress_counters;
    public final long acl_egress_counters_max;
    public final long acl_egress_meters;
    public final long acl_egress_meters_max;
    public final long acl_egress_slices;
    public final long acl_egress_slices_max;

    public HwTables(final ByteBuf buffer) throws InvalidPacketException {
        this.host_entries = BufferUtils.uint32(buffer);
        this.host_entries_max = BufferUtils.uint32(buffer);
        this.ipv4_entries = BufferUtils.uint32(buffer);
        this.ipv4_entries_max = BufferUtils.uint32(buffer);
        this.ipv6_entries = BufferUtils.uint32(buffer);
        this.ipv6_entries_max = BufferUtils.uint32(buffer);
        this.ipv4_ipv6_entries = BufferUtils.uint32(buffer);
        this.ipv6_ipv6_entries_max = BufferUtils.uint32(buffer);
        this.long_ipv6_entries = BufferUtils.uint32(buffer);
        this.long_ipv6_entries_max = BufferUtils.uint32(buffer);
        this.total_routes = BufferUtils.uint32(buffer);
        this.total_routes_max = BufferUtils.uint32(buffer);
        this.ecmp_nexthops = BufferUtils.uint32(buffer);
        this.ecmp_nexthops_max = BufferUtils.uint32(buffer);
        this.mac_entries = BufferUtils.uint32(buffer);
        this.mac_entries_max = BufferUtils.uint32(buffer);
        this.ipv4_neighbors = BufferUtils.uint32(buffer);
        this.ipv6_neighbors = BufferUtils.uint32(buffer);
        this.ipv4_routes = BufferUtils.uint32(buffer);
        this.ipv6_routes = BufferUtils.uint32(buffer);
        this.acl_ingress_entries = BufferUtils.uint32(buffer);
        this.acl_ingress_entries_max = BufferUtils.uint32(buffer);
        this.acl_ingress_counters = BufferUtils.uint32(buffer);
        this.acl_ingress_counters_max = BufferUtils.uint32(buffer);
        this.acl_ingress_meters = BufferUtils.uint32(buffer);
        this.acl_ingress_meters_max = BufferUtils.uint32(buffer);
        this.acl_ingress_slices = BufferUtils.uint32(buffer);
        this.acl_ingress_slices_max = BufferUtils.uint32(buffer);
        this.acl_egress_entries = BufferUtils.uint32(buffer);
        this.acl_egress_entries_max = BufferUtils.uint32(buffer);
        this.acl_egress_counters = BufferUtils.uint32(buffer);
        this.acl_egress_counters_max = BufferUtils.uint32(buffer);
        this.acl_egress_meters = BufferUtils.uint32(buffer);
        this.acl_egress_meters_max = BufferUtils.uint32(buffer);
        this.acl_egress_slices = BufferUtils.uint32(buffer);
        this.acl_egress_slices_max = BufferUtils.uint32(buffer);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("host_entries", this.host_entries)
                .add("host_entries_max", this.host_entries_max)
                .add("ipv4_entries", this.ipv4_entries)
                .add("ipv4_entries_max", this.ipv4_entries_max)
                .add("ipv6_entries", this.ipv6_entries)
                .add("ipv6_entries_max", this.ipv6_entries_max)
                .add("ipv4_ipv6_entries", this.ipv4_ipv6_entries)
                .add("ipv6_ipv6_entries_max", this.ipv6_ipv6_entries_max)
                .add("long_ipv6_entries", this.long_ipv6_entries)
                .add("long_ipv6_entries_max", this.long_ipv6_entries_max)
                .add("total_routes", this.total_routes)
                .add("total_routes_max", this.total_routes_max)
                .add("ecmp_nexthops", this.ecmp_nexthops)
                .add("ecmp_nexthops_max", this.ecmp_nexthops_max)
                .add("mac_entries", this.mac_entries)
                .add("mac_entries_max", this.mac_entries_max)
                .add("ipv4_neighbors", this.ipv4_neighbors)
                .add("ipv6_neighbors", this.ipv6_neighbors)
                .add("ipv4_routes", this.ipv4_routes)
                .add("ipv6_routes", this.ipv6_routes)
                .add("acl_ingress_entries", this.acl_ingress_entries)
                .add("acl_ingress_entries_max", this.acl_ingress_entries_max)
                .add("acl_ingress_counters", this.acl_ingress_counters)
                .add("acl_ingress_counters_max", this.acl_ingress_counters_max)
                .add("acl_ingress_meters", this.acl_ingress_meters)
                .add("acl_ingress_meters_max", this.acl_ingress_meters_max)
                .add("acl_ingress_slices", this.acl_ingress_slices)
                .add("acl_ingress_slices_max", this.acl_ingress_slices_max)
                .add("acl_egress_entries", this.acl_egress_entries)
                .add("acl_egress_entries_max", this.acl_egress_entries_max)
                .add("acl_egress_counters", this.acl_egress_counters)
                .add("acl_egress_counters_max", this.acl_egress_counters_max)
                .add("acl_egress_meters", this.acl_egress_meters)
                .add("acl_egress_meters_max", this.acl_egress_meters_max)
                .add("acl_egress_slices", this.acl_egress_slices)
                .add("acl_egress_slices_max", this.acl_egress_slices_max)
                .toString();
    }

    @Override
    public void writeBson(final BsonWriter bsonWriter, final SampleDatagramEnrichment enr) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeInt64("host_entries", this.host_entries);
        bsonWriter.writeInt64("host_entries_max", this.host_entries_max);
        bsonWriter.writeInt64("ipv4_entries", this.ipv4_entries);
        bsonWriter.writeInt64("ipv4_entries_max", this.ipv4_entries_max);
        bsonWriter.writeInt64("ipv6_entries", this.ipv6_entries);
        bsonWriter.writeInt64("ipv6_entries_max", this.ipv6_entries_max);
        bsonWriter.writeInt64("ipv4_ipv6_entries", this.ipv4_ipv6_entries);
        bsonWriter.writeInt64("ipv6_ipv6_entries_max", this.ipv6_ipv6_entries_max);
        bsonWriter.writeInt64("long_ipv6_entries", this.long_ipv6_entries);
        bsonWriter.writeInt64("long_ipv6_entries_max", this.long_ipv6_entries_max);
        bsonWriter.writeInt64("total_routes", this.total_routes);
        bsonWriter.writeInt64("total_routes_max", this.total_routes_max);
        bsonWriter.writeInt64("ecmp_nexthops", this.ecmp_nexthops);
        bsonWriter.writeInt64("ecmp_nexthops_max", this.ecmp_nexthops_max);
        bsonWriter.writeInt64("mac_entries", this.mac_entries);
        bsonWriter.writeInt64("mac_entries_max", this.mac_entries_max);
        bsonWriter.writeInt64("ipv4_neighbors", this.ipv4_neighbors);
        bsonWriter.writeInt64("ipv6_neighbors", this.ipv6_neighbors);
        bsonWriter.writeInt64("ipv4_routes", this.ipv4_routes);
        bsonWriter.writeInt64("ipv6_routes", this.ipv6_routes);
        bsonWriter.writeInt64("acl_ingress_entries", this.acl_ingress_entries);
        bsonWriter.writeInt64("acl_ingress_entries_max", this.acl_ingress_entries_max);
        bsonWriter.writeInt64("acl_ingress_counters", this.acl_ingress_counters);
        bsonWriter.writeInt64("acl_ingress_counters_max", this.acl_ingress_counters_max);
        bsonWriter.writeInt64("acl_ingress_meters", this.acl_ingress_meters);
        bsonWriter.writeInt64("acl_ingress_meters_max", this.acl_ingress_meters_max);
        bsonWriter.writeInt64("acl_ingress_slices", this.acl_ingress_slices);
        bsonWriter.writeInt64("acl_ingress_slices_max", this.acl_ingress_slices_max);
        bsonWriter.writeInt64("acl_egress_entries", this.acl_egress_entries);
        bsonWriter.writeInt64("acl_egress_entries_max", this.acl_egress_entries_max);
        bsonWriter.writeInt64("acl_egress_counters", this.acl_egress_counters);
        bsonWriter.writeInt64("acl_egress_counters_max", this.acl_egress_counters_max);
        bsonWriter.writeInt64("acl_egress_meters", this.acl_egress_meters);
        bsonWriter.writeInt64("acl_egress_meters_max", this.acl_egress_meters_max);
        bsonWriter.writeInt64("acl_egress_slices", this.acl_egress_slices);
        bsonWriter.writeInt64("acl_egress_slices_max", this.acl_egress_slices_max);
        bsonWriter.writeEndDocument();

    }
}
