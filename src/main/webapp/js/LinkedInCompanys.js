;
(function ($) {
    brite.registerView("LinkedInCompanys",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInCompanys");
        },

        postDisplay: function (data, config) {
        	var view = this;
        	showCompanys.call(this, data.keywork);
        }
    });
    
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
    
})(jQuery);