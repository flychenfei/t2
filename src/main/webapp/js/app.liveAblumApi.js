var app = app || {};
(function() {
	app.liveAblumApi = {
		getUserAblums : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/OutLook/getUserAblums", param);
		}
	}
})();