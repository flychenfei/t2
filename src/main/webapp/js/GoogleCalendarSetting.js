;
(function ($) {

    brite.registerView("GoogleCalendarSetting",{parent:".GoogleScreen-content",emptyParent:true},{
        create: function (data, config) {
            this.search = app.googleApi.listSetting;
            return app.render("tmpl-GoogleCalendarSetting");
        },

        postDisplay: function (data, config) {
            var view = this;
            var $e = view.$el;
            
            showCalendarSetting.call(view);
        },

        events: {
        },

        docEvents: {
        	"DO_REFRESH_CALENDAR_SETTING":function(){
                 var view = this;
                 showCalendarSetting.call(view);
             },
          	"click;.getSetting":function(event){
	        	var $btn = $(event.currentTarget);
	        	var $tr = $btn.closest("tr");
	        	var settingId = $tr.attr("data-obj_id");
            	brite.display("SettingCalendar", null, settingId);
	        }
        },

        daoEvents: {
        }
    });

    function showCalendarSetting() {
        var view = this;
        brite.display("DataTable", ".setting-container", {
            dataProvider: {list: view.search},
            columnDef: [
                {
                    text: "name",
                    render: function (obj) {
                        return obj.id;
                    },
                    attrs: "style='width: 100px'"

                },
                {
                    text: "value",
                    render: function (obj) {
                        return obj.value;
                    },
                    attrs: "style='width: 10%'"

                },
                {
                    text:"",
                    render:function(obj){
                    	return "<div class='glyphicon glyphicon-cog getSetting' style='cursor:pointer'></div>";
                    },
					attrs: "style='width: 10%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not calendar found",
                withPaging: true,
                withCmdEdit:false,
                withCmdDelete: false,
                dataOpts:{
                	withResultCount:false
                }
            }
        });
    }
    
})(jQuery);