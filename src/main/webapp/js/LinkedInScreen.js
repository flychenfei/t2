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
                showConnections.call(view);
            },
            events:{
              "btap;.nav li":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                $e.find("li").removeClass("active");
                $li.addClass("active");
                
                var menu = $li.attr("data-nav");
                if(menu == "connections"){
                  showConnections.call(view);
                }else if(menu == "search"){
                    var list = [
                        {name:"searchJobs",label:"Search Jobs"},
                        {name:"searchCompany",label:"Search Company"},
                        {name:"searchPeople",label:"Search People"}
                    ];
                    brite.display("Dropdown",null,{$target:$li,list:list});
                    $li.find("i").removeClass("icon-chevron-down").addClass("icon-chevron-up");
                }

              }
            },

            docEvents:{
                "DO_ON_DROPDOWN_CLOSE":function(){
                    var view = this;
                    var $e = view.$el;
                    var $li = $e.find("li[data-nav='search']");
                    $li.find("i").removeClass("icon-chevron-up").addClass("icon-chevron-down");
                },
                "DO_ON_DROP_DOWN_CLICK":function(event, name) {
                    var view = this;
                    switch (name) {
                        case "searchJobs":
                            brite.display("InputValue",".MainScreen", {title:'Search Job',callback:function(keywork){
                                showJobs(keywork);
                            }});
                            break;
                        case "searchCompany":
                            brite.display("InputValue", ".MainScreen",{title:'Search Company',callback:function(keywork){
                                showCompanys(keywork);
                            }});
                        case "searchPeople":
                            brite.display("InputValue", ".MainScreen",{title:'Search People',callback:function(keywork){
                                showPeoples(keywork);
                            }});
                        default:
                    }
                }
            },

            daoEvents:{
            }
            
        });
        function showConnections() {
            brite.display("DataTable", ".LinkedInScreen-content",{
                dataProvider: {list: app.linkedInApi.getConnections},
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
                        attrs: "style='width: 20%'"

                    },
                    {
                        text: "Last Name",
                        render: function (obj) {
                            return obj.lastName
                        },
                        attrs: "style='width: 25%'"
                    },
                    {
                        text: "Industry",
                        render: function (obj) {
                            return obj.industry || "";
                        }
                    }
                ],
                opts: {
                    htmlIfEmpty: "Not Connections found",
                    withPaging: true,
                    cmdDelete: "DELETE_CONNECTIONS"
                }
            });
        }

        function showJobs(keywork) {
            brite.display("DataTable", ".LinkedInScreen-content",{
                dataProvider: {list: function(params){
                    params.keywork = keywork.name;
                    return app.linkedInApi.searchJobs(params);
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
                    }
                ],
                opts: {
                    htmlIfEmpty: "Not Jobs found",
                    withPaging: true,
                    withCmdDelete:false
                }
            });
        }

        function showCompanys(keywork) {
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

        function showPeoples(keywork) {
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
                        attrs: "style='width: 30%'"

                    },
                    {
                        text: "Last Name",
                        render: function (obj) {
                            return obj.lastName;
                        }

                    }
                ],
                opts: {
                    htmlIfEmpty: "Not Peoples found",
                    withPaging: true,
                    withCmdDelete:false
                }
            });
        }
    })(jQuery);


})();
