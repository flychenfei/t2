;
(function ($) {
    brite.registerView("LinkedInJobs",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInJobs");
        },

        postDisplay: function (data, config) {
        	showJobs.call(this, data.keywork);
        }
    });
    
    function showJobs(keywork) {
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
    
})(jQuery);