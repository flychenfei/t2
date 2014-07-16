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
        saveFolder: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmail/folder/save", params);
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
        },
        listCalendarEvents: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendarEvents/list", params);
        },
        getCalendarEvent: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendarEvents/get", params);
        },
        saveCalendarEvent: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleCalendarEvents/save", params);
        },
        deleteCalendarEvent: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleCalendarEvents/delete", params);
        },
        listCalendars: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendars/list", params);
        },
        getCalendars: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendars/get", params);
        },
        saveCalendars: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleCalendars/save", params);
        },
        deleteCalendars: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleCalendars/delete", params);
        },
        saveShareCalendar: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleShareCalendars/save", params);
        },
        getShareCalendar: function(calendarId) {
            var params = {calendarId:calendarId}
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleShareCalendars/get", params);
        }, 
        deleteSharedCalendar:function(opts){
        	var params = {"calendarId":opts.calendarId,"ruleId":opts.ruleId}
            params.method = "Post";
            return app.getJsonData(contextPath + "/deleteSharedCalendar/delete", params);
        },
        searchEmailsRest: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/search", params);
        },
        deleteEmailRest: function(id) {
            var params = {id: id};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/delete", params);
        },
        trashEmailRest: function(id) {
            var params = {id: id};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/trash", params);
        },
        getMailRest: function(id) {
            var params = {id: id};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/get", params);
        },
        sendMailRest: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/send", params);
        },
        listLabelsRest: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/labels/list", params);
        },
        deleteLabelRest: function(id) {
            var params = {id: id};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/labels/delete", params);
        },
        getLabelRest: function(id) {
            var params = {id: id};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/labels/get", params);
        },
        saveLabelRest: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/labels/save", params);
        },
        getThreadMailsRest: function(threadId) {
            var params = {id:threadId};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/getThreadMails", params);
        }
	};
})();
