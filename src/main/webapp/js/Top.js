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
            		  $li.find("i").removeClass("icon-chevron-down").addClass("icon-chevron-up");
            		}else if(menu == "gmail"){
                        list = [
                            {name:"mail",label:"Mail"},
                            {name:"group",label:"Groups"},
                            {name:"folder",label:"Folders"},
                            {name:"createGroup",label:"Create Group"},
                            {name:"createFolder",label:"Create Folder"},
                        ];
                        brite.display("Dropdown",null,{$target:$li,list:list});
                    }

            	}
            },

            docEvents:{
              "DO_ON_DROPDOWN_CLOSE":function(){
                var view = this;
                var $e = view.$el;
                var $li = $e.find("li[data-nav='oauth']");
            		$li.find("i").removeClass("icon-chevron-up").addClass("icon-chevron-down");
              },
              "DO_ON_DROP_DOWN_CLICK":function(event, name) {
                  switch (name) {
                      case "facebook":
                          app.oauth.authorize('FaceBook');
                          break;
                      case "linkedin":
                          app.oauth.authorize('LinkedIn');
                          break;
                      case "google":
                          app.oauth.authorize('Google');
                          break;
                      case "mail":
                          brite.display("GoogleMails");
                          break;
                      case "group":
                          brite.display("GoogleGroups");
                          break;
                      case "folder":
                          brite.display("GoogleFolders");
                          break;
                      default:
                  }
              }
            },

            daoEvents:{
            }
        });
        
    })(jQuery);
})();