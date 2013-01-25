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
                
                brite.display("FacebookPosts");
            },
            events:{
              "btap;.nav li":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                $e.find("li").removeClass("active");
                $li.addClass("active");
                
                var menu = $li.attr("data-nav");
                if(menu == "contacts"){
                  brite.display("FacebookContacts");
                }else if(menu == "friends"){
                  brite.display("FacebookFriends");
                }
                else if(menu == "posts"){
                  brite.display("FacebookPosts");
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
