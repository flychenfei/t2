;
(function ($) {

    brite.registerView("GoogleGroups",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-GoogleGroups");
        },

        postDisplay: function (data, config) {
            var view = this;
            showGroups.call(view);
        },

        events: {
        },

        docEvents: {
            "EDIT_GROUP":function(event, extraData){
                if (extraData && extraData.objId) {
                    var groupId = getGroupId(extraData.objId);
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var title = $row.attr("data-title");
                    var etag = $row.attr("data-etag");
                    console.log(etag);
                    brite.display("CreateGroup", null, {groupId:groupId, title:title, etag:etag})
                }
            },
            "DELETE_GROUP": function(event, extraData){
                if (extraData && extraData.objId) {
                    var groupId = getGroupId(extraData.objId);
                    var etag = $(extraData.event.currentTarget).closest("tr").attr("data-etag");
                    app.googleApi.deleteGroup(groupId, etag).done(function (extradata) {
                        if (extradata && extradata.result) {
                            setTimeout((function () {
                                showGroups();
                            }), 3000);

                        }
                    });
                }
            }
        },

        daoEvents: {
        }
    });
    function showGroups() {
        var groups = app.googleApi.getGroups();
        return brite.display("DataTable", ".groups-container", {
            gridData: groups,
            rowAttrs: function(obj){ return "data-type='Group' data-etag='{0}' data-title='{1}'".format(obj.etag, obj.title.text)},
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){return idx + 1},
                    attrs:"style='width: 10%'"
                },
                {
                    text:"Title",
                    attrs: " data-cmd='SHOW_CONTACTS' style='cursor:pointer;width:40%' ",
                    render:function(obj){return obj.title.text}

                },
                {
                    text:"Etag",
                    render:function(obj){return obj.etag}

                }
            ],
            opts:{
                htmlIfEmpty: "Not Groups found",
                withPaging: false,
                cmdEdit: "EDIT_GROUP",
                cmdDelete: "DELETE_GROUP"
            }
        });
    }

    function getGroupId(url) {
        var myregexp = /http:\/\/www.google.com\/m8\/feeds\/groups\/(.+)\/base\/(.+)/;
        var match = myregexp.exec(url);
        if (match != null) {
            result = match[2];
        } else {
            result = "";
        }
        return result;
    }
})(jQuery);