;
(function ($) {
    brite.registerView("LinkedInPeoples",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInPeoples");
        },

        postDisplay: function (data, config) {
        	var view = this;
        	showPeoples.call(this, data.keywork);
        }
    });
    
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
})(jQuery);