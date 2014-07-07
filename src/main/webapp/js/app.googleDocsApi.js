var app = app || {};
(function() {
	app.googleDocsApi = {
        getDocsList: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/listDocs", param);
        }
	};
})();
