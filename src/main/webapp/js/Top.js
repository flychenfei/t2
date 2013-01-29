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
            		if(menu == "google"){
            		  brite.display("GoogleScreen");
            		}else if(menu == "facebook"){
            		  brite.display("FacebookScreen");
            		}else if(menu == "linkedIn"){
            		  brite.display("LinkedInScreen");
            		}else if(menu == "salesforce"){
            		  brite.display("SalesForceScreen");
            		}else if(menu == "twitter"){
            		  brite.display("TwitterScreen");
            		}else if(menu == "oauth"){
            		  var list = [
            		    {name:"linkedin",label:"Connect to LinkedIn"},
            		    {name:"google",label:"Connect to Google"},
            		    {name:"salesforce",label:"Connect to SalesForce"},
            		    {name:"github",label:"Connect to Github"},
            		    {name:"facebook",label:"Connect to Facebook"},
            		    {name:"twitter",label:"Connect to Twitter"}
            		  ];
            		  brite.display("Dropdown",null,{$target:$li,list:list});
            		  $li.find("i").removeClass("icon-chevron-down").addClass("icon-chevron-up");
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
                      case "salesforce":
                          app.oauth.authorize('SalesForce');
                          break;
                      case "github":
                          app.oauth.authorize('Github');
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