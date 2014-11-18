(function(){
	
	brite.registerView("LiveCalendar",{emptyParent:true, parent:".LiveScreen-content"},{

		create: function(){
			return showCalendar.call(this);
		}
		
	});

	function showCalendar(){
		var dfd = $.Deferred();
		app.liveCalendarApi.getUserCalendars().done(function(result){
			console.log(result.result.data);
			if(result.success){
				var html =  app.render("tmpl-LiveCalendar",{calendars:result.result.data,total:result.result.data.length});
				dfd.resolve(html);
			}
		});
		return dfd.promise();
	}
})();