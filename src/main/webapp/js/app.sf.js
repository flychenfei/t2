var app = app || {};
(function($) {
	app.sf = {};
	
	app.sf.listContacts = function(){
		var params = {method:"GET"};
		return app.getJsonData(contextPath+"/salesforce/listContacts",params);
	}
	app.sf.getContact = function(id){
		var params = {method:"Get"};
		params.id = id;
		return app.getJsonData(contextPath+"/salesforce/getContact",params);
	}
	app.sf.saveContact = function(id,name){
		var params = {method:"Post"};
		params.id = id;
		params.name = name;
		return app.getJsonData(contextPath+"/salesforce/saveContact",params);
	}
	app.sf.deleteContact = function(id){
		var params = {method:"Post"};
		params.id = id;
		return app.getJsonData(contextPath+"/salesforce/deleteContact",params);
	}
})(jQuery);
