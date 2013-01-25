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
                }else if(menu == "jobs"){
                    showJobs.call(view);
                }else if(menu == "companys"){
                    showCompanys.call(view);
                }

              }
            },

            docEvents:{
            },

            daoEvents:{
            }
            
        });
        function showConnections() {
            var view = this;
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
            var view = this;
            brite.display("DataTable", ".LinkedInScreen-content",{
                dataProvider: {list: app.linkedInApi.searchJobs},
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
                dataProvider: {list: app.linkedInApi.searchCompanys},
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
    })(jQuery);


})();
