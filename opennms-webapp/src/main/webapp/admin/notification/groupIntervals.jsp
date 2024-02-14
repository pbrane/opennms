<%--

    Licensed to The OpenNMS Group, Inc (TOG) under one or more
    contributor license agreements.  See the LICENSE.md file
    distributed with this work for additional information
    regarding copyright ownership.

    TOG licenses this file to You under the GNU Affero General
    Public License Version 3 (the "License") or (at your option)
    any later version.  You may not use this file except in
    compliance with the License.  You may obtain a copy of the
    License at:

         https://www.gnu.org/licenses/agpl-3.0.txt

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied.  See the License for the specific
    language governing permissions and limitations under the
    License.

--%>
<%@page language="java"
	contentType="text/html"
	session="true"
	import="
	    java.util.*,
		org.opennms.web.api.Util,
		org.opennms.core.utils.WebSecurityUtils,
		org.opennms.netmgt.config.*,
		org.opennms.netmgt.config.destinationPaths.*
	"
%>

<%!
    public void init() throws ServletException {
        try {
            UserFactory.init();
            GroupFactory.init();
            DestinationPathFactory.init();
        }
        catch( Exception e ) {
            throw new ServletException( "Cannot load configuration file", e );
        }
    }
%>

<%
    HttpSession user = request.getSession(true);
    Path newPath = (Path)user.getAttribute("newPath");
    String intervals[] = {"0m", "1m", "2m", "5m", "10m", "15m", "30m", "1h", "2h", "3h", "6h", "12h", "1d"};
%>

<%@ page import="org.opennms.web.utils.Bootstrap" %>
<% Bootstrap.with(pageContext)
          .headTitle("Group Intervals")
          .headTitle("Admin")
          .breadcrumb("Admin", "admin/index.jsp")
          .breadcrumb("Configure Notifications", "admin/notification/index.jsp")
          .breadcrumb("Destination Paths", "admin/notification/destinationPaths.jsp")
          .breadcrumb("Group Intervals")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<h2><%=(newPath.getName()!=null ? "Editing path: " + newPath.getName() + "<br/>" : "")%></h2>

<form method="post" name="groupIntervals" action="admin/notification/destinationWizard" >
  <%=Util.makeHiddenTags(request)%>
  <input type="hidden" name="sourcePage" value="groupIntervals.jsp"/>

<div class="card">
  <div class="card-header">
    <span>Choose the interval to wait between contacting each member in the groups.</span>
  </div>
  <div class="card-body">
    <div class="row">
      <div class="col-md-6">
        <table class="table table-sm table-borderless">
          <tr>
            <td valign="top" align="left">
            <%=intervalTable(newPath,
                             request.getParameterValues("groups"),
                             WebSecurityUtils.safeParseInt(request.getParameter("targetIndex")),
                             intervals)%>
            </td>
          </tr>
        </table>
      </div> <!-- column -->
    </div> <!-- row -->
  </div> <!-- card-body -->
  <div class="card-footer">
      <input type="reset" class="btn btn-secondary"/>
      <a class="btn btn-secondary" href="javascript:document.groupIntervals.submit()">Next Step <i class="fa fa-arrow-right"></i></a>
  </div> <!-- card-footer -->
</div> <!-- panel -->

</form>

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false" />

<%!
    public String intervalTable(Path path, String[] groups, int index, String[] intervals)
    {
        StringBuffer buffer = new StringBuffer("<table class=\"table table-sm table-borderless\">");
        
        for (int i = 0; i < groups.length; i++)
        {
            buffer.append("<tr><td>").append(groups[i]).append("</td>");
            buffer.append("<td>").append(buildIntervalSelect(path, groups[i], index, intervals)).append("</td>");
            buffer.append("</tr>");
        }
        
        buffer.append("</table>");
        return buffer.toString();
    }
    
    public String buildIntervalSelect(Path path, String group, int index, String[] intervals)
    {
        StringBuffer buffer = new StringBuffer("<select class=\"form-control custom-select\" NAME=\"" + group + "Interval\">");
        
        String selectedOption = "0m";
        
        for (int i = 0; i < intervals.length; i++)
        {
            if (path!=null && intervals[i].equals(getGroupInterval(path, group, index)) )
            {
                selectedOption = intervals[i];
                break;
            }
        }
        
        for (int i = 0; i < intervals.length; i++)
        {
            if (intervals[i].equals(selectedOption))
            {
                buffer.append("<option selected VALUE=\"" + intervals[i] + "\">").append(intervals[i]).append("</option>");
            }
            else
            {
                buffer.append("<option VALUE=\"" + intervals[i] + "\">").append(intervals[i]).append("</option>");
            }
        }
        buffer.append("</select>");
        
        return buffer.toString();
    }
    
    public String getGroupInterval(Path path, String group, int index)
    {
        List<Target> targets = null;
        
        if (index==-1)
        {
            targets = path.getTargets();
        }
        else
        {
            targets = path.getEscalates().get(index).getTargets();
        }
        
        for (final Target t : targets) {
            if (group.equals(t.getName())) {
                return t.getInterval().orElse(null);
            }
        }
        
        return null;
    }
%>
