<%@page import="com.google.gson.Gson"%>
<%@page import="java.io.IOException"%>
<%@page import="com.tranan.webstorage.shared.Item"%>
<%@page import="com.tranan.webstorage.server.DataServiceImpl"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%-- <%
	//Check if we serve mobile or not?
	String ua=request.getHeader("User-Agent").toLowerCase();
	if(ua.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*")||ua.substring(0,4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")) {
		//response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		//response.setHeader("Location", request.getRequestURI().replaceFirst("journey", "mobile"));
		 if (request.getPathInfo() == null || request.getPathInfo().length() <= 1) {
				redirectHomeUrl(response);
		 }
		 else{
			 String tripId = request.getPathInfo().replaceAll("/", "");
			 RequestDispatcher rd = getServletContext().getRequestDispatcher("/mtrip/"+ tripId);
				if (rd != null){
					rd.forward(request, response);
				}
				else {
					response.setStatus(response.SC_OK);
					redirectHomeUrl(response);
				}
		 } 
	}
%> --%>

<%!
	public void redirectHomeUrl(HttpServletResponse response) {
		/* String site = new String("/");	
		try {
			response.getWriter().print("<h1>NOT_FOUND</h1>");
		} catch(IOException e) {}
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setHeader("Location", site); */
	}
%>

<!-- Get the item  -->
<%
	Item item;
	String item_json;
	if (request.getPathInfo() == null
			|| request.getPathInfo().length() < 1) {
		redirectHomeUrl(response);
		return;
	} else {
		try {
			String[] url_path = request.getPathInfo().split("-");
			String itemId = url_path[url_path.length - 1];
			
			DataServiceImpl service = new DataServiceImpl();			
			item = service.getItemById(Long.valueOf(itemId));
			if (item == null) {
				redirectHomeUrl(response);
				return;
			}
			
			Gson gson = new Gson();
			item_json = gson.toJson(item);
		} catch (Exception e) {
			redirectHomeUrl(response);
			return;
		}
	} 
%>		 

<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="../index.css">
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Ubuntu"
	rel="stylesheet">
<link href="https://fonts.googleapis.com/css?family=Raleway"
	rel="stylesheet">

<!-- Specify the shortcut icon. -->
<link rel="shortcut icon" href="../favicon.ico" />

<title>Pretty Gal</title>
<%-- <title><%=trip.getName()%></title>
<meta property="og:title" content="<%=trip.getName().replaceAll("\"", "\'")%>" />
<meta property="og:type" content="article" />
<meta property="og:image" content="<%= tripAvatar %>" />
<meta property="og:url" content="http://born2go-b.appspot.com/journey/<%= tripId %>" />
<%if(trip.getDescription() != null) { %>
<meta property="og:description" content='<%=trip.getDescription().replaceAll("\'", "\"").replace("\n", "").replace("\r", "")%>' />
<%;} %> --%>

<script type="text/javascript" language="javascript" src="../prettygal/prettygal.nocache.js"></script>
<script src="http://connect.facebook.net/en_US/all.js"></script>
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic UI.        -->
<!--                                           -->
<body style="margin: 0px; background: whitesmoke; overflow-x: hidden;">

	<!-- OPTIONAL: include this if you want history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>

	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>
	
	<!-- <script type="text/javascript">
		window.onbeforeunload = function(){
			window.scrollTo(0,0);
		}
	</script> -->
	
	<div id="item" style="display: none;"><%= item_json %></div>
	
	<div id="item_content"></div>
	
</body>
</html>
