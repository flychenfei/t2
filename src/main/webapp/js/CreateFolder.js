;
(function () {

    /**
     * Component: CreateFolder
     */
    (function ($) {

        brite.registerView("CreateFolder", {
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (data, config) {
            	var view = this;
            	data = data || {};
                if(data) {
                    this.id = data.id;
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

            events:{
                "btap; .createFolderBtn":function () {
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
