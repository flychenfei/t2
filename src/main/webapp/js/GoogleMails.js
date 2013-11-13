;
(function ($) {

    brite.registerView("GoogleMails",{parent:".GoogleScreen-content",emptyParent:false}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.googleApi.getEmails;
            }
            return app.render("tmpl-GoogleMails");
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
                  return app.googleApi.searchEmails(opts)
                };
                
                showEmails.call(view);
          }

        },
        docEvents: {
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
                console.log(extra);
                var data = {id: extra.objId};
                console.log(data);
                brite.display("GoogleMailInfo", "body", data);
            },
            "REPLAY_EMAIL": function(event, extra) {
                app.googleApi.getMail(extra.objId).done(function(data){
                    if(data.success){
                        console.log(data);
                        brite.display("GoogleMailSend", "body",data.result);
                    }

                })

            }
        },

        daoEvents: {
        }
    });
    function showEmails() {
        var view = this;
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
                },{
                    text: "",
                    render: function(){
                        return "<div class='icon-envelope'/>"
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='SHOW_INFO'"
                },{
                    text: "",
                    render: function(){
                        return "<div class='icon-share-alt'/>"
                    },
                    attrs: "style='width:40px;cursor:pointer'  data-cmd='REPLAY_EMAIL'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not emails found",
                withPaging: true,
                cmdDelete: "DELETE_EMAIL"
            }
        });
    }
})(jQuery);