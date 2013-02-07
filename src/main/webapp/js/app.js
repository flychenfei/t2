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
                console.log(data);
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
                	console.log(data)
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
	        }
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
        }

    };
    
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
		}
    }

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

    app.foursquareApi = {
        getUserInfo: function(){
            var param = {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/foursquare/getUserInfo", param);
        }
    }

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
    
    app.addPost = function(value){
        var params = {"value":value};
        return app.getJsonData(contextPath + "/fb/post-add", params);
    };
    
    app.deleteFBContact = function(id){
        var params = {"id":id};
        return app.getJsonData(contextPath + "/fb/contact-delete", params);
    };
    app.getFBPosts = function(opts){
    	var params = {
            method:"Get"
        };
        return app.getJsonData(contextPath + "/fb/posts", $.extend(params, opts||{}));
    };

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


//get pagination object for some list data
(function($){
  
  var _pageNum = 1;
  var _pageSize = 25;
  
  var _dataList,_sizeCount,_pageCount;
  
  // --------- app.Pagination Constructor --------- //
  function Pagination(count,list,pageSize){
      //init private members
      _dataList = [].concat(list);
      _sizeCount = count;
      if(typeof pageSize != 'undefined'){
        _pageSize = pageSize;
      }
      _pageCount = getPageCount();
      return this;
  }
  app.Pagination = Pagination; 
  // --------- /app.Pagination Constructor --------- //

  // --------- Public Methods --------- //
  Pagination.prototype.getList = function(){
    return _dataList;
  }
  Pagination.prototype.getPageInfo = function(){
    return this.go(_pageNum);
  }
  Pagination.prototype.setPageSize = function(pageSize){
    if(typeof pageSize != "undefined"){
      _pageSize = pageSize;
      _pageCount = getPageCount();
    }
  }
  Pagination.prototype.go = function(pageNum){
    if(pageNum < 1){
      pageNum = 1;
    }
    var pageSizeNum = _pageSize;
    if(_pageSize == "all"){
      pageSizeNum = _sizeCount;
    }
    var startRows = ( pageNum-1 ) * pageSizeNum + 1;
    var endRows = pageNum * pageSizeNum;
    if(_sizeCount == 0){
      startRows = 0;
      endRows = 0;
    }else{
      if(startRows > _sizeCount){
        startRows = ( _pageCount-1 ) * pageSizeNum + 1; 
        endRows = _sizeCount;
        _pageNum = _pageCount;
      }else if(startRows <= _sizeCount && endRows > _sizeCount){
        endRows = _sizeCount;
        _pageNum = _pageCount;
      }else{
        _pageNum = pageNum;
      }
    }
    
    //var subList = [];
    //for(var i = startRows-1 ; i < endRows;i++){
      //subList.push(_dataList[i]);
    //}

    var pageCount = getPageCount();
    var pageInfo = {
        pageNum:_pageNum,
        pageSize:_pageSize,
        sizeCount:_sizeCount,
        pageCount:pageCount,
        pageList:_dataList,
        startRows:startRows,
        endRows:endRows,
        isFirst:isFirst(),
        isLast:isLast(),
        getArrayFrom1To6:getArrayFromMToN(1,6),
        getArrayFrom1ToPC:getArrayFromMToN(1,pageCount),
        getArrayFromPr2ToPa3:getArrayFromMToN(_pageNum-2,_pageNum+3),
        getArrayFromPCr6ToPC:getArrayFromMToN(pageCount-6,pageCount)
      }

    return pageInfo;
  }
  Pagination.prototype.next = function(){
    return this.go(_pageNum + 1);
  }
  Pagination.prototype.prev = function(){
    return this.go(_pageNum - 1);
  }
  Pagination.prototype.goFirst = function(){
    return this.go(1);
  }
  Pagination.prototype.goLast = function(){
    return this.go(_pageCount);
  }
  // --------- /Public Methods --------- //
  
  function isFirst(){
    if(_pageNum == 1){
      return true;
    }
    return false;
  }
  
  function isLast(){
    if(_pageNum == _pageCount){
      return true;
    }
    return false;
  }
  
  function getPageCount(){
    var pageSizeNum = _pageSize;
    if(_pageSize == "all"){
      pageSizeNum = _sizeCount;
    }
    _pageCount = Math.ceil(_sizeCount / pageSizeNum);
    return _pageCount;
  }
  
  
  
  //get a array from m to n, m and n must be number. m must be smaller than or equal n
  function getArrayFromMToN(m,n){
    var arr = [];
    if(typeof m == 'number' && typeof n == 'number' && m <= n){
      for(var i=m; i<=n ; i++){
        arr.push({num:i});
      }
      return arr;
    }
    return arr;
  };
})(jQuery);