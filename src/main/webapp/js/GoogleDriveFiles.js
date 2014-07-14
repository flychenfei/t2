;
(function ($) {
    brite.registerView("GoogleDriveFiles",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	 if(data && data.results) {
                 this.results = data.results;
             }else{
                 this.results = app.googleDriveApi.fileList;
             }
            return app.render("tmpl-GoogleDriveFiles");
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
            showFile.call(view);
        },

        events: {
        	"click;.btnUpload":function(e){
        		brite.display("GoogleDriveDialog",$("body"),{displayName:'Upload File'});
			},
			"click;.search":function(event){
				var $form = $(event.target).closest(".search-form");
				var params = {};
				var keyword = $form.find(".keyword").val();
				var searchType;
				$form.find("[name='searchType']").each(function(index,e){
					if($(e).prop("checked")){
						searchType = $(e).val();
					}
				});
				if(keyword){
					params.keyword = keyword;
				}
				params.searchType = searchType;
				var startDate = $form.find("[name='startDate']:first").val();
				var endDate = $form.find("[name='endDate']").val();
				if(startDate){
					params.startDate = startDate;
				}
				if(endDate){
					params.endDate = endDate;
				}
				brite.display("GoogleDriveFiles",".GoogleScreen-content",{
					results: function(){
					    return app.googleDriveApi.searchFile(params);
				    }
				});
			},  
			"click;.download":function(event){
				var fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
	        	var fileName = $(event.currentTarget).closest("tr").attr("data-fileName");
	        	var fileUrl = $(event.currentTarget).closest("tr").attr("data-hasUrl");
	        	if(fileId && fileUrl == "true"){
	        		window.location.href=contextPath+"/googleDrive/download?fileId="+fileId+"&fileName="+fileName;
	        	}else{
	        		alert("This file is not support download!");
	        	}
			},
			 "click;.trash":function(event){
			    var parma = {};
            	parma.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
                app.googleDriveApi.trashFile(parma).done(function (success) {
                    if(success){
                    	alert("Trash success");
                    }else{
                    	alert("Trash fail");
                    }
                    brite.display("GoogleDriveFiles",".GoogleScreen-content");
                });
			},
			 "click;.delete":function(event){
				    var parma = {};
	            	parma.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
	                app.googleDriveApi.deleteFile(parma).done(function (success) {
	                    if(success){
	                    	alert("Delete success");
	                    }else{
	                    	alert("Delete fail");
	                    }
	                    brite.display("GoogleDriveFiles",".GoogleScreen-content");
	                });
			}
        },

        docEvents: {
            "DO_REFRESH_DOCS":function(){
                 var view = this;
                 showFile.call(view);
             }
        },
        daoEvents: {
        }
    });
    

    function showFile() {
    	var view = this;
        return brite.display("DataTable", ".files-container", {
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
                    text:"FileSize",
                    attrs: "style='width:10%; word-break: break-word;'",
                    render:function(obj){
                    	return app.util.formatWithUnit(obj.fileSize);
                    }
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
                        return "<span> <a src=\"#\" class=\"download\">"+"download"+"</a><a src=\"#\" class=\"trash\">"+"trash"+"</a> <a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
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