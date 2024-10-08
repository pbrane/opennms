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
package org.opennms.web.asset;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.web.element.NetworkElementFactory;

import com.opencsv.CSVWriter;

/**
 *
 * Exports the assets database to a comma-separated values text file.
 *
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski</A>
 * @author <A HREF="mailto:ranger@opennms.org">Benjamin Reed</A>
 */
public class ExportAssetsServlet extends HttpServlet {
    private static final long serialVersionUID = -4854445395857220978L;
    protected AssetModel model;

    /**
     * <p>init</p>
     *
     * @throws javax.servlet.ServletException if any.
     */
    @Override
    public void init() throws ServletException {
        this.model = new AssetModel();
    }

    /** {@inheritDoc} */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Asset[] assets = null;

        try {
            assets = this.model.getAllAssets();
        } catch (SQLException e) {
            throw new ServletException("Database exception", e);
        }

        response.setContentType("text/plain");

        CSVWriter out = new CSVWriter(response.getWriter());

        String[] header = {
                "Node Label",
                "Node ID",
                "Category",
                "Manufacturer",
                "Vendor",
                "Model Number",
                "Serial Number",
                "Description",
                "Circuit ID",
                "Asset Number",
                "Operating System",
                "Rack",
                "Slot",
                "Port",
                "Region",
                "Division",
                "Department",
                "Address 1",
                "Address 2",
                "City",
                "State",
                "Zip",
                "Building",
                "Floor",
                "Room",
                "Vendor Phone",
                "Vendor Fax",
                "Date Installed",
                "Lease",
                "Lease Expires",
                "Support Phone",
                "Maint Contract",
                "Vendor Asset Number",
                "Maint Contract Expires",
                "Display Category",
                "Notification Category",
                "Poller Category",
                "Threshold Category",
                "Username",
                "Password",
                "Enable",
                "Connection",
                "Auto Enable",
                "Comments",
		"Cpu",
		"Ram",
		"Storage Controller",
		"HDD 1",
		"HDD 2",
		"HDD 3",
		"HDD 4",
		"HDD 5",
		"HDD 6",
		"Number of power supplies",
		"Inputpower",
		"Additional hardware",
		"Admin",
		"SNMP Community",
		"Rack unit height",
                "Country",
                "Longitude",
                "Latitude"
        };
        
        out.writeNext(header);

        // print a single line for each asset
        for (int i = 0; i < assets.length; i++) {
            Asset asset = assets[i];
            ArrayList<String> entries = new ArrayList<>();

            entries.add(NetworkElementFactory.getInstance(getServletContext()).getNodeLabel(asset.getNodeId()));
            entries.add(Integer.toString(asset.getNodeId()));
            entries.add(asset.getCategory());
            entries.add(asset.getManufacturer());
            entries.add(asset.getVendor());
            entries.add(asset.getModelNumber());
            entries.add(asset.getSerialNumber());
            entries.add(asset.getDescription());
            entries.add(asset.getCircuitId());
            entries.add(asset.getAssetNumber());
            entries.add(asset.getOperatingSystem());
            entries.add(asset.getRack());
            entries.add(asset.getSlot());
            entries.add(asset.getPort());
            entries.add(asset.getRegion());
            entries.add(asset.getDivision());
            entries.add(asset.getDepartment());
            entries.add(asset.getAddress1());
            entries.add(asset.getAddress2());
            entries.add(asset.getCity());
            entries.add(asset.getState());
            entries.add(asset.getZip());
            entries.add(asset.getBuilding());
            entries.add(asset.getFloor());
            entries.add(asset.getRoom());
            entries.add(asset.getVendorPhone());
            entries.add(asset.getVendorFax());
            entries.add(asset.getDateInstalled());
            entries.add(asset.getLease());
            entries.add(asset.getLeaseExpires());
            entries.add(asset.getSupportPhone());
            entries.add(asset.getMaintContract());
            entries.add(asset.getVendorAssetNumber());
            entries.add(asset.getMaintContractExpires());
            entries.add(asset.getDisplayCategory());
            entries.add(asset.getNotifyCategory());
            entries.add(asset.getPollerCategory());
            entries.add(asset.getThresholdCategory());
            entries.add(asset.getUsername());
            entries.add(asset.getPassword());
            entries.add(asset.getEnable());
            entries.add(asset.getConnection());
            entries.add(asset.getAutoenable());
            entries.add(asset.getComments());
            entries.add(asset.getCpu());
            entries.add(asset.getRam());
            entries.add(asset.getStoragectrl());
            entries.add(asset.getHdd1());
            entries.add(asset.getHdd2());
            entries.add(asset.getHdd3());
            entries.add(asset.getHdd4());
            entries.add(asset.getHdd5());
            entries.add(asset.getHdd6());
            entries.add(asset.getNumpowersupplies());
            entries.add(asset.getInputpower());
            entries.add(asset.getAdditionalhardware());
            entries.add(asset.getAdmin());
            entries.add(asset.getSnmpcommunity());
            entries.add(asset.getRackunitheight());
            entries.add(asset.getCountry());
            entries.add(asset.getLongitude());
            entries.add(asset.getLatitude());
            
            out.writeNext(entries.toArray(new String[0]));
        }

        out.close();
    }
}
