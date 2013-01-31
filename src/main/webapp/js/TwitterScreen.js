;(function() {

	/**
	 * View: TwitterScreen
	 *
	 */
    (function ($) {
        brite.registerView("TwitterScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
            	return app.twitterApi.getUserInfo().pipe(function(data) {
            		var $html = app.render("tmpl-TwitterScreen", {user : data.result});
            		console.log(data);
                    var $e = $($html);
                    return $e;
            	});
            },
            postDisplay:function (data, config) {
            	
            },
            
            events:{
             
            }
            
        });
    })(jQuery);


})();
