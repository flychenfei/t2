;(function() {

	/**
	 * View: GoogleScreen
	 *
	 */
    (function ($) {
        brite.registerView("LiveScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-LiveScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
                brite.display("LiveUserInfo");
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
                  brite.display("LiveUserInfo");
                }else if(menu == "contacts"){
                  brite.display("LiveContacts");
                }else if(menu == "albums"){
                  brite.display("LiveAlbums");
                }else if(menu == "drive"){
                  brite.display("LiveDrive");
                }else if(menu == "calendar"){
                  brite.display("LiveCalendar");
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
