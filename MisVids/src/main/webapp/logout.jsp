<%-- 
    Document   : logout
    Created on : Mar 7, 2025, 4:04:22 PM
    Author     : alumn
--%>

<%
    session.invalidate();
    response.sendRedirect("login.jsp");
%>
