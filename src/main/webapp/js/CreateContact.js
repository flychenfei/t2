;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("CreateContact", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
                if(data) {
                    this.contractId = data.id;
                }
                var html = app.render("tmpl-CreateContact",data||{});
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
                var $controls = $e.find(".controls input,.controls textarea");
                data = {};
                $controls.each(function(idx, obj){
                    var $this = $(this);
                    data[$this.attr("name")] = $this.val();
                });
                data.id = view.contractId;
                var input = $e.find("input[name='email']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("has-error").find("span").html("Please enter valid contact name.");
                } else {
                    app.googleApi.createContact(data).done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_CONTACT");
                        }), 5000);
                        view.close();
                    });

                }
            },

            events:{
                "btap; .createContactBtn":function () {
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
                },

                //event for when blur check the format.
                "blur; .controls input": function (event) {
                    var view = this;
                    var $input = $(event.currentTarget);
                    var $name = $input.attr("name");
                    var $controls = $input.closest("div");
                    if($name == "email"){
                        var emailRex = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g;
                        if(!emailRex.test($input.val())){
                           $controls.addClass("has-error").find("span").html('username must be in an email format "yourname@yourcompany.com"');
                        } else {
                            $controls.removeClass("has-error").find("span").html("");
                        }
                    }

                    if($name == "phone"){
                        var phoneRex = new RegExp("^[0-9]*$");
                        if(!phoneRex.test($input.val())){
                           $controls.addClass("has-error").find("span").html("the phone should be numeric character");
                        } else {
                            $controls.removeClass("has-error").find("span").html("");
                        }
                    }
                },

                //event for when type, remove the error message
                "keyup; .controls input": function (event) {
                    var $input = $(event.currentTarget);
                    var $controls = $input.parent();
                    $controls.removeClass("has-error");
                    var errorMsg = $controls.find("span");
                    errorMsg.text("");
                },
            }
        })
    })(jQuery);
})();
