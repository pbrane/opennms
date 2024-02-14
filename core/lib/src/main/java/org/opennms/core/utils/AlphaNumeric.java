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
package org.opennms.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Convenience classes for parsing and manipulating Strings.
 *
 * @author <a href="mailto:mike@opennms.org">Mike Davidson </a>
 */
public abstract class AlphaNumeric {
	
	private static final Logger LOG = LoggerFactory.getLogger(AlphaNumeric.class);

    /**
     * Any character in the passed string which does not match one of the
     * following values is replaced by the specified replacement character.
     *
     * Ascii chars: 0 - 9 (Decimal 48 - 57) A - Z (Decimal 65 - 90) a - z
     * (Decimal 97 - 122)
     *
     * For example: 'Ethernet 10/100' is converted to 'Ethernet_10_100'
     *
     * @param str
     *            string to be converted
     * @param replacement
     *            replacement character
     * @return Converted value which can be used in a file name.
     */
    public static String parseAndReplace(String str, char replacement) {
	return parseAndReplaceExcept(str, replacement, new Character(replacement).toString());
    }

    /**
     * Any character in the passed string which does not match one of the
     * following values is replaced by the specified replacement character,
     * unless it is contained in the exception string.
     *
     * Ascii chars: 0 - 9 (Decimal 48 - 57) A - Z (Decimal 65 - 90) a - z
     * (Decimal 97 - 122)
     *
     * For example: 'Ethernet 10/100' is converted to 'Ethernet_10_100'
     *
     * @param str
     *            string to be converted
     * @param replacement
     *            replacement character
     * @param except
     *            string containing exception characters
     * @return Converted value which can be used in a file name.
     */
    public static String parseAndReplaceExcept(String str, char replacement, String except) {
    	if (str == null) {
    		return "";
    	} else {
    		boolean replacedChar = false;
    		byte[] bytes = str.getBytes();
    		byte[] exBytes = except.getBytes();

    		blat: for (int x = 0; x < bytes.length; x++) {
    			if (!((bytes[x] >= 48 && bytes[x] <= 57) || (bytes[x] >= 65 && bytes[x] <= 90) || (bytes[x] >= 97 && bytes[x] <= 122))) {
    				for (int y = 0; y < exBytes.length; y++) {
    					if (bytes[x] == exBytes[y]) {
    						continue blat;
    					}
    				}
    				bytes[x] = (byte) replacement;
    				replacedChar = true;
    			}
    		}

    		String temp = new String(bytes);

    		// Log4j category
    		//
    		if (replacedChar) {
			LOG.debug("parseAndReplace: original='{}' new='{}'", str, temp);
    		}

    		return temp;
    	}
    }

    /**
     * Any character in the passed string which does not match one of the
     * following values is replaced by an Ascii space and then trimmed from the
     * resulting string.
     *
     * Ascii chars: 0 - 9 (Decimal 48 - 57) A - Z (Decimal 65 - 90) a - z
     * (Decimal 97 - 122)
     *
     * @param str
     *            string to be converted
     * @return Converted value.
     */
    public static String parseAndTrim(String str) {
        if (str == null) {
            return "";
        } else {
            byte[] bytes = str.getBytes();

            for (int x = 0; x < bytes.length; x++) {
                if (!((bytes[x] == 32) || (bytes[x] >= 48 && bytes[x] <= 57) || (bytes[x] >= 65 && bytes[x] <= 90) || (bytes[x] >= 97 && bytes[x] <= 122))) {
                    bytes[x] = 32; // Ascii space
                }
            }

            String temp = new String(bytes);
            temp = temp.trim();

            return temp;
        }
    }
}
