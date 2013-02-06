;(function() {

	/**
	 * View: GoogleScreen
	 *
	 */
    (function ($) {
        brite.registerView("FoursquareScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-FoursquareScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
                brite.display("FoursquareUserInfo");
            },
            events:{
              "btap;.nav li":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                $e.find("li").removeClass("active");
                $li.addClass("active");
                
                var menu = $li.attr("data-nav");
                if(menu == "userInfo"){
                  brite.display("FoursquareUserInfo");
                }
              }
            },

            docEvents:{

            },

            daoEvents:{
            }
            
        });
    })(jQuery);


})();
