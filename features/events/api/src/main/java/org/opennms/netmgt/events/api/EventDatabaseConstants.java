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
package org.opennms.netmgt.events.api;

import java.util.List;


/**
 * This class contains the constants and methods related to inserting events
 * into the database
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya Kumaraswamy </A>
 */
public class EventDatabaseConstants extends EventConstants {
    /**
     * The 'parms' are added to a single column of the DB - the parm name and
     * value are added as delimiter separated list of ' <parmName>= <value>'
     * strings
     */
    public static final char NAME_VAL_DELIM = '=';

    /**
     * The delimiter used to delimit multiple values of the same element that
     * are appended and made the value of a single database column
     */
    public static final char MULTIPLE_VAL_DELIM = ';';

    /**
     * The parser adds the value and attributes of an element to a single
     * element of eventBlock and uses the ATTRIB_DELIM to separate these values
     */
    public static final String ATTRIB_DELIM = "/\\";

    /**
     * The values and the corresponding attributes of an element are added to a
     * single column of the table and delimited by DB_ATTRIB_DELIM
     */
    public static final char DB_ATTRIB_DELIM = ',';

    /**
     * Multiple values of any xml element are appended into one value when
     * inserted into the database - if the length of the appended string exceeds
     * the column length, the value is appended with this pattern
     */
    public static final String VALUE_TRUNCATE_INDICATOR = "...";

    /**
     * Cleans any unwanted ISO control characters from the input string by
     * reformatting them into their <EM>%add</EM> format.
     * @param inStr
     *            string that might contain the delimiter
     * @return The string with any ISO characters reformatted for db ops
     */
    public static String sanitize(final String inStr) {

        final StringBuilder buf = new StringBuilder();

        for (final char ch : inStr.toCharArray()) {
            if (Character.isISOControl(ch) && !Character.isWhitespace(ch)) {
                buf.append('%');
                buf.append(String.valueOf((int)ch));
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * This method is used to escape required values from strings that may
     * contain those values. If the passed string contains the passed value then
     * the character is reformatted into its <EM>%dd</EM> format.
     *
     * @param inStr
     *            string that might contain the delimiter
     * @param delimchar
     *            delimiter to escape
     * @return The string with the delimiter escaped as in URLs
     * @see #DB_ATTRIB_DELIM
     * @see #MULTIPLE_VAL_DELIM
     * @see #DB_ATTRIB_DELIM
     * @see #MULTIPLE_VAL_DELIM
     */
    public static String escape(final String inStr, final char delimchar) {
        
        final StringBuilder buf = new StringBuilder(inStr.length()+16);

        for (final char ch : inStr.toCharArray()) {
            if (ch == delimchar || (Character.isISOControl(ch) && !Character.isWhitespace(ch))) {
                buf.append('%');
                buf.append(String.valueOf((int)ch));
            } else {
                buf.append(ch);
            }
        }
        
        return buf.toString();
    }

    /**
     * This method is passed a list of strings and a maximum string size that
     * must not be exceeded by the composite string.
     *
     * @param strings
     *            The list of String objects.
     * @param maxlen
     *            The maximum length of the composite string
     * @return The composite string.
     * @exception java.lang.ClassCastException
     *                Thrown if any processed item in the list is not a string
     *                object.
     */
    public static String format(final List<String> strings, final int maxlen) {
        if (strings == null) {
            return null;
        }

        final StringBuilder buf = new StringBuilder();
        boolean first = true;

        for (String s : strings) {
            if (maxlen != 0 && buf.length() >= maxlen) break;
            s = escape(s, MULTIPLE_VAL_DELIM);
            if (!first)
                buf.append(MULTIPLE_VAL_DELIM);
            buf.append(s);
            first = false;
        }

        if (maxlen != 0 && buf.length() >= maxlen) {
            buf.setLength(maxlen - 4);
            buf.append(VALUE_TRUNCATE_INDICATOR);
        }
        return buf.toString();
    }

    /**
     * This method is passed an array of strings and a maximum string size that
     * must not be exceeded by the composite string.
     *
     * @param strings
     *            The list of String objects.
     * @param maxlen
     *            The maximum length of the composite string
     * @return The composite string.
     * @exception java.lang.ClassCastException
     *                Thrown if any processed item in the list is not a string
     *                object.
     */
    public static String format(final String[] strings, final int maxlen) {
        if (strings == null || strings.length <= 0)
            return null;

        final StringBuilder buf = new StringBuilder();
        boolean first = true;

        for (String s : strings) {
            if (maxlen != 0 && buf.length() >= maxlen) break;
            s = escape(s, MULTIPLE_VAL_DELIM);
            if (!first)
                buf.append(MULTIPLE_VAL_DELIM);
            buf.append(s);
            first = false;
        }

        if (maxlen != 0 && buf.length() >= maxlen) {
            buf.setLength(maxlen - 4);
            buf.append(VALUE_TRUNCATE_INDICATOR);
        }
        return buf.toString();
    }

    /**
     * This method is passed a string to be truncated to the maximum string size
     * passed.
     *
     * @param maxlen
     *            The maximum length of the composite string
     * @return The string(truncated if necessary).
     * @param origString a {@link java.lang.String} object.
     */
    public static String format(final String origString, final int maxlen) {
        if (origString == null)
            return null;
        
        final String escapedString = EventDatabaseConstants.escape(origString, '\u0000');

        if (maxlen != 0 && escapedString.length() >= maxlen) {
            final StringBuilder buf = new StringBuilder(escapedString);

            buf.setLength(maxlen - 4);
            buf.append(VALUE_TRUNCATE_INDICATOR);

            return buf.toString();
        } else {
            return escapedString;
        }
    }

}
