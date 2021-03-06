<%@page import="dto.RaavareBatchDTO"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="java.util.ArrayList" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="dto.RaavareDTO"%>
<jsp:useBean id="raavareAdmin" class="model.RaavareAdministration" scope="session" />
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
	<%@include file="../../static/style2.css" %>
</style>

<title>V�lg R�vare</title>

</head>
<body>

	<div class="header">
		<div class="headerContent">
			<jsp:include page="menubar.jsp" />
		</div>
	</div>
	
	
	<div class="content">
	
		<h1>V�lg r�vare</h1>
		
		<form method="POST" action="">
		<div class="scrollBox">
			<table border="1">
			    <tr>
			    <th WIDTH="10"></th>
			    <th>R�varenavn</th>
			    <th>R�vare id</th>
			    </tr>	
			<%
				ArrayList<RaavareDTO> raavareList = raavareAdmin.getRaavare();
				for (int i=0; i<raavareList.size(); i++) {
					RaavareDTO raavare = raavareList.get(i);
					%>
						<tr>
							<td WIDTH="10"><input type="radio" name="raavarevalg" value="<%= raavare.getRaavareId() %>"></td>
							<td>  <%= raavare.getRaavareNavn() %></td>
							<td align="Right"><%=raavare.getRaavareId()%>
							
						</tr>
					<%
				}
			%>
			</table>
		</div>
		<div class="bottomSubmit">
			<input type="submit" value="V�lg raavare">
		</div>
		
		</form>

	
	</div>

</body>

</html>