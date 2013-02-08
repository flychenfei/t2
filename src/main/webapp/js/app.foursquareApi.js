var app = app || {};
(function() {
	app.foursquareApi = {
		getUserInfo : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/foursquare/getUserInfo", param);
		}
	}
})();
