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
            		}else if(menu == "github"){
                        brite.display("GithubScreen");
            		}else if(menu == "foursquare"){
                        brite.display("FoursquareScreen");
            		}else if(menu == "live"){
                        brite.display("LiveScreen");
            		}else if(menu == "dropbox"){
                        brite.display("DropboxScreen");
            		}else if(menu == "oauth"){
            		  var list = [
            		    {name:"linkedin",label:"Connect to LinkedIn"},
            		    {name:"google",label:"Connect to Google"},
            		    {name:"salesforce",label:"Connect to SalesForce"},
            		    {name:"github",label:"Connect to Github"},
            		    {name:"dropbox",label:"Connect to Dropbox"},
            		    {name:"twitter",label:"Connect to Twitter"},
            		    {name:"facebook",label:"Connect to Facebook"},
            		    {name:"foursquare",label:"Connect to FourSquare"},
            		    {name:"live",label:"Connect to Live"},
            		    {name:"yahoo",label:"Connect to Yahoo"}
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
                      case "dropbox":
                          app.oauth.authorize('Dropbox');
                          break;
                      case "twitter":
                          app.oauth.authorize('Twitter');
                          break;
                      case "live":
                          app.oauth.authorize("Live");
                          break;
                      case "foursquare":
                          app.oauth.authorize("Foursquare");
                          break;
                      case "yahoo":
                          app.oauth.authorize("Yahoo");
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