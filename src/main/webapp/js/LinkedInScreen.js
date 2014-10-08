;(function() {

	/**
	 * View: LinkedInScreen
	 *
	 */
    (function ($) {
        brite.registerView("LinkedInScreen",  {parent:".MainScreen-main", emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-LinkedInScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                app.linkedInApi.getCurrentUserInfo().done(function (result) {
            		brite.display("LinkedInCurrentUserInfo", ".LinkedInScreen-content", {result:result.result});
            	});
            },
            events:{
              "btap;.nav li":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                $e.find("li").removeClass("active");
                $li.addClass("active");
                
                var menu = $li.attr("data-nav");
                if(menu == "currentUserInfo"){
                	app.linkedInApi.getCurrentUserInfo().done(function (result) {
                		brite.display("LinkedInCurrentUserInfo", ".LinkedInScreen-content", {result:result.result});
                	});
                }else if(menu == "jobbookmarks"){
                  brite.display("LinkedInJobBookmarks");
                }else if(menu == "connections"){
                  brite.display("LinkedInConnections");
                }else if(menu == "search"){
                    var list = [
                        {name:"searchJobs",label:"Search Jobs"},
                        {name:"searchCompany",label:"Search Company"},
                        {name:"searchPeople",label:"Search People"}
                    ];
                    brite.display("Dropdown",null,{$target:$li,list:list});
                    $li.find("i").removeClass("glyphicon glyphicon-chevron-down").addClass("glyphicon glyphicon-chevron-up");
                }else if(menu == "groups"){
                	brite.display("LinkedInGroups");
                }
              },
              "click;.details":function(e){
            	  var view = this;
            	  var $detail = $(e.target);
            	  var param = {};
            	  param.groupId = $detail.closest("tr").attr("data-groupId");
            	  app.linkedInApi.groupDetails(param).done(function (result) {
	                    if(result.success === true){
	                    	 brite.display("LinkedInGroupDetails", ".LinkedInScreen-content", {result:result.result});
	                    }else{
	                    	alert("can't get the details of group!");
	                    }
	                });
              },
              "click;.leave":function(e){
            	  var view = this;
            	  var $detail = $(e.target);
            	  var param = {};
            	  param.groupId = $detail.closest("tr").attr("data-groupId");
            	  app.linkedInApi.leaveGroup(param).done(function (result) {
	                    if(result.success === true){
	                    	alert("Leave group success!");
	                    }else{
	                    	alert("Leave group fail!");
	                    }
	                    showGroups.call(view);
	                });
              },
              "click;.removebookmark":function(e){
            	  var view = $(this);
            	  var $e = (e.currentTarget);
            	  var param = {};
            	  param.id = $(e.currentTarget).attr("id");
            	  app.linkedInApi.removebookmark(param).done(function (result) {
            		  brite.display("LinkedInJobBookmarks");
	                });
              },
              "click;.bookmark":function(e){
            	  var view = $(this);
            	  var $e = (e.currentTarget);
            	  var param = {};
            	  param.id = $(e.currentTarget).attr("id");
            	  app.linkedInApi.removebookmark(param).done(function (result) {
            		  $(e.currentTarget).html("Save As Bookmark");
            		  $(e.currentTarget).removeClass("bookmark");
            		  $(e.currentTarget).addClass("addbookmark");
	                });
              },
              "click;.addbookmark":function(e){
            	  var view = this;
            	  var param = {};
            	  param.id = $(e.currentTarget).attr("id");
            	  app.linkedInApi.addbookmark(param).done(function (result) {
            		  $(e.currentTarget).html("Remove Bookmark");
            		  $(e.currentTarget).removeClass("addbookmark");
            		  $(e.currentTarget).addClass("bookmark");
	                });
              },
              "click;.glyphicon-user":function(e){
            	  var view = this;
            	  var $detail = $(e.target);
            	  var param = {};
            	  param.userId = $detail.closest("tr").attr("data-obj_id");
            	  app.linkedInApi.userInfo(param).done(function (result) {
            		  brite.display("LinkedInUserInfo", ".MainScreen", {title:"Show UserInfo", result:result.result});
	                });
              }
            },

            docEvents:{
                "DO_ON_DROPDOWN_CLOSE":function(){
                    var view = this;
                    var $e = view.$el;
                    var $li = $e.find("li[data-nav='search']");
                    $li.find("i").removeClass("glyphicon glyphicon-chevron-up").addClass("glyphicon glyphicon-chevron-down");
                },
                "DO_ON_DROP_DOWN_CLICK":function(event, name) {
                    var view = this;
                    switch (name) {
                        case "searchJobs":
                            brite.display("InputValue",".MainScreen", {title:'Search Job',callback:function(keywork){
                            	brite.display("LinkedInJobs",".LinkedInScreen-content",{keywork:keywork.name});
                            }});
                            break;
                        case "searchCompany":
                            brite.display("InputValue", ".MainScreen",{title:'Search Company',callback:function(keywork){
                            	brite.display("LinkedInCompanys",".LinkedInScreen-content",{keywork:keywork.name});
                            }});
                            break;
                        case "searchPeople":
                            brite.display("InputValue", ".MainScreen",{title:'Search People',callback:function(keywork){
                            	brite.display("LinkedInPeoples",".LinkedInScreen-content",{keywork:keywork.name});
                            }});
                            break;
                        default:
                    }
                }
            },

            daoEvents:{
            }
            
        });
    })(jQuery);
})();
