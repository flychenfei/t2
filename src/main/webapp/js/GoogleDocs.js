;
(function ($) {
    brite.registerView("GoogleDocs",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	 if(data && data.results) {
                 this.results = data.results;
             }else{
                 this.results = app.googleDocsApi.getDocsList;
             }
            return app.render("tmpl-GoogleDocs");
        },

        postDisplay: function (data, config) {
            var view = this;
            showDocs.call(view);
        },

        docEvents: {
            "DO_REFRESH_DOCS":function(){
                 var view = this;
                 showDocs.call(view);
             },
            "DELETE_DOC": function(event, extraData){alert("not implement");}
        },

        daoEvents: {
        }
    });
    function showDocs() {
    	var view = this;
        return brite.display("DataTable", ".docs-container", {
        	dataProvider: {list: view.results},
        	rowAttrs: function (obj) {
                return " data-fileId='{0}'".format(obj.fileId)
            },
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){ return idx + 1},
                    attrs:"style='width: 5%'"
                },
                {
                    text:"FileName",
                    attrs: "style='width:15%'",
                    render:function(obj){return obj.fileName}

                },
                {
                    text:"CreateTime",
                    attrs: "style='width:15%'",
                    render:function(obj){return obj.createTime}

                },
                {
                    text:"UpdateTime",
                    attrs: "style='width:15%'",
                    render:function(obj){return obj.updateTime}

                },
                {
                    text:"FileType",
                    attrs: "style='width:20%'",
                    render:function(obj){return obj.fileType}

                },
                {
                    text:"FileSize(bytes)",
                    attrs: "style='width:10%'",
                    render:function(obj){return obj.fileSize}

                },
                {
                    text:"Owner",
                    attrs: "style='width:10%'",
                    render:function(obj){return obj.owner}

                }
            ],
            opts:{
                htmlIfEmpty: "Not Docs found",
                withPaging: true,
                cmdDelete: "DELETE_DOC",
            	dataOpts: {
                	withResultCount:false
                }
            }
        });
    }
    
})(jQuery);