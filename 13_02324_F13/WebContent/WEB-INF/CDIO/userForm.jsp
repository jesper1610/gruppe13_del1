<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="login" class="model.Login" type="model.Login" scope="session"/>
<jsp:useBean id="brugerAdmin" class="model.BrugerAdministration" type="model.BrugerAdministration" scope="session"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<style type="text/css">
	<%@include file="../../static/style.css" %>
</style>

<title>Bruger formular</title>
</head>
<body>

<%!

String[] roller = new String[]{"Administrator","Farmaceut","V�rkf�rer","Operat�r"};
%>
<jsp:include page="menubar.jsp" /><br>
<h1>Bruger formular</h1>

<p><font color="red"><%=brugerAdmin.getError() %></font><font color="green"><%= brugerAdmin.getSucces() %></font><br>
<form method="POST">
	Operat�r navn: <input name="oprName" type="text" size = 20 value = "<%=brugerAdmin.getName() %>"><br>
	Initialer: <input type="text" name="ini" value = "<%= brugerAdmin.getIni() %>"><br>
	CPR: <input type="text" name="cpr" value = "<%= brugerAdmin.getCpr() %>"><br>
	Password:<input type="password" name="newPw" value = "<%= brugerAdmin.getPassword() %>"><br>
<%
	if(brugerAdmin.getId()==0 || !("Operat�r").equals(brugerAdmin.getRolle()))
	{
		%>
				Rolle: <select name="rolle">
				<option value="<%=brugerAdmin.getRolle()%>" selected="selected"><%=brugerAdmin.getRolle()%></option>
				<%
				for(int i = 0; i < roller.length; i++)
				{
					if(!roller[i].equals(brugerAdmin.getRolle()))
					{
						%>
							<option value="<%=roller[i]%>"><%=roller[i] %></option>
						<%
						
					}
				}
				%>
				</select><br>
				<%
	}else
	{
		out.println("Rolle: "+brugerAdmin.getRolle()+"<br>");
	}
	
	if(brugerAdmin.getId()==0)
	{
	%>
			<div class="bottomSubmit">	
				<input type="submit" name="handling" value="Opret bruger">
			</div>
	<%
	}
	else
	{
		%>
			<div class="bottomSubmit">
				<input type = "submit" name="Tilbage" value="Tilbage"><input type="submit" name="handling" value="�ndre">
				<input type="hidden" name="menuValg" value="Administrer bruger" />	
				<%
				if(!(brugerAdmin.getId()==login.getId()) && !("Operat�r".equals(brugerAdmin.getRolle())))
				{
					%>
						<input type = "submit" name="handling" value="Slet bruger">
					<%
				}
				%>
			</div>
		<%
	}
%>
</form>
</body>
</html>
