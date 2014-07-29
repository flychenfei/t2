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
        },
        groupDetails : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/groupDetails", param);
        },
        groupPost : function(param) {
            param = param||{};
            param.start = param.start || 0;
            param.count = param.count || 10;
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/groupPost", param);
        },
        groupPostComments : function(param) {
            param = param||{};
            param.start = param.start || 0;
            param.count = param.count || 10;
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/groupPostComments", param);
        },
        leaveGroup : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/leaveGroup", param);
        }
	};
})();
