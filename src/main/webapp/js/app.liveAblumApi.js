var app = app || {};
(function() {
	app.liveAblumApi = {
		getUserAblums : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAblum/getUserAblums", param);
		},
		getAblum : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/liveAblum/getAblum", param);
		},
		"saveAblum": function (ablum) {
            return app.getJsonData(contextPath + "/liveAblum/saveAblum", ablum);
        }
	}
})();