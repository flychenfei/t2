var app = app || {};
(function($) {

	app.dateformat = {};

	app.dateformat.dateDiff = function(strInterval, dtStart, dtEnd) {
		switch (strInterval) {
			case 's' :return parseInt((dtEnd - dtStart) / 1000);
			case 'n' :return parseInt((dtEnd - dtStart) / 60000);
			case 'h' :return parseInt((dtEnd - dtStart) / 3600000);
			case 'd' :return parseInt((dtEnd - dtStart) / 86400000);
			case 'w' :return parseInt((dtEnd - dtStart) / (86400000 * 7));
			case 'm' :return (dtEnd.getMonth()+1)+((dtEnd.getFullYear()-dtStart.getFullYear())*12) - (dtStart.getMonth()+1);
			case 'y' :return dtEnd.getFullYear() - dtStart.getFullYear();
		}
	}

	app.dateformat.strFormatToDate = function(formatStr, dateStr){
		var year = 0;
		var start = -1;
		var len = dateStr.length;
		if((start = formatStr.indexOf('yyyy')) > -1 && start < len){
			year = dateStr.substr(start, 4);
		}
		var month = 0;
		if((start = formatStr.indexOf('MM')) > -1  && start < len){
			month = parseInt(dateStr.substr(start, 2)) - 1;
		}
		var day = 0;
		if((start = formatStr.indexOf('dd')) > -1 && start < len){
			day = parseInt(dateStr.substr(start, 2));
		}
		var hour = 0;
		if( ((start = formatStr.indexOf('HH')) > -1 || (start = formatStr.indexOf('hh')) > 1) && start < len){
			hour = parseInt(dateStr.substr(start, 2));
		}
		var minute = 0;
		if((start = formatStr.indexOf('mm')) > -1  && start < len){
			minute = dateStr.substr(start, 2);
		}
		var second = 0;
		if((start = formatStr.indexOf('ss')) > -1  && start < len){
			second = dateStr.substr(start, 2);
		}
		return new Date(year, month, day, hour, minute, second);
	}
})(jQuery);


