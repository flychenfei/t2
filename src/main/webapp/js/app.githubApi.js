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
        },
        getReadme:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getReadme",params);
        },
        getPublicEvents:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/events",params);
        },
        getContents:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getContents",params);
        },
        getDownloads:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getDownloads",params);
        },
        deleteDownload:function(opts){
        	var params = opts||{};
        	params.method="Post";
        	return app.getJsonData(contextPath+"/github/deleteDownload",params);
        },
        getForks:function(opts){
        	var params = opts||{};
        	params.method="Get";
        	return app.getJsonData(contextPath+"/github/getForks",params);
        },
        createFork:function(opts){
        	var params = opts||{};
        	params.method="Post";
        	return app.getJsonData(contextPath+"/github/createFork",params);
        },
		getIssues:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getIssues",params);
		},
		getIssue:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getIssue",params);
		},
		newIssue:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/newIssue",params);
		},
		getReleases:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getReleases",params);
		},

		editRelease:function(opts){
			var params = opts||{};
			params.method="Post";
			return app.getJsonData(contextPath+"/github/editRelease",params);
		},
		createRelease:function(opts){
			var params = opts||{};
			params.method="Post";
			return app.getJsonData(contextPath+"/github/createRelease",params);
		},
		deleteRelease:function(opts){
			var params = opts||{};
			params.method="Post";
			return app.getJsonData(contextPath+"/github/deleteRelease",params);
		},
		getPullRequests:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getPullRequests",params);
		},
		getPullRequest:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getPullRequest",params);
		},
		editPullRequest:function(opts){
			var params = opts||{};
			params.method="Post";
			return app.getJsonData(contextPath+"/github/editPullRequest",params);
		},

		editIssue:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/editIssue",params);
		},

		editComment:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/editComment",params);
		},

		editTitle:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/editTitle",params);
		},

		deleteComment:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/deleteComment",params);
		},

		addComment:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/addComment",params);
		},

		getIssueEvents:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getIssueEvents",params);
		},

		getIssuesCount:function(opts){
			var params = opts||{};
			params.method="Get";
			return app.getJsonData(contextPath+"/github/getIssuesCount",params);
		}
    };
})();