<%@page import="dto.ProduktBatchDTO"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="produktAdmin" class="model.ProduktAdministration" type="model.ProduktAdministration" scope="session"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<style type="text/css">
	<%@include file="../../static/style2.css" %>
</style>

<title>V�lg produktbatch</title>
</head>
<body>

	<div class="header">
		<div class="headerContent">
			<jsp:include page="menubar.jsp" />
		</div>
	</div>


	<div class="content">
		<h1>V�lg produktbatch</h1>
		
		<form method="POST" action="">
			<div class="bottomSubmit">
				Indtast receptnavn for at s�ge:<input class="onInputLine" type = "text" name = "searchProduktBatch"><input class="onInputLine" type = "submit" name="searchProduktB" value="Soeg p� recept navn"><br>
			</div>
			<br>
			<%
			ArrayList<ProduktBatchDTO> produktbatchList = produktAdmin.getProduktbatch();
			for (int i=0; i<produktbatchList.size(); i++) {
				
				dto.ProduktBatchDTO produktBatch = produktbatchList.get(i);
				String color = "black";
				String status = "Ikke p�begyndt";
				if(produktBatch.getStatus()==1)
				{
					color = "green";
					status = "Under produktion";
				}
				else if(produktBatch.getStatus()==2)
				{
					color = "";
					status = "Afsluttet";
				}
				else if(produktBatch.getStatus()==3)
				{
					color = "red";
					status = "FEJL under afvejning";
				}
				%>
					<input type="radio" name="produktbatchValg" value="<%= produktBatch.getPbId() %>">
					<font color="<%=color%>">Status:<%=status %> produktbatch id <%=produktBatch.getPbId() %>: <%=produktBatch.getRecept().getReceptNavn() %></font><br>
				<%
			}
			%>
			<div class="bottomSubmit">
				<input type="submit" value="V�lg produktbatch">
			</div>
			
		</form>
		
	</div>
		
</body>
</html>