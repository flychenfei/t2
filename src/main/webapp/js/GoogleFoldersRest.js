;
(function ($) {

    brite.registerView("GoogleFoldersRest",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-GoogleFoldersRest");
        },

        postDisplay: function (data, config) {
            var view = this;
            showFolders.call(view);
        },

        events: {
        	"click;.btnAdd":function(e){
	        	brite.display("CreateFolder",null,{type:'rest', id:null});
	        },
            "click; .link" : function(event){
                var folderName = $(event.currentTarget).closest("tr").attr("data-obj_id");
                brite.display("GoogleMailsRest",null,{folderName:folderName});
            }
        },

        docEvents: {
            "EDIT_FOLDER":function(event, extraData){
                if (extraData && extraData.objId) {
                    var $row = $(extraData.event.currentTarget).closest("tr");
                    var name = $row.attr("data-name");
                    brite.display("CreateFolder", null, {type:'rest', id:extraData.objId, name:name})
                }
            },
            "DELETE_FOLDER": function(event, extraData){
                if (extraData && extraData.objId) {
                    app.googleApi.deleteLabelRest(extraData.objId).done(function (extradata) {
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
                    	return obj.type == 'system' ? "" : "<div class='glyphicon glyphicon-edit' data-cmd='EDIT_FOLDER'></div>";
                    },
					attrs: "style='width: 40px'"
                },
                {
                    text:"",
                    render:function(obj){
                    	return obj.type == 'system' ? "" : "<div class='glyphicon glyphicon-remove' data-cmd='DELETE_FOLDER'></div>";
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

})(jQuery);