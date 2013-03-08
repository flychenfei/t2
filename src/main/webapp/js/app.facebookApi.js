var app = app || {};
(function() {
	app.facebookApi = {
		getContacts : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/contacts", $.extend(params, opts || {}));
		},
		getFriends : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/friends", $.extend(params, opts || {}));
		},
		getFriendDetail : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/friend-detail", $.extend(params, opts || {}));
		},
		addContact : function(groupId, fbid) {
			var params = {
				"fbid" : fbid,
				groupId : groupId
			};
			return app.getJsonData(contextPath + "/fb/contact-add", params);
		},
		addPost : function(value) {
			var params = {
				"value" : value
			};
			return app.getJsonData(contextPath + "/fb/post-add", params);
		},
		deleteContact : function(id) {
			var params = {
				"id" : id
			};
			return app.getJsonData(contextPath + "/fb/contact-delete", params);
		},
		deleteEvent : function(id) {
			var params = {
				"id" : id
			};
			return app.getJsonData(contextPath + "/fb/delete-event", params);
		},
		deleteFeed : function(id) {
			var params = {
				"id" : id
			};
			return app.getJsonData(contextPath + "/fb/post-delete", params);
		},
		getPosts : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/posts", $.extend(params, opts || {}));
		},
		getPhotos : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/photos", $.extend(params, opts || {}));
		},
		getNotes : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/notes", $.extend(params, opts || {}));
		},
		getEvents : function(opts) {
			var params = {
				method : "Get"
			};
			return app.getJsonData(contextPath + "/fb/events", $.extend(params, opts || {}));
		},
		publishPhoto : function(data,fileElement) {
			return app.ajaxPost(contextPath + "/fb/post-add-photo", data,fileElement);
		},
		addNote : function(value) {
			return app.getJsonData(contextPath + "/fb/note-add", value);
		},
		addEvent : function(value) {
			return app.getJsonData(contextPath + "/fb/event-add", value);
		}
	}
})();