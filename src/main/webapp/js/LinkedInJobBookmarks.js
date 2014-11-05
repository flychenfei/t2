;
(function ($) {
    brite.registerView("LinkedInJobBookmarks",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInJobBookmarks");
        },

        postDisplay: function (data, config) {
            var view = this;
            showJobBookmarks.call(view);
        },

        events: {
        	"click;.removebookmark":function(e){
          	  var view = $(this);
          	  var $e = (e.currentTarget);
          	  var param = {};
          	  param.id = $(e.currentTarget).attr("id");
          	  app.linkedInApi.removebookmark(param).done(function (result) {
          		  brite.display("LinkedInJobBookmarks");
	                });
            }
        },
        docEvents: {
           
        },
        daoEvents: {
        }
    });
    
    function showJobBookmarks() {
    	brite.display("DataTable", ".LinkedInJobBookmarks",{
            dataProvider: {list: app.linkedInApi.getJobBookmarks},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "company",
                    render: function (obj) {
                        return obj.job.company.name;
                    },
                    attrs: "style='width: 20%'"

                },
                {
                    text: "Description",
                    render: function (obj) {
                        return obj.job.description
                    },
                    attrs: "style='width: 45%'"
                },
                {
                    text: "Location",
                    render: function (obj) {
                        return obj.job.locationDescription
                    }
                },
                {
                    text: "Action",
                    render: function (obj) {
                        return "<a href='#'><div class='removebookmark' id=\""+obj.job.id+"\">Remove Bookmark</div></a>"
                    },
                    attrs: "style='width: 15%' ata-cmd='REMOVE_BOOKMARK'"
                },
            ],
            opts: {
                htmlIfEmpty: "Not Job Bookmarks found",
                withPaging: true,
                withCmdDelete:false,
            }
        });
    }
    
})(jQuery);