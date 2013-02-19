var app = app || {};
(function() {
	app.googleApi = {
		"getContact": function (opts) {
            var params = opts || {};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gcontact/get", params);
        },
        "createContact": function (contact) {
            return app.getJsonData(contextPath + "/gcontact/create", contact);
        },
        "getGroups": function (opts) {
            var params = {
                method: "Get"
            };
            $.extend(params, opts);
            return app.getJsonData(contextPath + "/ggroup/list", params);
        },
        createGroup: function(params){
            return app.getJsonData(contextPath + "/ggroup/create", params);
        },
        "deleteGroup": function (groupId, etag) {
            var params = {"groupId": groupId, etag: etag};
            return app.getJsonData(contextPath + "/ggroup/delete", params);
        },
        getContacts: function (opts) {
            var params = {
                method: "Get"
            };
            return app.getJsonData(contextPath + "/gcontact/list", $.extend(params, opts || {}));
        },
        deleteContact: function (contactId, etag) {
            var params = {"contactId": contactId, etag: etag};
            return app.getJsonData(contextPath + "/gcontact/delete", params);
        },
        getEmails: function (opts) {
            var params = {
                method: "Get"
            };
            return app.getJsonData(contextPath + "/gmail/list", $.extend(params, opts || {}));
        },
        getFolders: function () {
            var params = {method: "Get"};
            return app.getJsonData(contextPath + "/gmail/folders", params);
        },
        deleteFolder: function (folderName) {
            var params = {method: "Post", folderName: folderName};
            return app.getJsonData(contextPath + "/gmail/folder/delete", params);

        } ,
        deleteEmail: function (id) {
            var params = {id: id};
            params.method = "Post"

            return app.getJsonData(contextPath + "/gmail/delete", params);
        },
        getMail: function(id){
            var params = {id: id};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmail/get", params);
        },
        sendMail: function(params) {
            return app.getJsonData(contextPath + "/gmail/send", params);
        },
        searchEmails: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmail/search", params);
        },
        searchContact: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gcontact/search", params);
        }
	};
})();
