;(function() {

	/**
	 * View: GoogleScreen
	 *
	 */
    (function ($) {
        brite.registerView("GoogleScreen",  {parent:".MainScreen-main",emptyParent:true}, {
            create:function (data, config) {
                var $html = app.render("tmpl-GoogleScreen");
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
                brite.display("GoogleContacts");
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
                  brite.display("GoogleContacts");
                }else if(menu == "mails"){
                  brite.display("GoogleMails");
                }else if(menu == "groups"){
                  brite.display("GoogleGroups");
                }else if(menu == "folders"){
                  brite.display("GoogleFolders");
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
