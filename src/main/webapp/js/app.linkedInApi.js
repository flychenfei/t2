var app = app || {};
(function() {
	app.linkedInApi = {
		getConnections: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/connects", param);
        },
        getGroups : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/groups", param);
        },
        searchJobs : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/jobs", param);
        },
        searchCompanys : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/companys", param);
        },
        searchPeoples : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/searchPeople", param);
        }
	};
})();
