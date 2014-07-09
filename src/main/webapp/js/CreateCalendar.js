;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("CreateCalendar", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
            	data = data || {};
                if(data) {
                    this.id = data.id;
                }
                var html = app.render("tmpl-CreateCalendar",data||{});
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
                data.id = view.id;
                data.summary=$e.find(".controls textarea[name='summary'] ").val();
                var input = $e.find("textarea[name='summary']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("error").find("span").html("Please enter summary.");
                } else {
                    app.googleApi.saveCalendars(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CALENDAR");
                        }), 3000);
                        view.close();
                    });

                }
                
            },

            events:{
                "btap; .CreateCalendarBtn":function () {
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
