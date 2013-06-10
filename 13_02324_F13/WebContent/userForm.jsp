<%@ include file = "adminLoginCheck.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="login" class="controller.Login" type="controller.Login" scope="session"/>
<jsp:useBean id="valg" class="controller.BrugerValg" type="controller.BrugerValg" scope="session"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bruger formular</title>
</head>
<body>

<h1>Bruger formular</h1>

<p><font color="red"><%=valg.getError() %></font><font color="green"><%= valg.getSucces() %></font><br>

<form method="POST">
	Operat�r navn: <input name="oprName" type="text" size = 20 value = "<%= valg.name %>"><br>
	Initialer: <input type="text" name="ini" value = "<%= valg.ini %>"><br>
	CPR: <input type="text" name="cpr" value = "<%= valg.cpr %>"><br>
	Password:<input type="password" name="newPw" value = "<%= valg.password %>"><br>
	Rolle: <select name="rolle">
			<option value="Administrator">Administrator</option>
			<option value="Farmaceut">Farmaceut</option>
			<option value="V�rkf�rer">V�rkf�rer</option>
			<option value="Operat�r" selected="selected">Operat�r</option>
			</select>
<%
	if(valg.id==0)
	{
		%>
			<input type = "submit" name="menuValg" value="Tilbage"><input type="submit" name="handling" value="Opret bruger">
		<%
	}
	else
	{
		%>
			<input type = "submit" name="menuValg" value="Tilbage"><input type="submit" name="handling" value="�ndre">
			<%
			if(!(valg.id==login.getId()))
			{
				%>
					<input type = "submit" name="handling" value="Slet">
				<%
			}
			%>
		<%
	}
%>
</form>
</body>
</html>
