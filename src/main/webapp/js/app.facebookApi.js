var app = app || {};
(function() {
	app.foursquareApi = {
		getUserInfo : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/foursquare/getUserInfo", param);
		}
	}
	
	app.getFBContacts = function(opts) {
		var params = {
			method : "Get"
		};
		return app.getJsonData(contextPath + "/fb/contacts", $.extend(params, opts || {}));
	};

	app.getFBFriends = function(opts) {
		var params = {
			method : "Get"
		};
		return app.getJsonData(contextPath + "/fb/friends", $.extend(params, opts || {}));
	};

	app.getFacebookFriendDetail = function(opts) {
		var params = {
			method : "Get"
		};
		return app.getJsonData(contextPath + "/fb/friend-detail", $.extend(params, opts || {}));
	};

	app.addFacebookContact = function(groupId, fbid) {
		var params = {
			"fbid" : fbid,
			groupId : groupId
		};
		return app.getJsonData(contextPath + "/fb/contact-add", params);
	};

	app.addPost = function(value) {
		var params = {
			"value" : value
		};
		return app.getJsonData(contextPath + "/fb/post-add", params);
	};

	app.deleteFBContact = function(id) {
		var params = {
			"id" : id
		};
		return app.getJsonData(contextPath + "/fb/contact-delete", params);
	};
	app.getFBPosts = function(opts) {
		var params = {
			method : "Get"
		};
		return app.getJsonData(contextPath + "/fb/posts", $.extend(params, opts || {}));
	};
})();
