<%@page import="dto.ReceptKompDTO"%>
<%@page import="dto.ProduktBatchDTO"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="produktAdmin" class="model.ProduktAdministration" type="model.ProduktAdministration" scope="session"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<style type="text/css">
	<%@include file="../../static/style.css" %>
</style>

<title>Produktbatch</title>
</head>
<body>
<%
ProduktBatchDTO produktBatch = produktAdmin.getProduktBatch();
%>
<jsp:include page="menubar.jsp" /><br>

<h1><%=produktBatch.getRecept().getReceptNavn()%></h1>
<%
String status = "Ikke påbegyndt";
if(produktBatch.getStatus()==1)
{
	status = "Under produktion";
}
else if(produktBatch.getStatus()==2)
{
		status = "Afsluttet";
}
else if(produktBatch.getStatus()==3)
{
		status = "FEJL i afvejning";
}
%>		
<p>Produktbatch id: <%=produktBatch.getPbId() %></p>
<p>Status: <%=status %></p>
<p>Recept id: <%=produktBatch.getRecept().getReceptId() %></p>
<p>Recept navn: <%=produktBatch.getRecept().getReceptNavn() %></p>
<p><b>Receptkomoponeneter:</b></p>
<%


for (int i=0; i<produktAdmin.getReceptKomp().size(); i++) {
		
		ReceptKompDTO receptKomp = (dto.ReceptKompDTO) produktAdmin.getReceptKomp().get(i);
		%>
		<b><%=i+1 %> Raavarenavn:</b> <%=receptKomp.getRaavare().getRaavareNavn() %><br>
		Netto: <%=receptKomp.getNomNetto() %> <br>
		Tolerance <%=receptKomp.getTolerance() %><br>
		<%
}
%>
<p><b>Produktbatch komoponeneter:</b></p>
<%
for (int i=0; i<produktAdmin.getproduktbatchKomp().size(); i++) {
		
		dto.ProduktBatchKompDTO produktbatchKomp = (dto.ProduktBatchKompDTO) produktAdmin.getproduktbatchKomp().get(i);
%>
		Råvarebatch id:<%=produktbatchKomp.getRb().getRbId() %><br>
		Netto: <%=produktbatchKomp.getNetto() %><br>
		Tara: <%=produktbatchKomp.getTara() %><br>

		<%
}
%>
<form method="POST">
<input type="hidden" name="menuValg" value="Vis produktbatch" />

<div class="bottomSubmit">	
	<input type = "submit" name="Tilbage" value="Tilbage">
</div>

</form>



</body>
</html>