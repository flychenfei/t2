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
			"btap; .add-Calendar":function (event) {
				var view = this;
				brite.display("InputValue", ".MainScreen",
					{title:'Add a Calendar',
					fields:[{label: "Calendar Name", name: 'calendarName'},{label: "Description", name: 'description'}],callback:function(data){
							var params = {};
							params.calendarName = data.calendarName;
							params.description = data.description;
							app.liveCalendarApi.addUserCalendar(params).done(function (result) {
								if(result.success === true){
									showCalendar.call(view);
								}else{
									alert("Add calendar failed!");
								}
							});
					}});
			},
			"btap; .update-Calendar":function (event) {
				var view = this;
				var $tr = $(event.target).closest("tr");
				var calendarId = $tr.attr("data-calendarId");
				var calendarName = $tr.find(".calendarName").text();
				var description = $tr.find(".description").text();
				brite.display("InputValue", ".MainScreen",
					{title:'Update a Calendar',
					fields:[{label: "Calendar Name", name: 'calendarName', value: calendarName},{label: "Description", name: 'description', value: description}],callback:function(data){
							var params = {};
							params.calendarId = calendarId;
							params.calendarName = data.calendarName;
							params.description = data.description;
							console.log(params);
							app.liveCalendarApi.updateUserCalendar(params).done(function (result) {
								if(result.success === true){
									showCalendar.call(view);
								}else{
									alert("Update calendar failed!");
								}
							});
					}});
			},
			"btap; .delete-Calendar":function (event) {
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
			if(result.success){
				$content.empty();
				var $renderContent = $(render("tmpl-LiveCalendarLists",{calendars:result.result.data,total:result.result.data.length}));
				$content.append($renderContent);
			}else{
				$content.empty();
				$content.append("Can't get the users liveCalendar!");
			}
		});
	}

})();