;
(function () {

    /**
     * Component: CreateCalendarEvent
     */
    (function ($) {

        brite.registerView("CreateCalendarEvent", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
            	view = this;
            	data = data || {};

                /*********************************Data filling start****************************************/

                if(data) {
                    this.id = data.id;
                    view.id = data.id;
                }

                if(data.startTime){
                	var date = new Date(data.startTime);
                	data.startDate = date.format("yyyy-MM-dd");
                	this.startDate = date;
                }
                if(data.endTime){
                	var date = new Date(data.endTime);
                	data.endDate = date.format("yyyy-MM-dd");
                	this.endDate = date;
                }

                this.calendarId = data.calendarId;
                if(data.reminders && data.reminders.overrides){
                    $(data.reminders.overrides).each(function(i,temp){
                        if(temp.method == 'email'){
                            data.minutes = temp.minutes;
                        }
                    })
                }
                /*********************************Data filling end****************************************/

                var html = app.render("tmpl-CreateCalendarEvent",data||{});
                var $e = $(html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;

                /***********************************init date Component******************************************************/

				$e.find('.datetimepicker').datetimepicker({
					format : 'yyyy-MM-dd',
					language : 'en',
					pickDate : true,
					pickTime : true,
					inputMask : true
				});

				$e.find(".hour").each(function(){
					var $hour = $(this);
					for (var i = 0; i <= 23; i++) {
						var value = i < 10 ? "0" + i : i;
						var selected = "";
						if($hour.hasClass("startHour")){
							if(view.startDate && view.startDate.getHours() == i){
								selected = "selected";
							}
						}else{
							if(view.endDate && view.endDate.getHours() == i){
								selected = "selected";
							}
						}
						$hour.append("<option value='" + value + "' "+selected+">" + value + "</option>");
					}
				});

				$e.find(".min").each(function(){
					var $min = $(this);
					for (var i = 0; i <= 59; i++) {
						var value = i < 10 ? "0" + i : i;
						var selected = "";
						if($min.hasClass("startMin")){
							if(view.startDate && view.startDate.getMinutes() == i){
								selected = "selected";
							}
						}else{
							if(view.endDate && view.endDate.getMinutes() == i){
								selected = "selected";
							}
						}
						$min.append("<option value='" + value + "' "+selected+">" + value + "</option>");
					}
				});

                /***********************************init date Component******************************************************/

				$calendar = $e.find(".calendar");
				$calendarOpt = $e.find(".calendarOpt");
				app.googleApi.listCalendars().done(function(data) {
					if(!view.id){
						$calendarOpt.removeClass("hide");
						for (var i = 0; i < data.result.length; i++) {
							var id = data.result[i].id;
							var value = data.result[i].summary;
							var selected = "";
							if(data.result[i].primary){
								selected = "selected";
							}
							$calendar.append("<option value='" + id + "' "+selected+">" + value + "</option>");
						}
					}
				});

                var mainScreen = view.mainScreen = $e.bComponent("MainScreen");
                $e.find("form").find("input[type=text]").focus();
            },

            close:function () {
                var $e = this.$el;
                $e.bRemove();
            },

            submit:function () {
                var view = this;
                var $e = this.$el;
                $e.find(".controls span.message").removeClass("error").empty();
                var mainScreen = view.mainScreen;
                data = {};
                data.id = view.id;
                data.location=$e.find(".controls input[name='location'] ").val();
                data.summary=$e.find(".controls textarea[name='summary'] ").val();
                data.reminders=$e.find(".reminders").val();

                var $inviter = $e.find(".addInviter");
                data.inviters = $inviter.val();

                var dateVal = $e.find("input[name='startTime']").val();
                var startHour = $e.find(".startHour").val();
                var startMin =$e.find(".startMin").val();
               	data.startTime = dateVal+" "+startHour+":"+ startMin+ ":00";

                var endDateVal = $e.find("input[name='endTime']").val();
                var endHour = $e.find(".endHour").val();
                var endMin =$e.find(".endMin").val();
               	data.endTime = endDateVal+" "+endHour+":"+ endMin+ ":00";
               	data.calendarId = $e.find(".calendar").val();
                var input = $e.find("textarea[name='summary']");
                if (dateVal == ''){
                    $e.find("input[name='startTime']").focus();
                    $e.find("input[name='startTime']").closest("div").find("span.message").addClass("error").html("Please enter StartTime.");
                } else if(endDateVal == ''){
                    $e.find("input[name='endTime']").focus();
                    $e.find("input[name='endTime']").closest("div").find("span.message").addClass("error").html("Please enter EndTime.");
                } else if(app.dateformat.dateDiff('s',app.dateformat.strFormatToDate('yyyy-MM-dd HH:mm:ss',data.startTime),app.dateformat.strFormatToDate('yyyy-MM-dd HH:mm:ss',data.endTime)) < 0){
                    $e.find("input[name='endTime']").focus();
                    $e.find("input[name='endTime']").closest("div").find("span.message").addClass("error").html("EndTime should after than StartTime.");
                } else if(input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("error").find("span").html("Please enter summary.");
                } else if($inviter.val() == ""){
                    $inviter.focus();
                    $inviter.closest("div").find("span").addClass("error").html("Please enter email address.");
                } else if(!app.validate.email($inviter)){
                    $inviter.focus();
                    $inviter.closest("div").find("span").addClass("error").html("Please enter correct email address.");
                }  else {
                    app.googleApi.saveCalendarEvent(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CALENDAR_EVENT");
                        }), 3000);
                        view.close();
                    });
                }

            },

            events:{
                "btap; .datetimepicker .pick": function(event){
                    var $target = $(event.currentTarget);
                    $target.closest(".datetimepicker").datetimepicker('show');
                },

                "btap; .CreateCalendarEventBtn":function () {
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
