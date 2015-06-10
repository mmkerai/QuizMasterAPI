function readCookie(name)
{
  name += '=';
  var parts = document.cookie.split(/;\s*/);
  for (var i = 0; i < parts.length; i++)
  {
    var part = parts[i];
    if (part.indexOf(name) == 0)
      return part.substring(name.length);
  }
  return null;
}

/*
 * Saves a cookie for delay time. If delay is blank then no expiry.
 * If delay is less than 100 then assumes it is days
 * otherwise assume it is in seconds
 */
function saveCookie(name, value, delay)
{
  var date, expires;
  if(delay)
  {
	  if(delay < 100)	// in days
		  delay = delay*24*60*60*1000;	// convert to milliseconds
	  else
		  delay = delay*1000;
	  
	  date = new Date();
	  date.setTime(date.getTime()+delay);	// delay must be in seconds
	  expires = "; expires=" + date.toGMTString();		// convert unix date to string
  }
  else
	  expires = "";
  
  document.cookie = name+"="+value+expires+"; path=/";
}

/*
 * Delete cookie by setting expiry to 1st Jan 1970
 */
function delCookie(name) 
{
	document.cookie = name + "=; expires=Thu, 01-Jan-70 00:00:01 GMT; path=/";
}

function NewWin(htmlfile, name)		// open a new window
{
	WIDTH = 1000;
	HEIGHT = 500;
	var left = (screen.width/2)-(WIDTH/2);
	var top = (screen.height/2)-(HEIGHT/2);
	var winpop = window.open("/"+htmlfile, name,
				'toolbar=yes,location=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,width='+WIDTH+',height='+HEIGHT+',top='+top+',left='+left);
	winpop.focus();
	return winpop;
}

/*
 * This function sends a HTTP POST to the url and post data specified 
 * used to register a quizmaster only
 */
function doAjaxPostNoToken(url, postdata)
{
//	alert("URL is "+ url+" and post data is "+postdata);
	xmlhttp = getAjaxCnx();
	xmlhttp.open("POST",url,false);
	xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	xmlhttp.send(postdata);
	var response = xmlhttp.response;
	if(xmlhttp.status != 200 && xmlhttp.status != 201)	// SC_OK and SC_Created codes
	{
		document.getElementById("response").innerHTML = response;
		return;
	}
//	document.getElementById("response").innerHTML = txt;
	return response;
}

/*
 * This function sends a HTTP POST to the url and post data specified 
 * adds basic authentication token
 */
function doAjaxPost(url, postdata)
{
//	alert("URL is "+ url+" and post data is "+postdata);
//	url = url + APP_ID;		// add the app id to the url as per API spec
	var token = getQMtoken();
	xmlhttp = getAjaxCnx();
	xmlhttp.open("POST",url,false);
	xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send(postdata);
	var response = xmlhttp.response;
	if(xmlhttp.status != 200 && xmlhttp.status != 201)	// SC_OK and SC_Created codes
	{
		document.getElementById("response").innerHTML = response;
		return;
	}
//	document.getElementById("response").innerHTML = txt;
	return response;
}

/*
 * This function sends a HTTP POST to the url and post data specified 
 * used by contestant
 */
function doAjaxPostContestant(url, postdata)
{
//	alert("URL is "+ url+" and post data is "+postdata);
//	url = url + APP_ID;		// add the app id to the url as per API spec
	var token = getQMCtoken();
	xmlhttp = getAjaxCnx();
	xmlhttp.open("POST",url,false);
	xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send(postdata);
	var response = xmlhttp.response;
	if(xmlhttp.status != 200 && xmlhttp.status != 201)	// SC_OK and SC_Created codes
	{
		document.getElementById("response").innerHTML = response;
		return;
	}
//	document.getElementById("response").innerHTML = txt;
	return response;
}

/*
 * This function sends a HTTP GET to the url with a bearer token
 * for Quizmaster
 */
function doAjaxGet(url)
{
//	url = url + APP_ID;		// add the app id to the url as per API spec
//	alert("URL is "+ url);
	var token = getQMtoken();
	xmlhttp = getAjaxCnx();
	
	xmlhttp.open("GET",url,false);
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send();
	var response = xmlhttp.response;
	if(xmlhttp.status != 200)	// SC_OK codes
	{
		alert("Ajax Get Error code: "+xmlhttp.status);
	}
	return(response);
}

/*
 * This function sends a HTTP GET to the url with a bearer token
 * for Contestant
 */
