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
            		if(menu == "contact"){
            		  brite.display("GoogleContacts");
            		}else if(menu == "mail"){
            		  brite.display("GoogleMails");
            		}else if(menu == "group"){
            		  brite.display("GoogleGroups");
            		}else if(menu == "fbfriend"){
            		  brite.display("FacebookFriends");
            		}else if(menu == "fbcontact"){
            		  brite.display("FacebookContacts");
            		}else if(menu == "oauth"){
            		  var list = [
            		    {name:"linkedin",label:"Connect to LinkedIn"},
            		    {name:"google",label:"Connect to Google"},
            		    {name:"facebook",label:"Connect to Facebook"}
            		  ];
            		  brite.display("Dropdown",null,{$target:$li,list:list});
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