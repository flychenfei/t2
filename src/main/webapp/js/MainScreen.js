;(function() {

	/**
	 * View: MainScreen
	 *
	 */
    (function ($) {
        brite.registerView("MainScreen",  {}, {
            create:function (data, config) {
                var $html = app.render("tmpl-MainScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                brite.display("Top");
            },
            events:{
            },

            docEvents:{
            },

            daoEvents:{
            }
            
        });
    })(jQuery);


})();
