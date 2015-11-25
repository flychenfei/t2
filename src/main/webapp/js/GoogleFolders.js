;
(function ($) {

    brite.registerView("GoogleFolders",{parent:".GoogleScreen-content",emptyParent:true}, {

        // --------- View Interface Implement--------- //
        create: function (data, config) {
            return app.render("tmpl-GoogleFolders");
        },

        postDisplay: function (data, config) {
            var view = this;
            showFolders.call(view);
        },
        // --------- /View Interface Implement--------- //

        // --------- Events--------- //
		events: {
            // event for Add folder
            "click;.btnAdd":function(e){
                brite.display("CreateFolder",null,{id:null});
            },

            // event for show mails for this folder
            "click;.folderClass":function(e){
                var folderName =  $(e.currentTarget).closest("tr").attr("data-obj_id")
                brite.display("GoogleMails",".GoogleScreen-content",{folderName:folderName});
            }
        },
        // --------- /Events--------- //

        // --------- Document Events--------- //
        docEvents: {
            // event for edit folder
            "EDIT_FOLDER":function(event, extraData){
                if (extraData && extraData.objId) {
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var fullName = $row.attr("data-fullName");
                    brite.display("CreateFolder", null, {id:fullName, name:fullName})
                }
            },

            // event for delete folder
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
            },

            // event for refresh folder table
            "DO_REFRESH_FOLDERS":function(){
                var view = this;
                showFolders.call(view);
            }
        }
        // --------- /Document Events--------- //
    });

    // --------- Private Methods --------- //
    function showFolders() {
        var view = this;
        var folders = app.googleApi.getFolders();
        return brite.display("DataTable", ".folders-container", {
            gridData: folders,
            rowAttrs: function(obj){ return "data-type='Folder' data-obj_id='{0}' data-fullName='{1}'".format(obj.name, obj.fullName)},
            columnDef:[
                {
                    text:"#",
                    attrs:"style='width: 10%;  cursor:pointer;'",
                    render: function(obj, idx){return idx + 1}
                },
                {
                    text:"Name",
                    render:function(obj){
                        return "<a src=\"#\" class=\"folderClass\"><span>{0}</span></a>".format(obj.fullName);
                    }
                },
                {
                    text:"",
                    render:function(obj){
                        return checkFolderTypeByName.call(view, obj.fullName) ? "" : "<div class='glyphicon glyphicon-edit' data-cmd='EDIT_FOLDER'></div>";
                    },
                    attrs: "style='width: 40px'"
                },
                {
                    text:"",
                    render:function(obj){
                        return checkFolderTypeByName.call(view, obj.fullName) ? "" : "<div class='glyphicon glyphicon-remove' data-cmd='DELETE_FOLDER'></div>";
                    },
                    attrs: "style='width: 40px'"
                }
            ],
            opts:{
                htmlIfEmpty: "Not Folder found",
                withPaging: false,
                withCmdEdit:false,
                withCmdDelete: false
            }
        });
    }

    function checkFolderTypeByName(folderName){
        var isSystemFolder = false;
        if(folderName == "INBOX" || folderName.indexOf("[Gmail]") > -1){
            isSystemFolder = true;
        }
        return isSystemFolder;
    }
    // --------- /Private Methods --------- //

})(jQuery);