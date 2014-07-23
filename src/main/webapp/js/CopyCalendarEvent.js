;
(function () {

    /**
     * Component: CreateTable
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
                
                var html = app.render("tmpl-CopyCalendarEvent",data||{});
                var $e = $(html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;

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
                
                if (startHour == "" || endHour == "") {
                    input.focus();
                    input.closest("div").addClass("error").find("span").html("Please enter summary.");
                } else {
                    app.googleApi.saveCopyCalendarEvent(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CALENDAR");
                        }), 3000);
                        view.close();
                    });

                }
                
            },

            events:{
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
