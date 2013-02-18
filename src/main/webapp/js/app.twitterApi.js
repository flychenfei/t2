var app = app || {};
(function() {
	app.twitterApi = {
		getUserInfo : function(param) {
			param = param||{};
			param.method = "Get";
			return app.getJsonData(contextPath + "/twitter/getUserInfo", param);
		},
		getTimeline : function(param) {
			param = param||{};
			param.method = "Get";
			return app.getJsonData(contextPath + "/twitter/getTimeline", param);
		},
		getRetweetById : function(param) {
			param = param||{};
			param.method = "GET";
			return app.getJsonData(contextPath + "/twitter/getRetweetById", param);
		},
		postStatus : function(param) {
			param = param||{};
			param.method = "POST";
			return app.getJsonData(contextPath + "/twitter/postStatus", param);
		},
		retweet : function(param) {
			param = param||{};
			param.method = "POST";
			return app.getJsonData(contextPath + "/twitter/retweet", param);
		},
		favorite : function(param) {
			param = param||{};
			param.method = "POST";
			return app.getJsonData(contextPath + "/twitter/favorite", param);
		},
		getUserTimeline : function(param) {
			param = param||{};
			param.method = "GET";
			return app.getJsonData(contextPath + "/twitter/getUserTimeline", param);
		},
		
		destroyTweet : function(param) {
			param = param||{};
			param.method = "POST";
			return app.getJsonData(contextPath + "/twitter/destroyTweet", param);
		},
		
		getSuggestions : function(param) {
			param = param||{};
			param.method = "GET";
			return app.getJsonData(contextPath + "/twitter/getSuggestions", param);
		},
		
		getMentionTimeline : function(param) {
			param = param||{};
			param.method = "GET";
			return app.getJsonData(contextPath + "/twitter/getMentionTimeline", param);
		},
		
		getRetweets : function(param) {
			param = param||{};
			param.method = "GET";
			return app.getJsonData(contextPath + "/twitter/getRetweets", param);
		}
	}
})();
