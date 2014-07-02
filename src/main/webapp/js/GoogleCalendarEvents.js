;
(function ($) {

    brite.registerView("GoogleCalendarEvents",{parent:".GoogleScreen-content",emptyParent:true},{
        create: function (data, config) {
            this.search = app.googleApi.listCalendarEvents;
            return app.render("tmpl-GoogleCalendarEvents");
        },

        postDisplay: function (data, config) {
            var view = this;
            showCalendars.call(view);
        },

        events: {
        	"click;.btnAdd":function(e){
	        	brite.display("CreateCalendarEvent",null,{id:null});
	        }
        },

        docEvents: {
        	"DO_REFRESH_CALENDAR":function(){
                 var view = this;
                 showCalendars.call(view);
             },
            "DELETE_CALENDAR": function(event, extraData) {
                if (extraData && extraData.objId) {
                    app.googleApi.deleteCalendarEvent({id:extraData.objId}).done(function (extradata) {
						setTimeout((function() {
							$(document).trigger("DO_REFRESH_CONTACT");
						}), 3000); 
                    });
                }

            },
            "EDIT_CALENDAR": function(event, extraData){
                if (extraData && extraData.objId) {
                    app.googleApi.getCalendarEvent({id:extraData.objId}).done(function (data) {
                        if(data && data.result){
                            brite.display("CreateCalendarEvent", null, data.result);
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
        brite.display("DataTable", ".contacts-container", {
            dataProvider: {list: view.search},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1;
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
                {
                    text: "Location",
                    render: function (obj) {
                        return obj.location || "";
                    },
                    attrs: "style='width: 20%"
                },
                {
                    text: "Date",
                    render: function (obj) {
                        return new Date(obj.date.dateTime.value).format("yyyy-MM-dd hh:mm:ss");
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Status",
                    render: function (obj) {
                        return obj.status;
                    },
                    attrs: "style='width: 10%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not calendar found",
                withPaging: false,
                cmdDelete: "DELETE_CALENDAR",
                cmdEdit: "EDIT_CALENDAR"
            }
        });
    }
})(jQuery);