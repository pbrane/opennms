/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016-2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.snmp.snmp4j;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;

import com.google.common.primitives.Bytes;

public class Snmp4JDummyTransportTest {

	private static final Logger LOG = LoggerFactory.getLogger(Snmp4JDummyTransportTest.class);

	@Before
	public void setupSnmp4jLogging() {
		MockLogAppender.setupLogging(true, "DEBUG");
	}

	/**
	 * Here's the binary content generated by this test, you can run it through 'xxd'
	 * to view it in human-readable format:
	 * 
	 * cat file.xxd | xxd -r | xxd | less
	 * 
	 * 0000000: 3082 0133 0201 0004 0568 656c 6c6f a482
	 * 0000010: 0125 0601 0040 0400 0000 0002 0100 0201
	 * 0000020: 0043 0100 3082 010f 300e 0608 2b06 0102
	 * 0000030: 0101 0300 4302 1388 3016 060a 2b06 0106
	 * 0000040: 0301 0104 0100 0608 2b06 0102 0101 0300
	 * 0000050: 3011 0609 2b06 0106 0312 0103 0040 047f
	 * 0000060: 0000 0130 1906 082b 0601 0201 0103 0004
	 * 0000070: 0d54 7261 7020 4d73 6720 7632 2d31 3019
	 * 0000080: 0608 2b06 0102 0101 0300 040d 5472 6170
	 * 0000090: 204d 7367 2076 322d 3230 1906 082b 0601
	 * 00000a0: 0201 0105 0004 0d54 7261 7020 7631 206d
	 * 00000b0: 7367 2d31 3018 0607 2b06 0102 0101 0304
	 * 00000c0: 0d54 7261 7020 7631 206d 7367 2d32 301b
	 * 00000d0: 060a 2b06 0106 0301 0104 0101 040d 5472
	 * 00000e0: 6170 2076 3120 6d73 672d 3330 1206 0d2b
	 * 00000f0: 0601 0401 855d 0603 1201 0500 0201 0130
	 * 0000100: 0c06 082b 0601 0201 0105 0005 0030 0c06
	 * 0000110: 082b 0601 0201 0105 0180 0030 0c06 082b
	 * 0000120: 0601 0201 0105 0281 0030 0c06 082b 0601
	 * 0000130: 0201 0105 0382 00
	 *
	 * @throws Exception
	 */
	@Test
	public void testDummyTransport() throws Exception {
		PDU pdu = makePdu();
		byte[] bytes = Snmp4JUtils.convertPduToBytes(InetAddressUtils.ONE_TWENTY_SEVEN, 162, "hello", pdu);
		LOG.debug(SnmpUtils.getHexString(bytes));
		// Search for the community string
		assertTrue(Bytes.indexOf(bytes, new byte[] { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o'}) >= 0);
		// Search for the IP address string
		assertTrue(Bytes.indexOf(bytes, new byte[] { 127, 0, 0, 1 }) >= 0);
		// Search for something that shouldn't exist
		assertTrue(Bytes.indexOf(bytes, new byte[] { 127, 0, 10, 1 }) < 0);
		// Search for a varbind value ("Trap Msg v2-1")
		assertTrue(Bytes.indexOf(bytes, new byte[] {
			(byte)'T',
			(byte)'r',
			(byte)'a',
			(byte)'p',
			(byte)' ',
			(byte)'M',
			(byte)'s',
			(byte)'g',
			(byte)' ',
			(byte)'v',
			(byte)'2',
			(byte)'-',
			(byte)'1'
		}) >= 0);
	}


	private static final PDU makePdu() {
		PDU snmp4JV2cTrapPdu = new PDUv1();

		OID oid = new OID(".1.3.6.1.2.1.1.3.0");

		snmp4JV2cTrapPdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(5000)));
		snmp4JV2cTrapPdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(oid)));
		snmp4JV2cTrapPdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,new IpAddress("127.0.0.1")));

		snmp4JV2cTrapPdu.add(new VariableBinding(new OID(oid), new OctetString("Trap Msg v2-1")));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID(oid), new OctetString("Trap Msg v2-2")));

		snmp4JV2cTrapPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"), new OctetString("Trap v1 msg-1")));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.3"), new OctetString("Trap v1 msg-2")));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID(".1.3.6.1.6.3.1.1.4.1.1"), new OctetString("Trap v1 msg-3")));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID(".1.3.6.1.4.1.733.6.3.18.1.5.0"), new Integer32(1))); 
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"), new Null())); 
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.1"), new Null(128)));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.2"), new Null(129)));
		snmp4JV2cTrapPdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.3"), new Null(130)));
		snmp4JV2cTrapPdu.setType(PDU.V1TRAP);

		return snmp4JV2cTrapPdu;
	}
}

