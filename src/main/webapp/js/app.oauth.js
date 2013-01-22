var app = app || {};
(function($) {
	app.oauth = {};
	
	app.oauth.authorize = function(service){
    return window.showModalDialog(contextPath+"/authorize?service="+service);
	}
	
	app.oauth.setToken = function(paramsStr,service){
		var params = {mehotd:"POST"};
		params.params = paramsStr;
		params.service = service;
		return app.getJsonData(contextPath+"/setToken.do",params);
	}
})(jQuery);
