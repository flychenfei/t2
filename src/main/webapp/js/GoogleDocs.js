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

        events: {
			"click;.btnSearch":function(e){
				  brite.display("InputValue", ".MainScreen", {
				  title: 'Search Doc',
				  fields: [
				      {label:"FileName", name:'title', mandatory:true}
				  ],
				  callback: function (params) {
				      brite.display("GoogleDocs",".GoogleScreen-content",{
				            	  results: function(opts){
				                     opts = opts||[];
				                      $.extend(opts, params)
				                     return app.googleDocsApi.searchDocs(opts)
				                 }
				              });
				          }});
				  },
			"click;.download":function(event){
				var fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
	        	var fileName = $(event.currentTarget).closest("tr").attr("data-fileName");
	        	var fileUrl = $(event.currentTarget).closest("tr").attr("data-hasUrl");
	        	if(fileId && fileUrl == "true"){
	        		window.location.href=contextPath+"/googleDocsList/download?fileId="+fileId+"&fileName="+fileName;
	        	}else{
	        		alert("This file is not support download!");
	        	}
			},
			 "click;.delete":function(event){
			    var parma = {};
            	parma.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
                app.googleDocsApi.deleteDoc(parma).done(function (success) {
                    if(success){
                    	alert("Delete success");
                    }else{
                    	alert("Delete fail");
                    }
                    brite.display("GoogleDocs",".GoogleScreen-content");
                });
				}
        },

        docEvents: {
            "DO_REFRESH_DOCS":function(){
                 var view = this;
                 showDocs.call(view);
             }
        },
        daoEvents: {
        }
    });
    function showDocs() {
    	var view = this;
        return brite.display("DataTable", ".docs-container", {
        	dataProvider: {list: view.results},
        	rowAttrs: function (obj) {
                return " data-fileId='{0}' data-fileName='{1}' data-hasUrl='{2}'".format(obj.fileId,obj.fileName,obj.hasUrl||"false")
            },
            columnDef:[
                {
                    text:"FileName",
                    attrs: "style='width:15%; word-break: break-word;'",
                    render:function(obj){return obj.fileName}
                },
                {
                    text:"CreateTime",
                    attrs: "style='width:15%; word-break: break-word;'",
                    render:function(obj){return obj.createTime}
                },
                {
                    text:"UpdateTime",
                    attrs: "style='width:15%; word-break: break-word;'",
                    render:function(obj){return obj.updateTime}
                },
                {
                    text:"FileType",
                    attrs: "style='width:20%; word-break: break-word;'",
                    render:function(obj){return obj.fileType}
                },
                {
                    text:"FileSize(bytes)",
                    attrs: "style='width:10%; word-break: break-word;'",
                    render:function(obj){return obj.fileSize}
                },
                {
                    text:"Owner",
                    attrs: "style='width:10%; word-break: break-word;'",
                    render:function(obj){return obj.owner}
                },
                {
                    text:"Operator",
                    attrs: "style='width:10%'",
                    render: function (obj) {
                        return "<span> <a src=\"#\" class=\"download\">"+"download"+"</a></br><a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
                    }
                }
            ],
            opts:{
                htmlIfEmpty: "Not Docs found",
                withCmdDelete: false,
                withPaging: true,
            	dataOpts: {
                	withResultCount:false
                }
            }
        });
    }
    
})(jQuery);