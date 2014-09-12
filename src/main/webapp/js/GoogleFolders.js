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
        	"click;.btnAdd":function(e){
	        	brite.display("CreateFolder",null,{id:null});
	        },
	        "click;.folderClass":function(e){
	            var folderName =  $(e.currentTarget).closest("tr").attr("data-obj_id")
	            brite.display("GoogleMails",".GoogleScreen-content",{folderName:folderName});
	        }
        },
        docEvents: {
            "EDIT_FOLDER":function(event, extraData){
                if (extraData && extraData.objId) {
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var fullName = $row.attr("data-fullName");
                    brite.display("CreateFolder", null, {id:fullName, name:fullName})
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
            },
            "DO_REFRESH_FOLDERS":function(){
            	var view = this;
            	showFolders.call(view);
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
                    attrs:"style='width: 10%;  cursor:pointer;'",
                    render: function(obj, idx){return idx + 1}
                },
                {
                    text:"Name",
                    render:function(obj){
                            return "<a src=\"#\" class=\"folderClass\"><span>{0}</span></a>".format(obj.fullName);
                            }
                }
            ],
            opts:{
                htmlIfEmpty: "Not Folder found",
                withPaging: false,
                cmdDelete: "DELETE_FOLDER",
                cmdEdit: "EDIT_FOLDER"
            }
        });
    }

})(jQuery);