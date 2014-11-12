;
(function ($) {

    brite.registerView("GoogleMails",{parent:".GoogleScreen-content",emptyParent:true}, {
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

        events: {
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
          "btap; .searchCondition":function () {
                var view = this;
                var $e = view.$el;
                $e.find(".search-mails-container").toggleClass("hide");
          }

        },
        docEvents: {
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
            "DELETE_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    app.googleApi.deleteEmail(extra.objId).done(function(result){
                        console.log(result);
                        setTimeout(function(){
                            showEmails().call(view);
                        }, 3000)

                    });
                }
            },
            "SHOW_INFO": function(event, extra) {
                var data = {id: extra.objId};
                brite.display("GoogleMailInfo", "body", data);
            },
            "REPLAY_EMAIL": function(event, extra) {
                app.googleApi.getMail(extra.objId).done(function(data){
                    if(data.success){
                        console.log(data);
                        brite.display("GoogleMailSend", "body",data.result);
                    }

                })

            },
            "FORWARDING_EMAIL": function(event, extra) {
                app.googleApi.getMail(extra.objId).done(function(data){
                    if(data.success){
                        console.log(data);
                        brite.display("GoogleMailForwardImap", "body",data.result);
                    }

                })

            },
            
        },

        daoEvents: {
        }
    });
    function showEmails() {
        var view = this;
        var $e = view.$el;
        return brite.display("DataTable", ".mails-container", {
            dataProvider: {list: view.search},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "data-cmd='SHOW_INFO' style='width: 5%;cursor: pointer'"
                },
                {
                    text: "Date",
                    render: function (obj) {
                        var recDate = new Date(obj.date);
                        return recDate.format("yyyy-MM-dd hh:mm:ss")
                    },
                    attrs: "style='width: 20%'"

                },
                {
                    text: "From",
                    render: function (obj) {
                        return obj.from
                    },
                    attrs: "style='width: 25%'"
                },
                {
                    text: "Subject",
                    render: function (obj) {
                        return obj.subject
                    }
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-envelope'/>"
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='SHOW_INFO' title='Show Info'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-share'/>"
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='FORWARDING_EMAIL' title='Forward Email'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-share-alt'/>"
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='REPLAY_EMAIL' title='Replay Email'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='glyphicon glyphicon-trash'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='TRASH_EMAIL' title='Trash Email'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not emails found",
                withPaging: true,
                cmdDelete: "DELETE_EMAIL"
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
})(jQuery);