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
                }else if(menu == "userFriends"){
                    brite.display("FoursquareUserFriends");
                }else if(menu == "venuesCategories"){
                    brite.display("FoursquareVenuesCategories");
                }else if(menu == "search"){
                    var list = [
                        {name:"venuesTrending",label:"Venues Trending"},
                        {name:"recentCheckins",label:"Recent Checkins"},
                        {name:"venuesSearch",label:"Venues Search"}
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
                    var $li = $e.find("li[data-nav='actions']");
                    $li.find("i").removeClass("icon-chevron-up").addClass("icon-chevron-down");
                },
                "DO_ON_DROP_DOWN_CLICK":function(event, name) {
                    switch (name) {
                        case "venuesTrending":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Venues Trending',
                                fields: [
                                    {label:"LL", name:'ll', mandatory:false},
                                    {label:"Limit", name:"limit", mandatory:false},
                                    {label:"After", name:"after", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareVenuesTrending",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.venuesTrending(opts);
                                        }
                                    });
                                }});
                            break;
                        case "recentCheckins":
                            brite.display("FoursquareRecentCheckins");
                            break;

                        case "VenuesSearch":
                            brite.display("FoursquareVenuesSearch");
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
