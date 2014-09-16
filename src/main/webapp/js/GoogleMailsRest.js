;
(function ($) {

    brite.registerView("GoogleMailsRest",{parent:".GoogleScreen-content",emptyParent:true}, {
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
            var $e = view.$el;

			$e.find('.datetimepicker').datetimepicker({
				format : 'yyyy-MM-dd',
				language : 'en',
				pickDate : true,
				pickTime : true,
				inputMask : true
			});

            showEmails.call(view);
        },

        events: {
          "btap; .inputValueBtn":function () {
				var view = this;
				var $e = view.$el;
				//view.submit();
				var result = {};
				$e.find(".search-mails-container :text").each(function() {
					if ($(this).val() !== "") {
						result[$(this).attr("name")] = $(this).val();
					}
				});
				$e.find(".search-mails-container .checkbox").each(function() {
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
          "click; .currentThread":function(event){
				var view = this;
				var $e = view.$el;
				var threadId = $(event.currentTarget).closest("tr").attr("data-thread-id");
				brite.display("GoogleThreadMails",null,{threadId:threadId});
          },
          "click; .updateLabels":function(event){
				var view = this;
				var $e = view.$el;
				var id = $(event.currentTarget).closest("tr").attr("data-obj_id");
				brite.display("GoogleMailLabelsUpdate",null,{id:id});
          }

        },
        docEvents: {
        	"TRASH_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    app.googleApi.trashEmailRest(extra.objId).done(function(result){
                        setTimeout(function(){
                            showEmails.call(view);
                        }, 3000)

                    });
                }
            },
        	"DELETE_EMAIL": function(event, extra){
                var view = this;
                if(extra.objId){
                    app.googleApi.deleteEmailRest(extra.objId).done(function(result){
                        setTimeout(function(){
                            showEmails.call(view);
                        }, 3000)

                    });
                }
            },
            "SHOW_INFO": function(event, extra) {
                var data = {id: extra.objId, type:'rest'};
                brite.display("GoogleMailInfo", "body", data);
            },
            "FORWARD_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                    	var opt = data.result || {};
                    	opt.type = "rest";
                        brite.display("GoogleMailForwardRest", "body",opt);
                    }

                })

            },
            "REPLAY_EMAIL": function(event, extra) {
                app.googleApi.getMailRest(extra.objId).done(function(data){
                    if(data.success){
                        var opt = data.result || {};
                        opt.type = "rest";
                        brite.display("GoogleMailSend", "body",opt);
                    }

                })

            },
            "DO_REFRESH_MAIL": function(event, extra) {
            	var view = this;
				showEmails.call(view); 
            }
        },

        daoEvents: {
        }
    });
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
                        return obj.subject;
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
                        return "<div class='icon-envelope'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='SHOW_INFO'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='icon-share'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='FORWARD_EMAIL'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='icon-share-alt'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='REPLAY_EMAIL'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='icon-remove'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='DELETE_EMAIL'"
                },
                {
                    text: "",
                    render: function(){
                        return "<div class='icon-trash'/>";
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='TRASH_EMAIL'"
                },
                {
                    text: "",
                    render: function(){
                        return "<a href-'javascript:void(0)' class='currentThread'>Current Thread</a>";
                    },
                    attrs: "style='width:140px;cursor:pointer'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not emails found",
                withCmdDelete:false,
                dataOpts: {
                	withResultCount:false
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
        });
    }
})(jQuery);