function doAjaxGetContestant(url)
{
//	url = url + APP_ID;		// add the app id to the url as per API spec
//	alert("URL is "+ url);
	var token = getQMCtoken();
	xmlhttp = getAjaxCnx();
	
	xmlhttp.open("GET",url,false);
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send();
	var response = xmlhttp.response;
	if(xmlhttp.status != 200)	// SC_OK codes
	{
		alert("Ajax Get Error code: "+xmlhttp.status);
	}
	return(response);
}

/*
 * This function sends a HTTP GET to the url with Basic Authentication
 * Should receive an auth token if all OK
 */
function doAjaxGetWithBasicAuth(url, credentials)
{
//	alert("URL is "+ url);
//	var username = readCookie("QMName");
//	var password = readCookie("QMPassword");

	xmlhttp = getAjaxCnx();
	
	xmlhttp.open("GET",url,false);
	xmlhttp.setRequestHeader("Authorization","Basic "+btoa(credentials));	// base 64
//	xmlhttp.setRequestHeader("Authorization","Basic "+btoa(username+":"+password));	// base 64
	xmlhttp.send();
	var response = xmlhttp.response;
	if(xmlhttp.status != 200)	// SC_OK codes
	{
		alert("Ajax Get Error code: "+xmlhttp.status);
	}
	return(response);
}

/*
 * This function sends an async HTTP GET to the url and
 * displays the response in the html page at location where id is response
 */
function doAjaxGetAsync(gameid)
{
	if(FINISH_FLAG == 1) return;
	var token = getQMtoken();
	var xhr = getAjaxCnx();
	url = GAME_URL+"/"+gameid+"/contestants";
	xhr.open("GET",url);
	xhr.setRequestHeader("Authorization","Bearer "+token);
	xhr.onload = function (e)
	{
	  if(xhr.readyState === 4) 
	  {
	    if(xhr.status === 200) 
	    {
	      response = xhr.responseText;
//		  document.getElementById("response").innerHTML = response;	// for testing
		  UpdateGameContestantTable(response, CURRENT_QUESTION);
//		  console.log("Current Ques is "+CURRENT_QUESTION);
	    } 
	    else 
	    {
	      console.error(xhr.statusText);
	    }
	  }
	};
	xhr.onerror = function (e) 
	{
	  console.error(xhr.statusText);
	};
	xhr.send(null);
}

/*
 * This function sends a HTTP PUT to the url and put data specified and
 * displays the response in the html page at location where id is response
 */
function doAjaxPut(url, putdata)
{
//	alert("URL is "+ url+" and put data is "+putdata);
//	url = url + APP_ID;		// add the app id to the url as per API spec
	var token = getQMtoken();
	xmlhttp = getAjaxCnx();
	
	xmlhttp.open("PUT",url,false);
	xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send(putdata);
	var response = xmlhttp.response;
	if(xmlhttp.status != 200 && xmlhttp.status != 201)	// SC_OK and SC_Created codes
	{
		document.getElementById("response").innerHTML = response;
		return;
	}
//	document.getElementById("response").innerHTML = response;
	return response;
}

/*
 * This function sends a HTTP DELETE to the url and
 * displays the response in the html page at location where id is response
 */
function doAjaxDelete(url)
{
//	alert("URL is "+ url);
//	url = url + APP_ID;		// add the app id to the url as per API spec
	var token = getQMtoken();
	xmlhttp = getAjaxCnx();
	
	xmlhttp.open("DELETE",url,false);
	xmlhttp.setRequestHeader("Authorization","Bearer "+token);
	xmlhttp.send();
	var response = xmlhttp.response;
	if(xmlhttp.status != 200)	// SC_OK codes
	{
		alert("Ajax Get Error code: "+xmlhttp.status);
	}
//	alert("Error code: "+xmlhttp.status);
	return(response);
}

/*
 * retrieve quizmaster token from cookie and re-direct to login if none
 */

function getQMtoken()
{
	token = readCookie("QMAccessToken");
	if(token == null)
	{
		alert("You need to login first");
		window.location.assign("qmlogin.html");
	}
	return token;
}

/*
 * retrieve contestant token from cookie and re-direct to join game again if none
 */

function getQMCtoken()
{
	token = readCookie("QMCAccessToken");
	if(token == null)
	{
		alert("You need to join the game first");
		window.location.assign("joingame.html");
	}
	return token;
}

/*
 * Set up a ajax cnx
 */

function getAjaxCnx()
{
	if (window.XMLHttpRequest)
		xmlhttp=new XMLHttpRequest();
	else
		alert("This browser is not compatible. Please upgrade your browser");

	if("withCredentials" in xmlhttp)
	{
		return xmlhttp;		
	}
	else if(typeof XDomainRequest != "undefined")
	{
		xmlhttp = XDomainRequest();
		return xmlhttp;
	}
	else
		alert("CORS not supported");
}
