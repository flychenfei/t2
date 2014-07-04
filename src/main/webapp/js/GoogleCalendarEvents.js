;
(function ($) {

    brite.registerView("GoogleCalendarEvents",{parent:".GoogleScreen-content",emptyParent:true},{
        create: function (data, config) {
            this.search = app.googleApi.listCalendarEvents;
            return app.render("tmpl-GoogleCalendarEvents");
        },

        postDisplay: function (data, config) {
            var view = this;
            var $e = view.$el;
            
            $e.find('.datetimepicker').datetimepicker({ 
                format: 'yyyy-MM-dd', 
                language: 'en', 
                 pickDate: true, 
                 pickTime: true, 
                 inputMask: true 
            });
            showCalendars.call(view);
        },

        events: {
        	"click;.btnAdd":function(e){
	        	brite.display("CreateCalendarEvent",null,{id:null});
	        },
	        "click;.searchCalendar":function(){
	        	view = this;
	        	$e = view.$el;
	        	var result = {};
	        	$e.find(".search-calendars-container :text").each(function(){
	        		if($(this).val() != ""){
	        			result[$(this).attr("name")] = $(this).val();
	        		}
	        	});
	        	view.search = function(opts){
	        		opts = opts || [];
	        		$.extend(opts,result);
	        		return app.googleApi.listCalendarEvents(opts);
	        	};
	        	showCalendars.call(view);
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
							$(document).trigger("DO_REFRESH_CALENDAR");
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
        brite.display("DataTable", ".calendars-container", {
            dataProvider: {list: view.search},
            columnDef: [
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
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Date",
                    render: function (obj) {
                    	if(obj.date){
	                        return new Date(obj.date.dateTime.value).format("yyyy-MM-dd hh:mm:ss");
                    	}
                    	return "";
                    },
                    attrs: "style='width: 15%'"
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