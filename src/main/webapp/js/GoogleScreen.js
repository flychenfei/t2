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
                
                brite.display("GoogleMails");
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
                }else if(menu == "actions"){
                    var list = [
                        {name:"sendMail",label:"Send Mail"},
                        {name:"createGroup",label:"Create Group"},
                        {name:"createContact",label:"Create Contact"}
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
                        case "sendMail":
                            brite.display("GoogleMailSend");
                            break;
                        case "createGroup":
                            brite.display("CreateGroup");
                            break;
                        case "createFolder":
                            app.oauth.authorize('GoogleFodler');
                            break;
                        case "createContact":
                            brite.display("CreateContact");
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
