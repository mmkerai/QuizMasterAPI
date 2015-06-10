<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" %>

<%@ include file="pageheader.jsp" %>
<div id="wrapper">
<div id="header">
<h1>Welcome to Quiz Master Games</h1>
</div>
<p></p>
<div id="main">
<c:choose>
	<c:when test="${empty sessionScope.TCC_QMASTERAPP_EMAIL}">
		<p>To access the Quiz Master API you need to register using an OpenID service below</p>
		<p><a href="/openidlogin?provider=facebook">Login using facebook</a></p>
		<p><a href="/openidlogin?provider=google">Login using google</a></p>
		<p>This service uses OpenID for authentication purposes only. It does not 
			post anything anywhere. You will have to grant permission to TCC-OpenID when prompted
			by your chosen provider. 
			For more info see: <a href="http://en.wikipedia.org/wiki/OpenID">OpenID on Wikipedia</a></p>
	</c:when>
	<c:otherwise>
		<p>Greetings ${sessionScope.TCC_QMASTERAPP_EMAIL}</p>
		<p id="boldmessage">${sessionScope.ERROR}</p>
		<table>
		<form id="regappform" action="/updateapp" method="POST">
		<tr><td>App id:</td><td><input type="text" readonly="readonly" name="appid" value="${sessionScope.APP.appId}"/></td></tr>
		<tr><td>Email:</td><td><input type="text" readonly="readonly" value="${sessionScope.APP.appEmail}"/></td></tr>
		<tr><td>Name of App:</td><td><input type="text" name="appname" value="${sessionScope.APP.appName}"/></td></tr>
		<tr><td>App URL:</td><td><input type="text" name="appurl" value="${sessionScope.APP.appUrl}"/></td></tr>
		<tr><td>App Secret:</td><td><input type="text" readonly="readonly" value="${sessionScope.APP.appSecret}"/></td></tr>
		<tr><td><input type="submit" value="Update App Details"></td></tr>
		</form>
		</table>
		<p>Number of registered users: ${sessionScope.APP.regUsers}</p>
		<p>Total no. of API requests: ${sessionScope.APP.noOfRequests}</p>				
	</c:otherwise>
</c:choose>
<div id="response"></div>
<p>To view the API documentation <a href="resources/QuizMasterAPI.pdf">click here</a></p>
<p>To view the demo web app <a href="http://thecodecentre.co.uk/quizmaster">click here</a></p>
</div>
</div>

<%@ include file="pagefooter.jsp" %>
