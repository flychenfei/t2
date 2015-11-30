;
(function ($) {

    brite.registerView("GoogleMails",{parent:".GoogleScreen-content",emptyParent:true}, {

        // --------- View Interface Implement--------- //
        create: function (data, config) {
            var view = this;
            data = data || {};
            view.folderName = data.folderName;
            this.search = function(opts) {
                opts.label = view.folderName;
				return app.googleApi.searchEmails(opts)
			};
            return app.render("tmpl-GoogleMails",{folderName:view.folderName});
        },

        postDisplay: function (data, config) {
            var view = this;
            var $e = view.$el;

            $e.find('.datetimepicker').datetimepicker({
                format: 'yyyy-MM-dd',
                language: 'en',
                pickDate: true,
                pickTime: true,
                inputMask: true
            });

            showEmails.call(view);
        },
        // --------- /View Interface Implement--------- //

        // --------- Events--------- //
        events: {
            // click to show date picker
            "btap; .datetimepicker": function(event){
                var view = this;
                var $e = view.$el;
                $(event.currentTarget).datetimepicker('show');
            },

            // click the search button
            "btap; .inputValueBtn":function () {
                var view = this;
                var $e = view.$el;
                //view.submit();
                var result = {};
                $e.find(".search-mails-container :text").each(function(){
                    if($(this).val() !== ""){
                        result[$(this).attr("name")] = $(this).val();
                    }
                });
                $e.find(".search-mails-container .checkbox").each(function(){
                    if($(this).val() !== ""){
                        result[$(this).attr("name")] = $(this).prop("checked");
                    }
                });
                view.search = function(opts) {
                    opts = opts || [];
                    $.extend(opts, result)
                    opts.label = view.folderName;
                    return app.googleApi.searchEmails(opts)
                };
                showEmails.call(view);
            },

            // click to clean the serach value
            "btap; .cleanSearchBtn":function(event){
                var view = this;
                var $e = view.$el;
                $e.find(".search-mails-container :text").each(function() {
                    if ($(this).val() !== "") {
                        $(this).val("");
                    }
                });
                $e.find(".search-mails-container .checkbox").each(function() {
                    if ($(this).val() !== "") {
                        $(this).prop("checked", false);
                    }
                });
                view.search = function(opts) {
                    opts = opts || [];
                    opts.label = view.folderName;
                    return app.googleApi.searchEmails(opts)
                };
                showEmails.call(view);
            },

            // show or hide search condition
            "btap; .searchCondition":function () {
                var view = this;
                var $e = view.$el;
                $e.find(".search-mails-container").toggleClass("hide");
            }

        },
        // --------- /Events--------- //

        // --------- Document Events--------- //
        docEvents: {
            // trash the email
            "TRASH_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    app.googleApi.trashEmail(extra.objId).done(function(result){
                        setTimeout(function(){
                            showEmails.call(view);
                        }, 3000)

                    });
                }
            },

            // delete the email
            "DELETE_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    app.googleApi.deleteEmail(extra.objId).done(function(result){
                        console.log(result);
                        setTimeout(function(){
                            showEmails.call(view);
                        }, 3000)

                    });
                }
            },

            // show the email info
            "SHOW_INFO": function(event, extra) {
                var data = {id: extra.objId};
                brite.display("GoogleMailInfo", "body", data);
            },

            // replay the email
            "REPLAY_EMAIL": function(event, extra) {
                app.googleApi.getMail(extra.objId).done(function(data){
                    if(data.success){
                        console.log(data);
                        brite.display("GoogleMailSend", "body",data.result);
                    }
                })
            },

            // forward the email
            "FORWARDING_EMAIL": function(event, extra) {
                app.googleApi.getMail(extra.objId).done(function(data){
                    if(data.success){
                        console.log(data);
                        brite.display("GoogleMailForwardImap", "body",data.result);
                    }
                })
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
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1;
                    },
                    attrs: "data-cmd='SHOW_INFO' style='width: 5%;cursor: pointer'"
                },
                {
                    text: "Date",
                    render: function (obj) {
                        var recDate = new Date(obj.date);
                        return recDate.format("yyyy-MM-dd hh:mm:ss");
                    },
                    attrs: "style='width: 20%'"

                },
                {
                    text: "From",
                    render: function (obj) {
                        return obj.from;
                    },
                    attrs: "style='width: 25%'"
                },
                {
                    text: "Subject",
                    render: function (obj) {
                        return "<div class='click-able'  data-cmd='SHOW_INFO'>"+obj.subject+"</div>";
                    }
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-envelope click-able'  data-cmd='SHOW_INFO' title='Show Info'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-share click-able'  data-cmd='FORWARDING_EMAIL' title='Forward Email'/>";
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
                        return "<div class='glyphicon glyphicon-trash click-able' data-cmd='TRASH_EMAIL' title='Trash Email'/>";
                    },
                    attrs: "style='width:40px;'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-remove click-able' data-cmd='DELETE_EMAIL' title='Delete Email'/>";
                    },
                    attrs: "style='width:40px;'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not emails found",
                withPaging: true,
                withCmdDelete: false
            }
        }).done(function(){
            var $mailsFolder = $e.find(".imapMails-folder");
            if(typeof view.folderName != "undefined" && view.folderName != null){
                $mailsFolder.show();
                $mailsFolder.find(".folderName").html(view.folderName);
            }else{
                $mailsFolder.hide();
            }
        });
    }
    // --------- /Private Methods --------- //
})(jQuery);