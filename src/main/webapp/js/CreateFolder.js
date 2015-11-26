;
(function () {

    /**
     * Component: CreateFolder
     * Description: for create folder view
     */
    (function ($) {

        brite.registerView("CreateFolder", {
            parent:".MainScreen",
            emptyParent:false
        }, {
            // --------- View Interface Implement--------- //
            create:function (data, config) {
                var view = this;
                data = data || {};
                if(data) {
                    view.id = data.id;
                }
                if(data.type){
                    view.type = data.type;
                }
                var html = app.render("tmpl-CreateFolder",data||{});
                var $e = $(html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                view.mainScreen = $e.bComponent("MainScreen");
                $e.find("form").find("input[type=text]").focus();
            },

            close:function () {
                var view = this;
                view.$el.bRemove();
            },

            submit:function () {
                var view = this;
                var $e = this.$el;
                var mainScreen = view.mainScreen;
                var dfd;
                var input = $e.find("input[name='name']");
                if (input.val() == "") {
                    input.focus();
                    input.closest("div").addClass("has-error").find("span").html("Please enter valid folder name.");
                } else {
                    if(view.type == 'rest'){
                        dfd = app.googleApi.saveLabelRest({id:view.id, name: input.val()});
                    }else{
                        dfd = app.googleApi.saveFolder({oldName:view.id, name: input.val()});
                    }
                    dfd.done(function (extraData) {
                        setTimeout((function () {
                            $(document).trigger("DO_REFRESH_FOLDERS");
                        }), 5000);
                        view.close();
                    });

                }
            },
            // --------- /View Interface Implement--------- //

            // --------- Events--------- //
            events:{
                //event for the Create button to create folder
                "btap; .createFolderBtn":function () {
                    var view = this;
                    var $e = view.$el;
                    view.submit();
                },

                // keydown event for close or submit
                "keydown": function (e) {
                    var view = this;
                    if (e.keyCode == 27) {
                        view.close();
                    }else if (e.keyCode == 13) {
                        view.submit();
                    }
                },

                // event for Cancel button
                "btap; .cancelBtn":function () {
                    var view = this;
                    view.close();
                }
            }
            // --------- /Events--------- //
        })
    })(jQuery);
})();
