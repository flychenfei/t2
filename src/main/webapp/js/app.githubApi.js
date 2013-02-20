var app = app || {};
(function(){
	app.githubApi={
        showUserInfo : function(){
        	 var params = {};
        	 params.method = "Get";
        	return app.getJsonData(contextPath + "/github/userInfo",params);
        },
        addEmail:function(opts){
        	var params = opts||{};
        	params.method = "Post";
        	return app.getJsonData(contextPath + "/github/addEmail",params);
        },
        deleteEmail:function(opts){
        	var params = opts||{};
        	params.method = "Post";
        	return app.getJsonData(contextPath + "/github/deleteEmail",params);
        },
        getRepositories:function(){
        	var params = {};
        	params.method = "Get";
        	return app.getJsonData(contextPath + "/github/repositories",params);
        },
        createRepository:function(opts){
        	var params = opts||{};
        	params.method = "Post";
        	return app.getJsonData(contextPath + "/github/createRepository",params);
        },
        editRepository:function(opts){
        	var params = opts||{};
        	params.method="Post";
        	return app.getJsonData(contextPath+"/github/editRepository",params);
        },
        getCommits:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getCommits",params);
        },
        getCommit:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getCommit",params);
        }
    };
})();