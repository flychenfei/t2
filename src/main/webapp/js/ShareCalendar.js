;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("ShareCalendar", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
            	view = this;
            	data = data || {};
                if(data) {
                    this.calendarId = data;
                    view.calendarId = this.calendarId;
                }
                var html = app.render("tmpl-ShareCalendar",data||{});
                var $e = $(html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
				
                var mainScreen = view.mainScreen = $e.bComponent("MainScreen");
                $e.find("form").find("input[type=text]").focus();
                
                app.googleApi.getShareCalendar(this.calendarId).done(function(data){
	        		for(var i = 0; i < data.result.length; i++){
	        			var sharedVal=data.result[i].scopeValue;
	        			var ruleId = data.result[i].ruleId;
	        			if (ruleId !="default"){
	        				if(sharedVal != view.calendarId){

								var htmlVal = app.render("tmpl-ShareCalendar-item", {
									sharedVal : sharedVal,
									ruleId : ruleId
								});
								
								$e.find(".shared").append(htmlVal);
								if (sharedVal == data.primaryId) {
									$e.find(".ShareCalendar-item[data-id='"+ruleId+"'] .sharedDel").addClass("hide");
								}
							}
						}
	        		}
	        	});
            },

            close:function () {
                var $e = this.$el;
                $e.bRemove();
            },

            submit:function () {
                var view = this;
                var $e = this.$el;
                var mainScreen = view.mainScreen;
                data = {};
                data.calendarId = view.calendarId;
                data.role=$e.find(".role").val();
                data.scopeType=$e.find(".scopeType").val();
                data.scopeValue=$e.find(".newAccessPerson").val();
                var input = $e.find("input[name='newAccessPerson']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("has-error").find("span").html("Please enter Access.");
                } else if(!app.validate.email(input)){
                    input.focus();
                    input.closest("div").addClass("has-error").find("span").html("Please enter right Email Address.");
                }else {
                    app.googleApi.saveShareCalendar(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CALENDAR");
                        }), 3000);
                        view.close();
                    });
                }
            },

            events:{
            	"click;.sharedDel":function(event){
                    if(confirm("Are you sure to delete it?")){
                        data = {};
                        data.calendarId = view.calendarId;
                        var $btn = $(event.currentTarget);
                        var $div = $btn.closest(".ShareCalendar-item");
                        data.ruleId = $div.attr("data-id");
                        app.googleApi.deleteSharedCalendar(data).done(function (extradata) {
                            setTimeout((function() {
                                $(document).trigger("DO_REFRESH_CALENDAR");
                            }), 3000);
                            view.close();
                        });
                    }
            	},
                "btap; .ShareCalendarBtn":function () {
                    var view = this;
                    var $e = view.$el;
                    view.submit();
                },
                "keydown": function (e) {
                    var view = this;
                    if (e.keyCode == 27) {
                        view.close();
                    }else if (e.keyCode == 13) {
                        view.submit();
                    }
                },
                "btap; .cancelBtn":function () {
                    var view = this;
                    view.close();
                }
            }
        })
    })(jQuery);
})();
