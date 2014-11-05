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
                }else if(menu == "companys"){
                	brite.display("LinkedInCompanys");
                }
              },
              "click;.removebookmark":function(e){
            	  var view = $(this);
            	  var $e = (e.currentTarget);
            	  var param = {};
            	  param.id = $(e.currentTarget).attr("id");
            	  app.linkedInApi.removebookmark(param).done(function (result) {
            		  $(e.currentTarget).html("Save As Bookmark");
            		  $(e.currentTarget).removeClass("removebookmark");
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
            		  $(e.currentTarget).addClass("removebookmark");
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
                            	searchJobs.call(this, keywork.name);
                            }});
                            break;
                        case "searchCompany":
                            brite.display("InputValue", ".MainScreen",{title:'Search Company',callback:function(keywork){
                            	searchCompanys.call(this, keywork.name);
                            }});
                            break;
                        case "searchPeople":
                            brite.display("InputValue", ".MainScreen",{title:'Search People',callback:function(keywork){
                            	searchPeoples.call(this, keywork.name);
                            }});
                            break;
                        default:
                    }
                }
            },

            daoEvents:{
            }
            
        });
        
    //----------------------- private method --------------------
    function searchJobs(keywork) {
    	var view = this;
        brite.display("DataTable", ".LinkedInScreen-content",{
            dataProvider: {list: function(params){
                params.keywork = keywork.name;
                return app.linkedInApi.searchJobs(params);
            }},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1;
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "Company",
                    render: function (obj) {
                        return obj.company.name;
                    },
                    attrs: "style='width: 20%'"

                },
                {
                    text: "Description",
                    render: function (obj) {
                        return obj.descriptionSnippet;
                    },
                    attrs: "style='width: 50%'"
                },
                {
                    text: "Location",
                    render: function (obj) {
                        return obj.locationDescription || "";
                    }
                },
                {
                    text: "Action",
                    render: function (obj) {
                    	return "<a href='#'><div class='"+obj.check+"' id=\""+obj.id+"\">"+obj.mark+"</div></a>";
                    },
                    attrs: "style='width: 15%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not Jobs found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    
    function searchCompanys(keywork) {
        var view = this;
        brite.display("DataTable", ".LinkedInScreen-content",{
            dataProvider: {list: function(params){
                params.keywork = keywork.name;
               return app.linkedInApi.searchCompanys(params);
            }},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.name;
                    },
                    attrs: "style='width: 20%'"

                }
            ],
            opts: {
                htmlIfEmpty: "Not Companys found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    
    function searchPeoples(keywork) {
        var view = this;
        brite.display("DataTable", ".LinkedInScreen-content",{
            dataProvider: {list: function(params){
                params.keywork = keywork.name;
                return app.linkedInApi.searchPeoples(params);
            }},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "First Name",
                    render: function (obj) {
                        return obj.firstName;
                    },
                    attrs: "style='width: 40%'"

                },
                {
                    text: "Last Name",
                    render: function (obj) {
                        return obj.lastName;
                    },
                    attrs: "style='width: 45%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not Peoples found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    //----------------------- /private method --------------------
    
    })(jQuery);
})();
