var app = app || {};
(function() {
	app.linkedInApi = {
		getCurrentUserInfo: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/currentUserInfo", param);
        },
		getJobBookmarks: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/jobbookmarks", param);
        },
        removebookmark: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/removebookmark", param);
        },
        addbookmark: function(param){
            param = param||{};
            param.method = "Post";
            return app.getJsonData(contextPath + "/linkedin/addbookmark", param);
        },
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
        },
        likeGroupPost : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/likeGroupPost", param);
        },
        userInfo : function(param) {
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/getUserInfo", param);
        },
        followedCompanys : function(param) {
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/followedCompanys", param);
        },
        suggestsFollowedCompanys : function(param) {
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/suggestsFollowedCompanys", param);
        },
        StartFollowingCompany : function(param) {
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/company/startFollowing", param);
        },
        StopFollowingCompany : function(param) {
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/company/stopFollowing", param);
        },
        CompanyUpdates : function(param) {
            param = param;
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/company/companyUpdates", param);
        },
        CommentCompanyUpdates : function(param) {
            param = param;
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/company/commentCompanyUpdates", param);
        },
        LikeCompanyUpdates : function(param) {
            alert(param.like);
            param = param;
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/company/likeCompanyUpdates", param);
        }
	};
})();
