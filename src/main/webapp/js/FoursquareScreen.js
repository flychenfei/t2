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
                }else if(menu == "usersBadges"){
                    brite.display("FoursquareUsersBadges");
                }else if(menu == "venuesCategories"){
                    brite.display("FoursquareVenuesCategories");
                }else if(menu == "search"){
                    var list = [
                        {name:"searchUser",label:"User Search"},
                        {name:"venuesTrending",label:"Venues Trending"},
                        {name:"recentCheckins",label:"Recent Checkins"},
                        {name:"venuesSearch",label:"Venues Search"},
                        {name:"venuesExplore",label:"Venues Explore"},
                        {name:"specialsSearch",label:"Specials Search"}
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
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Recent Checkins',
                                fields: [
                                    {label:"LL", name:'ll', mandatory:false},
                                    {label:"Limit", name:"limit", mandatory:false},
                                    {label:"After", name:"after", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareRecentCheckins",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.recentCheckins(opts);
                                        }
                                    });
                                }});
                            break;

                        case "venuesSearch":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Search VenuesSearch',
                                fields: [
                                    {label:"Near", name:'venues.near', mandatory:false},
                                    {label:"Ll", name:'venues.ll', mandatory:false},
                                    {label:"Limit", name:'venues.limit', mandatory:false},
                                    {label:"Intent", name:'venues.intent', mandatory:false},
                                    {label:"CategoryId", name:'venues.categoryId', mandatory:false},
                                    {label:"Url", name:'venues.url', mandatory:false},
                                    {label:"ProviderId", name:'venues.providerId', mandatory:false},
                                    {label:"LinkedId", name:'venues.LinkedId', mandatory:false},
                                    {label:"Query", name:"venues.query", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareVenuesSearch",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.venuesSearch(opts);
                                        }
                                    });
                                }});
                            break;
                        case "searchUser":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Search User',
                                fields: [
                                    {label:"Name", name:'name', mandatory:false},
                                    {label:"Phone", name:'phone', mandatory:false},
                                    {label:"Email", name:"email", mandatory:false},
                                    {label:"FBID", name:"fbid", mandatory:false},
                                    {label:"Twitter", name:"twitter", mandatory:false},
                                    {label:"TwitterSource", name:"twitterSource", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareSearchUser",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.searchUser(opts);
                                        }
                                    });
                                }});
                            break;
                        case "venuesExplore":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Venues Explore',
                                fields: [
                                    {label:"LL", name:'ll', mandatory:false},
                                    {label:"Limit", name:"limit", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareVenuesExplore",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.venuesExplore(opts);
                                        }
                                    });
                                }});
                            break;
                        case "specialsSearch":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Specials Search',
                                fields: [
                                    {label:"LL", name:'ll', mandatory:false},
                                    {label:"Limit", name:"limit", mandatory:false}
                                ],
                                callback: function (params) {
                                    brite.display("FoursquareSpecialSearch",".FoursquareScreen-content",{
                                        search: function(opts){
                                            opts = opts||[];
                                            $.extend(opts, params);
                                            return app.foursquareApi.specialSearch(opts);
                                        }
                                    });
                                }});
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
