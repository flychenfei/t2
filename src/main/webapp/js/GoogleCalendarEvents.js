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

            /***********************************init date Component******************************************************/
            $e.find(".datetimepicker").datetimepicker({
                format: 'yyyy-MM-dd',
                language: 'en',
                pickDate: true,
                pickTime: true,
                inputMask: true
            });
            /***********************************init date Component******************************************************/

            showCalendarEvents.call(view);

			$calendar = $e.find(".calendar");
			app.googleApi.listCalendars().done(function(data) {
				for (var i = 0; i < data.result.length; i++) {
					var id = data.result[i].id;
					var value = data.result[i].summary;
					var selected = "";
					if (data.result[i].primary) {
						selected = "selected";
					}
					$calendar.append("<option value='" + id + "' " + selected + ">" + value + "</option>");
				}
			});

        },

        events: {
            "btap; .datetimepicker": function(event){
                $(event.currentTarget).datetimepicker('show');
            },

        	"click;.btnAdd":function(e){
	        	brite.display("CreateCalendarEvent",null,{id:null});
	        },
	        "click;.showCanlendarView":function(){
	        	brite.display("GoogleCalendarView");
	        },

	        "click;.searchCalendar":function(){
	        	view = this;
	        	$e = view.$el;
	        	var result = {};

                var startDate = $e.find("input[name='startDate']").val();
                var endDate = $e.find("input[name='endDate']").val();
                if(app.dateformat.dateDiff('d',app.dateformat.strFormatToDate('yyyy-MM-dd',endDate),app.dateformat.strFormatToDate('yyyy-MM-dd',startDate)) > 0){
                    alert("The start date should be before the end date");
                    return false;
                }

	        	$e.find(".search-calendars-container :text").each(function(){
	        		if($(this).val() != ""){
	        			result[$(this).attr("name")] = $(this).val();
	        		}
	        	});
	        	result.calendarId = $e.find(".calendar").val();
	        	view.search = function(opts){
	        		opts = opts || [];
	        		$.extend(opts,result);
	        		return app.googleApi.listCalendarEvents(opts);
	        	};
	        	showCalendarEvents.call(view);
	        },
	        "btap; .editEvent": function(event){
	        	var view = this;
	        	var $btn = $(event.currentTarget);
	        	var $tr = $btn.closest("tr");
	        	var id = $tr.attr("data-obj_id");
	        	var calendarId = $tr.attr("data-calendarId");
                if (id) {
                    app.googleApi.getCalendarEvent({id:id, calendarId:calendarId}).done(function (data) {
                        if(data && data.result){
                            brite.display("CreateCalendarEvent", null, data.result);
                        }
                    });
                }
           },
           "btap; .deleteEvent": function(event, extraData) {
               if(confirm("Are you sure to delete it?")){
                   var $btn = $(event.currentTarget);
                   var $tr = $btn.closest("tr");
                   var id = $tr.attr("data-obj_id");
                   var calendarId = $tr.attr("data-calendarId");
                   if (id) {
                       app.googleApi.deleteCalendarEvent({id:id, calendarId:calendarId}).done(function (data) {
                           $(document).trigger("DO_REFRESH_CALENDAR_EVENT");
                       });
                   }
               }
            },
	        "btap; .copyEvent": function(event){
	        	var view = this;
	        	var $btn = $(event.currentTarget);
	        	var $tr = $btn.closest("tr");
	        	var id = $tr.attr("data-obj_id");
	        	var calendarId = $tr.attr("data-calendarId");
                if (id) {
                    app.googleApi.getCalendarEvent({id:id, calendarId:calendarId}).done(function (data) {
                        if(data && data.result){
                        	data.result.copy = true;
                            brite.display("CopyCalendarEvent", null, data.result);
                        }
                    });
                }
           },            
        },

        docEvents: {
        	"DO_REFRESH_CALENDAR_EVENT":function(){
                 var view = this;
                 showCalendarEvents.call(view);
             }
            
            
        },

        daoEvents: {
        }
    });

    function showCalendarEvents() {
        var view = this;
        brite.display("DataTable", ".calendars-container", {
            dataProvider: {list: view.search},
            rowAttrs: function(obj){ return " data-calendarId='{0}'".format(obj.calendarId);},
            columnDef: [
                {
                    text: "Summary",
                    render: function (obj) {
                        return obj.summary || '';
                    },
                    attrs: "style='width: 300'"

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
                    	if(obj.date && obj.date.dateTime){
	                        return new Date(obj.date.dateTime.value).format("yyyy-MM-dd");
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
                },
                {
                    text:"",
                    render:function(obj){
                    	return "<div class='glyphicon glyphicon-edit editEvent hander'title='edit'></div>";
                    },
					attrs: "style='width: 10%'"
                },
                {
                    text:"",
                    render:function(obj){
                    	return "<div class='glyphicon glyphicon-remove deleteEvent hander' data-cmd='DELETE_CALENDAR' title='delete'></div>";
                    },
					attrs: "style='width: 10%'"
                },
                {
                    text:"",
                    render:function(obj){
                    	return "<div class='copyEvent hander'title='copy'>Copy</div>";
                    },
					attrs: "style='width: 10%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not calendar found",
                withPaging: true,
                withCmdEdit:false,
                withCmdDelete:false,
                dataOpts:{
                	withResultCount:false
                }
            },
            showLoading:true
        });
    }
    
})(jQuery);