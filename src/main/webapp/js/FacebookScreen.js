;(function() {

	/**
	 * View: FacebookScreen
	 *
	 */
    (function ($) {
        brite.registerView("FacebookScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-FacebookScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
                brite.display("FacebookFriends");
            },
            events:{
              "btap;.nav li":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                $e.find("li").removeClass("active");
                $li.addClass("active");
                
                var menu = $li.attr("data-nav");
                if(menu == "contact"){
                  brite.display("FacebookContacts");
                }else if(menu == "friend"){
                  brite.display("FacebookFriends");
                }
                else if(menu == "feed"){
                  brite.display("FacebookFeeds");
                }
                else if(menu == "photo"){
                  brite.display("FacebookPhotos");
                } else if(menu == "note"){
                  brite.display("FacebookNotes");
                } else if(menu == "event"){
                  brite.display("FacebookEvents");
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
