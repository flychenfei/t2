;
(function ($) {

    brite.registerView("GoogleFolders",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-GoogleFolders");
        },

        postDisplay: function (data, config) {
            var view = this;
            showFolders.call(view);
        },

        events: {
        },

        docEvents: {
            "EDIT_FOLDER":function(event, extraData){
                if (extraData && extraData.objId) {
                    var folderName = getGroupId(extraData.objId);
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var fullName = $row.attr("data-fullName");
                    brite.display("CreateFolder", null, {folerName:folderName, fullName:fullName})
                }
            },
            "DELETE_FOLDER": function(event, extraData){
                if (extraData && extraData.objId) {
                    app.googleApi.deleteFolder(extraData.objId).done(function (extradata) {
                        if (extradata && extradata.result) {
                            setTimeout((function () {
                                showFolders();
                            }), 3000);

                        }
                    });
                }
            }
        },

        daoEvents: {
        }
    });
    function showFolders() {
        var folders = app.googleApi.getFolders();
        return brite.display("DataTable", ".folders-container", {
            gridData: folders,
            rowAttrs: function(obj){ return "data-type='Folder' data-obj_id='{0}' data-fullName='{1}'".format(obj.name, obj.fullName)},
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){return idx + 1},
                    attrs:"style='width: 10%'"
                },
                {
                    text:"Name",
                    render:function(obj){return obj.fullName}

                }
            ],
            opts:{
                htmlIfEmpty: "Not Folder found",
                withPaging: false,
                cmdDelete: "DELETE_FOLDER"
            }
        });
    }

})(jQuery);