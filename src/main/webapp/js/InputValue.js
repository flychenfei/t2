;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("InputValue", {
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
                if(data||data.callback) {
                    this.callback = data.callback;
                }
                var html = app.render("tmpl-InputValue");
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
                var input = $e.find("input[name='name']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("error").find("span").html("Please enter value.");
                } else {
                    view.callback(input.val());
                    view.close();
                }
            },

            events:{
                "btap; .inputValueBtn":function () {
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
