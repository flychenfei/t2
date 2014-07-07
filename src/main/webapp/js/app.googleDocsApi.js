var app = app || {};
(function() {
	app.googleDocsApi = {
        getDocsList: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/listDocs", param);
        },
        searchDocs: function(param){
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/search", param);
        },
        deleteDoc: function(param){
        	param = param || {};
        	param.Permanent = param.Permanent || false;
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDocsList/deleteDoc", param);
        }
	};
})();
