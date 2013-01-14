<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Sample Social</title>
    
    <link rel="stylesheet" type="text/css" href="${_r.contextPath}/bootstrap/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="${_r.contextPath}/bootstrap/css/bootstrap-responsive.css" />
    
    <link rel="stylesheet" type="text/css" href="${_r.contextPath}/css/all.css">
    [@webBundle path="/js/" type="js" /]
    
    [#-- Global Initialization --] 
    <script type="text/javascript">
      // set the contextPath as a javascript global variable
      var contextPath = "${_r.contextPath}";
    </script>
    [#-- /Global Initialization --] 
    	
  </head>

  <body>
   	<div id="bodyPage">
  	</div>
  
  <script type="text/javascript">
	$(function(){
		[#if user??]
				brite.display("MainScreen","#bodyPage");
	    [#else]
				brite.display("Login");
	    [/#if]
	});
	</script>
  </body>
</html>
