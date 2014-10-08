;
(function ($) {
    brite.registerView("LinkedInGroups",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInGroups");
        },

        postDisplay: function (data, config) {
            var view = this;
            showGroups.call(view);
        },

        events: {
        	
        },
        docEvents: {
           
        },
        daoEvents: {
        }
    });
    
    function showGroups() {
        brite.display("DataTable", ".LinkedInScreen-content",{
            dataProvider: {list: app.linkedInApi.getGroups},
            rowAttrs: function (obj) {
                return "data-groupId='{0}'".format(obj.group.id)
            },
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "GroupId",
                    render: function (obj) {
                    	return obj.group.id;
                    },
                    attrs: "style='width: 15%'"
                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.group.name;
                    },
                    attrs: "style='width: 20%'"

                },
                {
                    text: "MembershipState",
                    render: function (obj) {
                        return obj.membershipState.code;
                    },
                    attrs: "style='width: 15%'"

                },
                {
                    text:"Operator",
                    attrs: "style='width:40%; word-break: break-word; cursor:pointer;'",
                    render: function (obj) {
                    	var functionString = "<span><a src=\"#\" class=\"details\">"+"details"+"</a> </span><span><a src=\"#\" class=\"leave\">"+"leave"+"</a> </span>";
                        return functionString;
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not Groups found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    
})(jQuery);