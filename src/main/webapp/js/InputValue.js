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
                this.model = {};
                if(data||data.callback) {
                    this.model.callback = data.callback;
                }
                this.model.title = data.title || 'Input Value';
                this.model.fields = data.fields || [{label: "Name", name: 'name', mandatory: true}];
                var html = app.render("tmpl-InputValue", this.model);
                var $e = $(html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
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
                var result = {};
                var input;
                var check = true;
                $.each(this.model.fields, function(idx, val){
                    if (check) {
                        input = $e.find("input[name='{0}']".format(val.name));
                        if (val.mandatory && input.val() == "") {
                            input.focus();
                            input.closest("div").addClass("error").find("span").html("Please enter value.");
                            check = false;
                        } else {
                            //remove empty value
                            var fieldVal = input.val();
                            if(fieldVal !== ""){
                               result[val.name] = fieldVal;
                            }
                        }
                    }
                });

                if(check){
                    view.model.callback(result);
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
