;
(function ($) {

    brite.registerView("GoogleMailsRest",{parent:".GoogleScreen-content",emptyParent:true}, {

        // --------- View Interface Implement--------- //
        create: function (data, config) {
            var view = this;
            data = data || {};
            view.folderName = data.folderName;
            this.search = function(opts) {
                opts.label = view.folderName
                return app.googleApi.searchEmailsRest(opts)
            };
            return app.render("tmpl-GoogleMailsRest");
        },

        postDisplay: function (data, config) {
            var view = this;
            view.isSearchTrashEmail = false;

			view.$el.find('.datetimepicker').datetimepicker({
				format : 'yyyy-MM-dd',
				language : 'en',
				pickDate : true,
				pickTime : true,
				inputMask : true
			});

            showEmails.call(view);
        },
        // --------- /View Interface Implement--------- //

        // --------- Events--------- //
        events: {
            // click to show date picker
            "btap; .datetimepicker": function(event){
                $(event.currentTarget).datetimepicker('show');
            },

            // click the search button
            "btap; .inputValueBtn":function () {
				var view = this;
				var result = {};
                view.isSearchTrashEmail = false;
				view.$el.find(".search-mails-container :text").each(function() {
					if ($(this).val() !== "") {
                        if($(this).attr("name") == "label"){
                            var labelVals = $(this).val().toLowerCase();
                            if(labelVals === "trash")view.isSearchTrashEmail = true;
                        }
						result[$(this).attr("name")] = $(this).val();
					}
				});
				view.$el.find(".search-mails-container .checkbox").each(function() {
					if ($(this).val() !== "") {
						result[$(this).attr("name")] = $(this).prop("checked");
					}
				});

				view.search = function(opts) {
					opts = opts || [];
                    //this way, the label input name will be overide, make sure try to combine them to search
                    opts.label = view.folderName
					$.extend(opts, result)
					return app.googleApi.searchEmailsRest(opts)
				};

				showEmails.call(view);
            },

            // click to clean the serach value
            "btap; .cleanSearchBtn":function(event){
                var view = this;
                view.$el.find(".search-mails-container :text").each(function() {
                    if ($(this).val() !== "") {
                        $(this).val("");
                    }
                });
                view.$el.find(".search-mails-container .checkbox").each(function() {
                    if ($(this).val() !== "") {
                        $(this).prop("checked", false);
                    }
                });
                view.search = function(opts) {
                    opts = opts || [];
                    opts.label = view.folderName
                    return app.googleApi.searchEmailsRest(opts)
                };
                showEmails.call(view);
            },

            // event click Current Thread
            "click; .currentThread":function(event){
				var threadId = $(event.currentTarget).closest("tr").attr("data-thread-id");
				brite.display("GoogleThreadMails",null,{threadId:threadId});
            },

            // event click Labels value
            "click; .updateLabels":function(event){
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("GoogleMailLabelsUpdate",null,{id:id});
            },

            // show or hide search condition
            "btap; .searchCondition":function(){
                var view = this;
                view.$el.find(".search-mails-container").toggleClass("hide");
            }
        },
        // --------- /Events--------- //

        // --------- Document Events--------- //
        docEvents: {
            // trash the email
            "TRASH_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    view.$screen = $("<div class='notTransparentScreen'><span class='loading'>Loading data ...</span></div>").appendTo("body");
                    app.googleApi.trashEmailRest(extra.objId).done(function(result){
                        showEmails.call(view);
                    });
                }
            },

            // untrash the email
            "UNTRASH_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    view.$screen = $("<div class='notTransparentScreen'><span class='loading'>Loading data ...</span></div>").appendTo("body");
                    app.googleApi.untrashEmailRest(extra.objId).done(function(result){
                        showEmails.call(view);
                    });
                }
            },

            // delete the email
            "DELETE_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    view.$screen = $("<div class='notTransparentScreen'><span class='loading'>Loading data ...</span></div>").appendTo("body");
                    app.googleApi.deleteEmailRest(extra.objId).done(function(result){
                        showEmails.call(view);
                    });
                }
            },

            // show the email info
            "SHOW_INFO": function(event, extra) {
                var data = {id: extra.objId, type:'rest'};
                brite.display("GoogleMailInfo", "body", data);
            },

            // forward the email
            "FORWARD_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                        var opt = data.result || {};
                        opt.type = "rest";
                        brite.display("GoogleMailForwardRest", "body",opt);
                    }
                })
            },

            // replay the email
            "REPLAY_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                        var opt = data.result || {};
                        opt.type = "rest";
                        brite.display("GoogleMailSend", "body",opt);
                    }
                })
            },

            // event for click the Insert icon
            "INSERT_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                        var opt = data.result || {};
                        opt.type = "rest";
                        opt.isInsert = true;
                        brite.display("GoogleMailSend", "body",opt);
                    }
                })
            },

            // event for click the import icon
            "IMPORT_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                        var opt = data.result || {};
                        opt.type = "rest";
                        opt.isImport = true;
                        brite.display("GoogleMailSend", "body",opt);
                    }
                })
            },

            // event for refresh mail
            "DO_REFRESH_MAIL": function(event, extra) {
                var view = this;
				showEmails.call(view);
            }
        }
        // --------- /Document Events--------- //
    });

    // --------- Private Methods --------- //
    function showEmails() {
        var view = this;
        var $e = view.$el;
        return brite.display("DataTable", ".mails-container", {
            dataProvider: {list: view.search},
            rowAttrs: function(obj){ return "data-thread-id={0}".format(obj.threadId)},
            columnDef: [
                {
                    text: "Date",
                    render: function (obj) {
                        var recDate = new Date(obj.date);
                        return recDate.format("yyyy-MM-dd hh:mm:ss");
                    },
                    attrs: "style='width: 12%'"

                },
                {
                    text: "From",
                    render: function (obj) {
                        return obj.from;
                    },
                    attrs: "style='width: 12%'"
                },
                {
                    text: "Subject",
                    render: function (obj) {
                        return "<div class='click-able'  data-cmd='SHOW_INFO'>"+obj.subject+"</div>";
                    }
                },
                {
                    text: "Labels",
                    render: function (obj) {
                        return "<a href='javascript:void(0)' class='updateLabels'>"+obj.folderNames.join(",")+"</a>";
                    },
                    attrs: "style='width: 20%'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-envelope click-able' data-cmd='SHOW_INFO' title='Show Info'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-share click-able' data-cmd='FORWARD_EMAIL' title='Forward Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-share-alt click-able' data-cmd='REPLAY_EMAIL' title='Replay Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-remove click-able' data-cmd='DELETE_EMAIL' title='Delete Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return view.isSearchTrashEmail ? "<div class='glyphicon glyphicon-transfer click-able' data-cmd='UNTRASH_EMAIL' title='UnTrash Email'/>" : "<div class='glyphicon glyphicon-trash click-able' data-cmd='TRASH_EMAIL' title='Trash Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-saved click-able' data-cmd='INSERT_EMAIL' title='Insert Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-import click-able' data-cmd='IMPORT_EMAIL' title='Import Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<a href-'javascript:void(0)' class='currentThread click-able'>Current Thread</a>";
                    },
                    attrs: "style='width:120px;'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not emails found",
                withCmdDelete:false,
                withPaging: true,
                dataOpts: {
                    withResultCount:true
                }
            }
        }).done(function(){
            var $mailsFolder = $e.closest('.GoogleMailsRest').find(".mails-folder");
            var $tfoot = $e.closest('.GoogleMailsRest').find(".mails-container .tfoot");
            if(typeof view.folderName != "undefined" && view.folderName != null){
                if($tfoot.length > 0){
                    $mailsFolder.removeClass('notHaveFooter');
                }else{
                    $mailsFolder.addClass('notHaveFooter');
                }
                $mailsFolder.show();
                $mailsFolder.find(".folderName").html(view.folderName);
            }else{
                $mailsFolder.hide();
                $mailsFolder.removeClass('notHaveFooter');
            }

            //after show the table, move the screen
            if(view.$screen){
                view.$screen.remove();
            }
        });
    }
    // --------- /Private Methods --------- //
})(jQuery);