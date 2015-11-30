;
(function () {

    /**
     * Component: CopyCalendarEvent
     */
    (function ($) {

        brite.registerView("CopyCalendarEvent", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
            	var view = this;
            	data = data || {};
                /*********************************Data filling start****************************************/

                view.calendarId = data.calendarId;
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
                if(data.iCalUID){
                	view.iCalUID = data.iCalUID;
                }

                if(data.reminders && data.reminders.overrides){
                    $(data.reminders.overrides).each(function(i,temp){
                        if(temp.method == 'email'){
                            data.minutes = temp.minutes;
                        }
                    })
                }
                /*********************************Data filling end****************************************/
                var html = app.render("tmpl-CopyCalendarEvent",data||{});
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

				$copy = $e.find(".copyEvent");
				$copyTo = $e.find(".copyTo");
				app.googleApi.listCalendars().done(function(data) {

					for (var i = 0; i < data.result.length; i++) {
						var id = data.result[i].id;
						var value = data.result[i].summary;
						if (data.result[i].id != view.calendarId) {
							$copyTo.append("<option value='" + id + "' >" + value + "</option>");
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
                var mainScreen = view.mainScreen;
                data = {};
                data.location=$e.find(".controls input[name='location'] ").val();
                data.summary=$e.find(".controls textarea[name='summary'] ").val();
                data.reminders=$e.find(".reminders").val();

                var dateVal = $e.find("input[name='startTime']").val();
                var startHour = $e.find(".startHour").val();
                var startMin =$e.find(".startMin").val();
               	data.startTime = dateVal+" "+startHour+":"+ startMin+ ":00";

                var endDateVal = $e.find("input[name='endTime']").val();
                var endHour = $e.find(".endHour").val();
                var endMin =$e.find(".endMin").val();
               	data.endTime = endDateVal+" "+endHour+":"+ endMin+ ":00";

               	data.copyTo = $e.find(".copyTo").val();
               	data.iCalUID = view.iCalUID;

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
                    input.closest(".control-group").find("span.message").addClass("error").html("Please enter summary.");
                }  else {
                    app.googleApi.saveCopyCalendarEvent(data).done(function (extraData) {
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

                "btap; .CopyCalendarEventBtn":function () {
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
