;
(function ($) {

    brite.registerView("GoogleMails",{parent:".GoogleScreen-content",emptyParent:true}, {
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
            showEmails.call(view);
        },

        events: {
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
                        return "<div class='icon-user'/>"
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