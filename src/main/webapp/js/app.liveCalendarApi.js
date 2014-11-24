var app = app || {};
(function() {
	app.liveCalendarApi = {
		getUserCalendars : function() {
			var param = {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/calendar/getUserCalendars", param);
		},
		addUserCalendar : function(param) {
			var param = param || {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/calendar/addUserCalendar", param);
		},
		updateUserCalendar : function(param) {
			var param = param || {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/calendar/updateUserCalendar", param);
		},
		deleteUserCalendar : function(param) {
			var param = param || {};
			param.method = "Get";
			return app.getJsonData(contextPath + "/live/calendar/deleteUserCalendar", param);
		}
	}
})();