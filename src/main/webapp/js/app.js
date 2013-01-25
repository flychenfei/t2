var app = app || {};

(function(w){  
  
	w.render = function(templateName, data) {
		var tmpl = Handlebars.templates[templateName];
		if (tmpl) {
			return tmpl(data);
		} else {
			// obviously, handle this case as you think most appropriate.
			return "<small>Error: could not find template: " + templateName + "</small>";
		}
	}

})(window);

(function($) {
	
	app.render = function(templateName,data){
		data = data || {};
		return render(templateName,data);
	}
	
	// -------- Public Methods --------- //
	/**
	 * A method about use ajax to get json data
	 */
	app.getJsonData = function(url, params, failcount, pdfd) {
		var dfd = pdfd||$.Deferred();
		params = params || {};
		jQuery.ajax({
			  type : params.method ? params.method : "Post",
			  url : url,
			  async : true,
			  data : params,
			  dataType : "json"
		  }).success(function(data) {
                //console.log(data);
                //auth fail
                if (data && data.AUTH_FAILED) {

                    window.location.href = contextPath;
                    return;
                } else if (data && data.OAUTH_FAILED) {
                    //oauth fail
                    var count = failcount || 0;
                    if (count < 3) {
                        var callback = function () {
                            count++;
                            app.getJsonData(url, params, count, dfd);
                        };
                        window.showModalDialog(data.oauthUrl);
                        callback();
                    }
                    return;
                } else {
                    dfd.resolve(data);
                }

		  }).fail(function(jxhr, arg2) {
			try {
				if (jxhr.responseText) {
					console.log(" WARNING: json not well formatted, falling back to JS eval");
					var data = eval("(" + jxhr.responseText + ")");
					dfd.resolve(data);
				} else {
					throw " EXCEPTION: Cannot get content for " + url;
				}
			} catch (ex) {
				console.log(" ERROR: " + ex + " Fail parsing JSON for url: " + url + "\nContent received:\n"
				  + jxhr.responseText);
			}
		});

		return dfd.promise();
	};

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
        }

    };

    app.linkedInApi = {
        getConnections: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/linkedin/connects", param);
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
        }
    };

 	app.getFBContacts = function (opts) {
        var params = {
            method:"Get"
        };
        console.log(opts)
        return app.getJsonData(contextPath + "/fb/contacts", $.extend(params, opts||{}));
    };
    
    app.getFBFriends = function (opts) {
        var params = {
            method:"Get"
        };
        return app.getJsonData(contextPath + "/fb/friends", $.extend(params, opts||{}));
    };
    
    app.getFacebookFriendDetail = function (opts) {
        var params = {
            method:"Get"
        };
        return app.getJsonData(contextPath + "/fb/friend-detail", $.extend(params, opts||{}));
    };
    
    app.addFacebookContact = function(groupId,fbid){
        var params = {"fbid":fbid,groupId:groupId};
        return app.getJsonData(contextPath + "/fb/contact-add", params);
    };
    
    app.deleteFBContact = function(id){
        var params = {"id":id};
        return app.getJsonData(contextPath + "/fb/contact-delete", params);
    };

})(jQuery);

//handlebars plugin
(function($) {
    Handlebars.registerHelper('check', function (lvalue, operator, rvalue, options) {

        var operators, result;

        if (arguments.length < 3) {
            throw new Error("Handlerbars Helper 'compare' needs 2 parameters");
        }

        if (options === undefined) {
            options = rvalue;
            rvalue = operator;
            operator = "===";
        }

        operators = {
            '==': function (l, r) { return l == r; },
            '===': function (l, r) { return l === r; },
            '!=': function (l, r) { return l != r; },
            '!==': function (l, r) { return l !== r; },
            '<': function (l, r) { return l < r; },
            '>': function (l, r) { return l > r; },
            '<=': function (l, r) { return l <= r; },
            '>=': function (l, r) { return l >= r; },
            'typeof': function (l, r) { return typeof l == r; }
        };

        if (!operators[operator]) {
            throw new Error("Handlerbars Helper 'compare' doesn't know the operator " + operator);
        }

        result = operators[operator](lvalue, rvalue);

        if (result) {
            return options.fn(this);
        } else {
            return options.inverse(this);
        }

    });

})(jQuery);

(function ($) {
    //add format to string
    String.prototype.format = function (args) {
        if (arguments.length > 0) {
            var result = this;
            if (arguments.length == 1 && typeof (args) == "object") {
                for (var key in args) {
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
            else {
                for (var i = 0; i < arguments.length; i++) {
                    if (arguments[i] == undefined) {
                        return "";
                    }
                    else {
                        var reg = new RegExp("({[" + i + "]})", "g");
                        result = result.replace(reg, arguments[i]);
                    }
                }
            }
            return result;
        }
        else {
            return this;
        }
    };
    //add format to date
    Date.prototype.format = function(format)
    {
        /*
         * format="yyyy-MM-dd hh:mm:ss";
         */
        var o = {
            "M+" : this.getMonth() + 1,
            "d+" : this.getDate(),
            "h+" : this.getHours(),
            "m+" : this.getMinutes(),
            "s+" : this.getSeconds(),
            "q+" : Math.floor((this.getMonth() + 3) / 3),
            "S" : this.getMilliseconds()
        }

        if (/(y+)/.test(format))
        {
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4
                - RegExp.$1.length));
        }

        for (var k in o)
        {
            if (new RegExp("(" + k + ")").test(format))
            {
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                    : ("00" + o[k]).substr(("" + o[k]).length));
            }
        }
        return format;
    };
})(jQuery);