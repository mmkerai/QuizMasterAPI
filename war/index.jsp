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
	<c:when test="${empty sessionScope.TCC_QMASTER_ID}">
		<p>To set up and play a game you need to login first using an OpenID service below</p>
		<p><a href="/openidlogin?provider=facebook">Login using facebook</a></p>
		<p><a href="/openidlogin?provider=google">Login using google</a></p>
		<p>This service uses OpenID for authentication purposes only. It does not 
			post anything anywhere. You will have to grant permission to TCC-OpenID when prompted
			by your chosen provider. 
			For more info see: <a href="http://en.wikipedia.org/wiki/OpenID">OpenID on Wikipedia</a></p>
		<p>If you want to join a game click below.<br/>
		<a href="joingame.html">Join a game</a></p>
	</c:when>
	<c:otherwise>
		<p>Greetings Quiz-Master ${sessionScope.TCC_QMASTER_EMAIL}</p>
		<p><a href="#" onClick="NewWin('setupnewgame.html', 'Set Up Game')">Set up a new game</a></p>
		<p><a href="showgames.html">Manage your games</a></p>
		<p>If you are a contestant:</p><br/>
		<p><a href="joingame.html">Join a game</a></p>
	</c:otherwise>
</c:choose>
<!--<p>If you are the Quiz Master:</p>
		<p><a href="#" onClick="NewWin('setupnewgame.html', 'Set Up Game')">Set up a new game</a></p>
		<p><a href="showgames.html">Manage your games</a></p>
		<p>If you are a contestant:</p>
		<p><a href="joingame.html">Join a game</a></p>
-->
<div id="response"></div>
</div>
</div>

<%@ include file="pagefooter.jsp" %>
