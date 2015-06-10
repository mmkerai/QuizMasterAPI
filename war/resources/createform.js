/* 
* create contestant form within div "editcontestant"
*/
function CreateContestantForm(cont)
{
    conform  = document.createElement("FORM");
    conform.name = "editcontestantform";
    conform.method = "POST";
    conform.action = "#";
//	sstr = "<p><a href=# onClick='UpdateContestant("+cont.contestantId+")'>Update</a></p>";

	label1 = document.createTextNode('Contestant name: ');  
	label2 = document.createTextNode('Access Code: ');  
	label3 = document.createTextNode('Contestant email: ');  
	label4 = document.createTextNode("<a href=# onClick='UpdateContestant("+cont.contestantId+")'>Update</a>");
	
    inp1 = document.createElement("INPUT");
    inp1.type = "TEXT";
    inp1.name = "contestantname";
    inp1.value = cont.contestantName;
    p1 = document.createElement("P");
	p1.appendChild(label1);
    p1.appendChild(inp1);
    conform.appendChild(p1);
    
    inp1 = document.createElement("INPUT");
    inp1.type = "TEXT";
    inp1.name = "qmaccesscode";
    inp1.value = cont.accessCode;
    p1 = document.createElement("P");
	p1.appendChild(label2);
    p1.appendChild(inp1);
    conform.appendChild(p1);
    
    inp1 = document.createElement("INPUT");
    inp1.type = "TEXT";
    inp1.name = "contestantemail";
    inp1.value = cont.email;
    p1 = document.createElement("P");
	p1.appendChild(label3);
    p1.appendChild(inp1);
    conform.appendChild(p1);
 
    conform.appendChild(label4);
    document.getElementById("editcontestant").appendChild(conform);
  
/*    ctable = "<table border='1'><form id='updatecontestantform'>" +
    "<tr><td>Contestant Name</td><td><input type='text' name='contestantname' value='"+cont.contestantName+"'></td></tr>" +
	"<tr><td>Access Code</td><td><input type='text' name='qmaccesscode' value='"+cont.accessCode+"'></td></tr>" +
	"<tr><td>Email</td><td><input type='text' name='contestantemail' value='"+cont.email+"'></td></tr>" +
	"</form></table>" +
	"<p><a href=# onClick='UpdateContestant("+cont.contestantId+")'>Update</a></p>";
    
//    document.getElementById("editcontestant").appendChild(ctable);
    document.getElementById("editcontestant").innerHTML = ctable;
*/
}
