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
                }else if(menu == "groups"){
                  brite.display("GoogleGroups");
                }else if(menu == "calendar-events"){
                  brite.display("GoogleCalendarEvents");
                }else if(menu == "calendars"){
                  brite.display("GoogleCalendars");
                }else if(menu == "mails"){
                  brite.display("GoogleMails");
                }else if(menu == "mails_rest"){
                  brite.display("GoogleMailsRest");
                }else if(menu == "folders"){
                  brite.display("GoogleFolders");
                }else if(menu == "folders_rest"){
                  brite.display("GoogleFoldersRest");
                }else if(menu == "gmail_analytics"){
                  brite.display("GoogleGmailAnalytics");
                }else if(menu == "actions"){
                    var list = [
                        {name:"sendMail",label:"Send Mail"},
                        {name:"sendMailRest",label:"Send Mail Via Rest"},
                        {name:"createGroup",label:"Create Group"},
                        {name:"createContact",label:"Create Contact"},
                        {name:"searchEmail",label:"Search Email"},
                        {name:"searchContact",label:"Search Contact"}
                    ];
                    brite.display("Dropdown",null,{$target:$li,list:list});
                    $li.find("i").removeClass("glyphicon glyphicon-chevron-down").addClass("glyphicon glyphicon-chevron-up");
                }else if(menu == "drive"){
                	var list = [
                                {name:"file",label:"Files"},
                                {name:"trash",label:"Trash"}
                            ];
                    brite.display("Dropdown",null,{$target:$li,list:list});
                    $li.find("i").removeClass("glyphicon glyphicon-chevron-down").addClass("glyphicon glyphicon-chevron-up");
                }
              }
            },

            docEvents:{
                "DO_ON_DROPDOWN_CLOSE":function(){
                    var view = this;
                    var $e = view.$el;
                    var $li = $e.find("li[data-nav='actions']");
                    var $Li = $e.find("li[data-nav='drive']");
                    $li.find("i").removeClass("glyphicon glyphicon-chevron-up").addClass("glyphicon glyphicon-chevron-down");
                    $Li.find("i").removeClass("glyphicon glyphicon-chevron-up").addClass("glyphicon glyphicon-chevron-down");
                },
                "DO_ON_DROP_DOWN_CLICK":function(event, name) {
                    switch (name) {
                        case "sendMail":
                            brite.display("GoogleMailSend");
                            break;
                        case "sendMailRest":
                            brite.display("GoogleMailSend",null,{type:'rest'});
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
                        case "searchEmail":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Search Email',
                                fields: [
                                    {label:"Subject", name:'subject', mandatory:false},
                                    {label:"From", name:"from", mandatory:false},
                                    {datelabel:"StartDate",name:"startDate",placeholder:"yyyy-mm-dd",mandatory:false},
									{datelabel:"EndDate",name:"endDate",placeholder:"yyyy-mm-dd",mandatory:false} 
                                ],
                                callback: function (params) {
                                    brite.display("GoogleMails",".mails-container",{
                                       search: function(opts){
                                           opts = opts||[];
                                            $.extend(opts, params)
                                           return app.googleApi.searchEmails(opts)
                                       }
                                    });
                                }});
                            break;
                        case "searchContact":
                            brite.display("InputValue", ".MainScreen", {
                                title: 'Search Contact',
                                fields: [
                                    {label:"Name", name:'contactName', mandatory:true}
                                ],
                                callback: function (params) {
                                    brite.display("GoogleContacts",".mails-container",{
                                       search: function(opts){
                                           opts = opts||[];
                                            $.extend(opts, params)
                                           return app.googleApi.searchContact(opts)
                                       }
                                    });
                                }});
                            break;
                        case "file":brite.display("GoogleDriveFiles");
                        	break;
                        case "trash":brite.display("GoogleDriveTrash");
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
