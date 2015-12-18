;
(function ($) {

    brite.registerView("GoogleFoldersRest",{parent:".GoogleScreen-content",emptyParent:true}, {

        // --------- View Interface Implement--------- //
        create: function (data, config) {
            return app.render("tmpl-GoogleFoldersRest");
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
                brite.display("CreateFolder",null,{type:'rest', id:null});
            },

            // event for show mails for this folder
            "click; .link" : function(event){
                var folderName = $(event.currentTarget).closest("tr").attr("data-obj_id");
                brite.display("GoogleMailsRest",null,{folderName:folderName});
            }
        },
        // --------- /Events--------- //

        // --------- Document Events--------- //
        docEvents: {
            // event for edit folder
            "EDIT_FOLDER":function(event, extraData){
                if (extraData && extraData.objId) {
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var name = $row.attr("data-name");
                    brite.display("CreateFolder", null, {type:'rest', id:extraData.objId, name:name})
                }
            },

            // event for delete folder
            "DELETE_FOLDER": function(event, extraData){
                var view = this;
                if (extraData && extraData.objId) {
                    view.$screen = $("<div class='notTransparentScreen'><span class='loading'>Loading data ...</span></div>").appendTo("body");
                    app.googleApi.deleteLabelRest(extraData.objId).done(function (extradata) {
                        showFolders.call(view);
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
        var folders = app.googleApi.listLabelsRest({force:true});
        return brite.display("DataTable", ".folders-container", {
            gridData: folders,
            rowAttrs: function(obj){ return "data-type='Folder' data-obj_id='{0}' data-name='{1}'".format(obj.id,obj.name)},
            columnDef:[
                {
                    text:"Name",
                    render: function (obj) {
                        return "<a href='javascript:void(0)' class='link'>"+obj.name+"</a>";
                    }

                },
                {
                    text:"",
                    render:function(obj){
                        return obj.type == 'system' ? "" : "<div class='glyphicon glyphicon-edit click-able' title='Edit Folder' data-cmd='EDIT_FOLDER'></div>";
                    },
					attrs: "style='width: 40px'"
                },
                {
                    text:"",
                    render:function(obj){
                        return obj.type == 'system' ? "" : "<div class='glyphicon glyphicon-remove click-able' title='Delete Folder' data-cmd='DELETE_FOLDER'></div>";
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
        }).done(function(){
            //after show the table, move the screen
            if(view.$screen){
                view.$screen.remove();
            }
        });
    }
    // --------- /Private Methods --------- //

})(jQuery);