;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("CreateGroup", {
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
                if(data) {
                    this.groupId = data.groupId;
                    this.etag = data.etag;
                }
                var html = app.render("tmpl-CreateGroup",data||{});
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
                var dfd;
                var input = $e.find("input[name='name']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("error").find("span").html("Please enter valid group name.");
                } else {
                    if(view.groupId) {
                        dfd = app.googleApi.createGroup({groupId:view.groupId,etag:view.etag, groupName: input.val()})
                    }else{
                        dfd = app.googleApi.createGroup({groupName: input.val()});
                    }
                    dfd.done(function (extraData) {
                        setTimeout((function () {
                            $("body").trigger("SHOW_GROUPS");
                        }), 5000);
                        view.close();
                    });

                }
            },

            events:{
                "btap; .createGroupBtn":function () {
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
