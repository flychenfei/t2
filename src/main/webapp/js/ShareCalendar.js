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
            	data = data || {};
                if(data) {
                    this.calendarId = data;
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
                    input.closest("div").addClass("error").find("span").html("Please enter Access.");
                } else {
                    app.googleApi.saveShareCalendar(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CALENDAR");
                        }), 3000);
                        view.close();
                    });

                }
                
            },

            events:{
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
