var app = app || {};
(function() {
	app.googleDocsApi = {
        getDocsList: function(param){
            param = param||{};
            param.pageIndex = param.pageIndex || 0;
            param.pageSize = param.pageSize || 10;
            param.withResultCoun = param.withResultCoun || true;
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/listDocs", param);
        },
        searchDocs: function(param){
        	param = param||{};
            param.pageIndex = param.pageIndex || 0;
            param.pageSize = param.pageSize || 10;
            param.withResultCoun = param.withResultCoun || true;
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/search", param);
        }
	};
})();
