;
(function ($) {
    brite.registerView("LinkedInConnections",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInConnections");
        },

        postDisplay: function (data, config) {
            var view = this;
            showConnections.call(view);
        },

        events: {
        	
        },
        docEvents: {
           
        },
        daoEvents: {
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
                },
                {
                    text: "Info",
                    render: function(){
                        return "<div class='glyphicon glyphicon-user'/>"
                    },
                    attrs: "style='width:50px;text-align:center;cursor:pointer'  data-cmd='USER_INFO'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not Connections found",
                withPaging: true,
                cmdDelete: "DELETE_CONNECTIONS"
            }
        });
    }

    
})(jQuery);