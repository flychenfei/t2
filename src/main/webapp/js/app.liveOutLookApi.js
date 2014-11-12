var app = app || {};
(function() {
	app.liveOutLookApi = {
		getUserInfo : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/OutLook/getUserInfo", param);
		},
		getUserContactlist : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/OutLook/getUserContactlist", param);
		}
	}
})();