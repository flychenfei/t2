(function(){
	
	brite.registerView("LiveCalendar",{emptyParent:true, parent:".LiveScreen-content"},{

		create: function(){
			return app.render("tmpl-LiveCalendar");
		},
		postDisplay: function () {
        	var view = this;
            showCalendar.call(view);
        },
		events:{
        	"btap; .deleteCalendar":function (event) {
                var view = this;
                var param = {};
                param.calendarId = $(event.target).closest("tr").attr("data-calendarId");
        		app.liveCalendarApi.deleteUserCalendar(param).done(function(result){
        			if(result.success){
						showCalendar.call(view);
					}else{
						alert("Delete user calendar failed!");
					}
        		});
        	}
        }
	});

	function showCalendar(){
		var view = this;
		app.liveCalendarApi.getUserCalendars().done(function(result){
			var $content = view.$el.find(".LiveCalendar-body");
			console.log($content.get(0));
			console.log(result.result.data);
			if(result.success){
				$content.empty();
				var $renderContent = $(render("tmpl-LiveCalendarLists",{calendars:result.result.data,total:result.result.data.length}));
				console.log($renderContent);
				$content.append($renderContent);
			}else{
				$content.empty();
				$content.append("Can't get the users liveCalendar!");
			}
		});
	}

})();