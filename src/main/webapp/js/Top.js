;(function() {

	/**
	 * View: Top
	 *
	 */
    (function ($) {
        brite.registerView("Top",  {parent:".MainScreen-header"}, {
            create:function (data, config) {
                var $html = app.render("tmpl-Top");
               	var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
            },
            events:{
            	"btap;.nav li":function(e){
            		var view = this;
            		var $e = view.$el;
            		var $li = $(e.currentTarget);
            		$e.find("li").removeClass("active");
            		$li.addClass("active");
            		var menu = $li.attr("data-nav");
            		if(menu == "facebook"){
            		  app.oauth.authorize('FaceBook');
            		}else if(menu == "linkedin"){
            		  app.oauth.authorize('LinkedIn');
            		}else if(menu == "google"){
            		  app.oauth.authorize('Google');
            		}else if(menu == "contact"){
            		  brite.display("GoogleContacts");
            		}else if(menu == "mail"){
            		  brite.display("GoogleMails");
            		}else if(menu == "fbfriend"){
            		  brite.display("FacebookFriends");
            		}else if(menu == "fbcontact"){
            		  brite.display("FacebookContacts");
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