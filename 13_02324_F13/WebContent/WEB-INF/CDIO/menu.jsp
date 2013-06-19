<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<jsp:useBean id="login" class="model.Login" type="model.Login" scope="session"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<style type="text/css">
	<%@include file="../../static/style.css" %>
</style>

<title>Menu</title>
</head>
<body>
<jsp:include page="menubar.jsp" /><br>

	<h1>Menu</h1>
	
<p>Velkommen <%=login.getUser().getOprNavn() %>, du har rettigheder til f�lgende opgaver:</p>
<form method="POST">
	<%
		String rolle = login.getUser().getRolle();
	%>
	<input class="submitLink" type="submit" name="menuValg" value="Skift Adgangskode"><br>
	
	<h3>V�rkf�rer opgaver:</h3>	
	<input class="submitLink" type="submit" name="menuValg" value="Opret raavarebatch"><br>
	<input class="submitLink" type="submit" name="menuValg" value="Opret produktbatch"><br>					
	<input class="submitLink" type="submit" name="menuValg" value="Vis raavarebatch"><br>
	<input class="submitLink" type="submit" name="menuValg" value="Vis produktbatch"><br>
	<%
		if("Farmaceut".equals(rolle) || "Administrator".equals(rolle) )
		{
			%>
					<h3>Farmaceut opgaver:</h3>
					<input class="submitLink" type="submit" name="menuValg" value="Opret recept"><br>
					<input class="submitLink" type="submit" name="menuValg" value="Opret raavare"><br>
					<input class="submitLink" type="submit" name="menuValg" value="Administrer raavare"><br>
					<input class="submitLink" type="submit" name="menuValg" value="Vis recept"><br>
			<%
		}
		if("Administrator".equals(rolle) )
		{
			%>
					<h3>Administrator opgaver:</h3>
					<input class="submitLink" type="submit" name="menuValg" value="Opret bruger"><br>
					<input class="submitLink" type="submit" name="menuValg" value="Administrer bruger"><br>
			<%
		}
	%>
</form>

<form method="POST">
	<div class="bottomSubmit">
		<input type="hidden" name="handling" value="log_ud">
		<input type="submit" value="Log ud">
	</div>
</form>
	
	

</body>
</html>