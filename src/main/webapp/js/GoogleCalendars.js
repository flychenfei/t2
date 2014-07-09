;
(function ($) {

    brite.registerView("GoogleCalendars",{parent:".GoogleScreen-content",emptyParent:true},{
        create: function (data, config) {
            this.search = app.googleApi.listCalendars;
            return app.render("tmpl-GoogleCalendars");
        },

        postDisplay: function (data, config) {
            var view = this;
            var $e = view.$el;
            
            showCalendars.call(view);
        },

        events: {
        	"click;.btnAdd":function(e){
	        	brite.display("CreateCalendar",null,{id:null});
	        },
        },

        docEvents: {
        	"DO_REFRESH_CALENDAR":function(){
                 var view = this;
                 showCalendars.call(view);
             },
            "DELETE_CALENDAR": function(event, extraData) {
                if (extraData && extraData.objId) {
                    app.googleApi.deleteCalendars({id:extraData.objId}).done(function (extradata) {
						setTimeout((function() {
							$(document).trigger("DO_REFRESH_CALENDAR");
						}), 3000); 
                    });
                }

            },
            "EDIT_CALENDAR": function(event, extraData){
                if (extraData && extraData.objId) {
                    app.googleApi.getCalendars({id:extraData.objId}).done(function (data) {
                        if(data && data.result){
                            brite.display("CreateCalendar", null, data.result);
                        }
                    });
                }
            }
        },

        daoEvents: {
        }
    });

    function showCalendars() {
        var view = this;
        brite.display("DataTable", ".calendars-container", {
            dataProvider: {list: view.search},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Summary",
                    render: function (obj) {
                        return obj.summary;
                    },
                    attrs: "style='width: 300px'"

                },
               
            ],
            opts: {
                htmlIfEmpty: "Not calendar found",
                withPaging: true,
                cmdDelete: "DELETE_CALENDAR",
                cmdEdit: "EDIT_CALENDAR",
                dataOpts:{
                	withResultCount:false
                }
            }
        });
    }
    
})(jQuery);