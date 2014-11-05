;
(function ($) {
    brite.registerView("LinkedInCompanys",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInCompanys");
        },

        postDisplay: function (data, config) {
        	var view = this;
        	showFollowedCompanys.call(this);
        },
        events:{
        	"btap; .followed-companys":function () {
                var view = this;
                showFollowedCompanys.call(this);
        	},
        	"btap; .suggests-followed-companys":function () {
                var view = this;
                showSuggestsFollowedCompanys.call(this);
        	},
        	"btap; .StartFollowing":function (e) {
                var view = this;
                var param = {};
                param.companyId = $(e.target).closest("tr").attr("data-companyId");
                app.linkedInApi.StartFollowingCompany(param).done(function (result) {
                    if(result.success === true){
                        showSuggestsFollowedCompanys.call(this);
                    }else{
                    	alert("Following failed!");
                    }
                });
        	}
        	
        }
    });
    
	//----------------------- private method --------------------
    function showFollowedCompanys() {
        var view = this;
        brite.display("DataTable", ".Companys-list",{
            dataProvider: {list: function(params){
               return app.linkedInApi.followedCompanys(params);
            }},
            rowAttrs: function (obj) {
                return "data-companyId='{0}'".format(obj.id)
            },
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
                            attrs: "style='width: 10%'"

                        },
                        {
                            text: "UniversalName",
                            render: function (obj) {
                                return obj.universalName;
                            },
                            attrs: "style='width: 10%'"

                        },
                        {
                            text: "Website",
                            render: function (obj) {
                                return "<a href=\"{0}\" target=\"_blank\" class=\"fileSelf\">{1}</a>".format(obj.websiteUrl,obj.websiteUrl);
                            },
                            attrs: "style='width: 10%; word-break: break-word; white-space: normal;'"
                        },
                        {
                            text: "Location",
                            render: function (obj) {
                            	var locations = obj.locations;
                            	if(locations && locations.values && locations.values.length > 0){
                            		var value = locations.values[0];
                            		return "<span>{0}-{1}(postalCode:{2})</span>".format(value.address.city,value.address.street1,value.address.postalCode);
                            	}else{
                            		return "NONE";
                            	}
                            },
                            attrs: "style='width: 10%; word-break: break-word; white-space: normal;'"
                        },
                        {
                            text: "Description",
                            render: function (obj) {
                                return obj.description;
                            },
                            attrs: "style='width: 40%'"

                        },
                        {
                            text: "Followers Number",
                            render: function (obj) {
                                return obj.numFollowers;
                            },
                            attrs: "style='width: 5%'"
                        }
                    ],
            opts: {
                htmlIfEmpty: "Not Companys found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    
    function showSuggestsFollowedCompanys() {
        var view = this;
        brite.display("DataTable", ".Companys-list",{
            dataProvider: {list: function(params){
               return app.linkedInApi.suggestsFollowedCompanys(params);
            }},
            rowAttrs: function (obj) {
                return "data-companyId='{0}'".format(obj.id)
            },
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
                            attrs: "style='width: 10%'"

                        },
                        {
                            text: "UniversalName",
                            render: function (obj) {
                                return obj.universalName;
                            },
                            attrs: "style='width: 10%'"
                        },
                        {
                            text: "Website",
                            render: function (obj) {
                                return "<a href=\"{0}\" target=\"_blank\" class=\"fileSelf\">{1}</a>".format(obj.websiteUrl,obj.websiteUrl);
                            },
                            attrs: "style='width: 10%; word-break: break-word; white-space: normal;'"
                        },
                        {
                            text: "Location",
                            render: function (obj) {
                            	var locations = obj.locations;
                            	if(locations && locations.values && locations.values.length > 0){
                            		var value = locations.values[0];
                            		return "<span>{0}-{1}(postalCode:{2})</span>".format(value.address.city,value.address.street1,value.address.postalCode);
                            	}else{
                            		return "NONE";
                            	}
                            },
                            attrs: "style='width: 10%; word-break: break-word; white-space: normal;'"
                        },
                        {
                            text: "Description",
                            render: function (obj) {
                                return obj.description;
                            },
                            attrs: "style='width: 40%; word-break: break-word; white-space: normal;'"
                        },
                        {
                            text: "Followers Number",
                            render: function (obj) {
                                return obj.numFollowers;
                            },
                            attrs: "style='width: 5%'"
                        },
                        {
                            text: "Operator",
                            render: function (obj) {
                            	var functionString = "<span><a src=\"#\" class=\"StartFollowing\">Start Following</a></span>";
                                return functionString;
                            },
                            attrs: "style='width: 10%; cursor:pointer;'"
                        }
                    ],
            opts: {
                htmlIfEmpty: "Not Companys found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    
    //----------------------- /private method --------------------
    
})(jQuery);