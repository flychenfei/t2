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
        trashEmail: function (id) {
            var params = {id: id};
            params.method = "Post"
            return app.getJsonData(contextPath + "/gmail/trash", params);
        },
        getMail: function(id){
            var params = {id: id};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmail/get", params);
        },
        sendMail: function(opts, files) {
            opts = opts || {};
            files = files || [];
            var dfd = $.Deferred();
			var formData = new FormData();
			
			for(var key in opts){
				formData.append(key, opts[key]);
			}
			
			for(var i = 0; i < files.length; i++){
				if(files[i]){
					formData.append("files", files[i]);
				}
			}
			
			var xhr = new XMLHttpRequest();
			xhr.open('POST', contextPath + "/gmail/send", true);
			xhr.onload = function(e) {
				var ret = eval("(" + this.response + ")").result;
				dfd.resolve(ret);
			};
			xhr.send(formData);
			return dfd.promise();
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
		listByCalendars: function(opts) {
			var params = opts || {};
			params.method = "Get";
			return app.getJsonData(contextPath + "/googleCalendarEvents/listByCalendars", params);
		},
		listFreeBusy: function(opts) {
			var params = opts || {};
			params.method = "Get";
			return app.getJsonData(contextPath + "/googleCalendarEvents/listFreeBusy", params);
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
        saveCopyCalendarEvent: function(opts) {
            var params = opts||{};
            params.method = "Post";
            return app.getJsonData(contextPath + "/googleCopyCalendarEvents/save", params);
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
        listSetting: function(opts) {
            var params = opts||{};
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendarSetting/list", params);
        },
        getSetting: function(settingId) {
            var params = {settingId:settingId}
            params.method = "Get";
            return app.getJsonData(contextPath + "/googleCalendarSetting/get", params);
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
        untrashEmailRest: function(id) {
            var params = {id: id};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/untrash", params);
        },
        getMailRest: function(id) {
            var params = {id: id};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/get", params);
        },
        importMailRest: function(opts, files) {
            opts = opts || {};
            files = files || [];
            var dfd = $.Deferred();
            var formData = new FormData();

            for(var key in opts){
                formData.append(key, opts[key]);
            }

            for(var i = 0; i < files.length; i++){
                if(files[i]){
                    formData.append("files", files[i]);
                }
            }
            var xhr = new XMLHttpRequest();
            xhr.open('POST', contextPath + "/gmailrest/import", true);
            xhr.onload = function(e) {
                var ret = eval("(" + this.response + ")").result;
                dfd.resolve(ret);
            };
            xhr.send(formData);
            return dfd.promise();
        },
        insertMailRest: function(opts, files) {
            opts = opts || {};
            files = files || [];
            var dfd = $.Deferred();
            var formData = new FormData();

            for(var key in opts){
                formData.append(key, opts[key]);
            }

            for(var i = 0; i < files.length; i++){
                if(files[i]){
                    formData.append("files", files[i]);
                }
            }
            var xhr = new XMLHttpRequest();
            xhr.open('POST', contextPath + "/gmailrest/insert", true);
            xhr.onload = function(e) {
                var ret = eval("(" + this.response + ")").result;
                dfd.resolve(ret);
            };
            xhr.send(formData);
            return dfd.promise();
        },
        sendMailRest: function(opts, files) {
            opts = opts || {};
            files = files || [];
            var dfd = $.Deferred();
			var formData = new FormData();

			for(var key in opts){
				formData.append(key, opts[key]);
			}

			for(var i = 0; i < files.length; i++){
				if(files[i]){
					formData.append("files", files[i]);
				}
			}
			var xhr = new XMLHttpRequest();
			xhr.open('POST', contextPath + "/gmailrest/send", true);
			xhr.onload = function(e) {
				var ret = eval("(" + this.response + ")").result;
				dfd.resolve(ret);
			};
			xhr.send(formData);
			return dfd.promise();
        },
        forwardMailRest: function(opts, files) {
            opts = opts || {};
            files = files || [];
            var dfd = $.Deferred();
            var formData = new FormData();
            for(var key in opts){
                formData.append(key, opts[key]);
            }
            for(var i = 0; i < files.length; i++){
                if(files[i]){
                    formData.append("files", files[i]);
                }
            }
            var xhr = new XMLHttpRequest();
            xhr.open('POST', contextPath + "/gmailrest/forward", true);
            xhr.onload = function(e) {
                var ret = eval("(" + this.response + ")").result;
                dfd.resolve(ret);
            };
            xhr.send(formData);
            return dfd.promise();
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
        },
        updateLabelsRest: function(opts) {
            var params = opts || {};
            params.method = "Post";
            return app.getJsonData(contextPath + "/gmailrest/updateLabels", params);
        },
        getGmailAnalytics: function(opts) {
            var params = opts || {};
            params.method = "Get";
            return app.getJsonData(contextPath + "/gmailrest/gmailAnalytics", params);
        }
	};
})();
