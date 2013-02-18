var app = app || {};
(function() {
	app.liveApi = {
		getUserInfo : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/getUserInfo", param);
		}
	}
})();
