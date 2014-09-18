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
        	"click;.btnSetting":function(e){
	        	brite.display("GoogleCalendarSetting",null,{id:null});
	        },	        
        	"click;.shareCalendar":function(event){
	        	var $btn = $(event.currentTarget);
	        	var $tr = $btn.closest("tr");
	        	var calendarId = $tr.attr("data-obj_id");
	        	
            	brite.display("ShareCalendar", null, calendarId);
	        }
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
                {
                    text:"",
                    render:function(obj){
                    	return (obj.accessRole == "reader" || obj.primary) ? "" : "<div class='glyphicon glyphicon-edit' data-cmd='EDIT_CALENDAR'></div>";
                    },
					attrs: "style='width: 40px'"
                },
                {
                    text:"",
                    render:function(obj){
                    	return (obj.accessRole == "reader" || obj.primary) ? "" : "<div class='glyphicon glyphicon-remove' data-cmd='DELETE_CALENDAR'></div>";
                    },
					attrs: "style='width: 40px'"
                },
                {
                    text:"",
                    render:function(obj){
                    	return "<div class='glyphicon glyphicon-share shareCalendar' style='cursor:pointer'></div>";
                    },
					attrs: "style='width: 40px'"
                }               
            ],
            opts: {
                htmlIfEmpty: "Not calendar found",
                withPaging: true,
                withCmdEdit:false,
                withCmdDelete: false,
                dataOpts:{
                	withResultCount:false
                }
            }
        });
    }
    
})(jQuery);