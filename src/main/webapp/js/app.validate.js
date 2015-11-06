var app = app || {};
(function($) {

	app.validate = {};

	//email validate
	app.validate.email = function(item) {
		if(item.val()){
			if (item.val().search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1){
				return true;
			}else{
				return false;
			}
		}
	}
})(jQuery);


