;(function() {

	/**
	 * View: TwitterScreen
	 *
	 */
    (function ($) {
        brite.registerView("TwitterScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-TwitterScreen");
                var $e = $($html);
                return $e;
            	
            },
            postDisplay:function (data, config) {
            	app.twitterApi.getUserInfo().pipe(function(data) {
                   console.log("success");
            	});
            },
            events:{
             
            }
            
        });
    })(jQuery);


})();
