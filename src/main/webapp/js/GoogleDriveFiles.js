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
        		var parentId = $(e.target).attr("data-parentId");
        		brite.display("GoogleDriveDialog",$("body"),{parentId:parentId,displayName:'Upload File'});
			},
			"click;.btnCreateFolder":function(e){
				var parentId = $(e.target).attr("data-parentId");
				brite.display("InputValue", ".MainScreen", {
                    title: 'Create Folder',
                    fields: [
                        {label:"Folder Name", name:'folderName', mandatory:false}
                    ],
                    callback: function (params) {
                    	var params = params || {};
                    	params.parentId = parentId;
                    	app.googleDriveApi.createFolder(params);
                    	brite.display("GoogleDriveFiles",".GoogleScreen-content");
                    }});
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
			},
			"click;.copy":function(event){
					var parma = {};
					parma.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
					parma.copyTitle = $(event.currentTarget).closest("tr").attr("data-fileName");
					app.googleDriveApi.copyFile(parma).done(function (success) {
		                    if(success){
		                    	alert("Copy success");
		                    }else{
		                    	alert("Copy fail");
		                    }
		                    brite.display("GoogleDriveFiles",".GoogleScreen-content");
		               });
			},
			"click;.fileSelf":function(event){
				var view = this;
				var parma = {};
            	parma.selfId = $(event.currentTarget).closest("tr").attr("data-fileId");
				mimeType = $(event.currentTarget).closest("tr").attr("data-mimeType");
            	if(mimeType == "application/vnd.google-apps.folder"){
            		view.parma = parma;
            		openFolder.call(view);
            	}else{
    			    parma.fileName = $(event.currentTarget).closest("tr").attr("data-fileName");
    			    parma.fileUrl = $(event.currentTarget).closest("tr").attr("data-hasUrl");
    			    view.parma = parma;
    			    download.call(view);
            	}
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

    function openFolder(){
    	 var parma = this.parma || {};
    	 brite.display("GoogleDriveFiles",".GoogleScreen-content",{
				results: function(){
				    return app.googleDriveApi.childList(parma);
			    }
			});
    }
    
    function download(){
    	var parma = this.parma || {};
    	if(parma.selfId && parma.fileUrl == "true"){
    		window.location.href=contextPath+"/googleDrive/download?fileId="+parma.selfId+"&fileName="+parma.fileName;
    	}else{
    		alert("This file is not support download!");
    	}
   }

    function showFile() {
    	var view = this;
        return brite.display("DataTable", ".files-container", {
        	dataProvider: {list: view.results},
        	rowAttrs: function (obj) {
                return " data-fileId='{0}' data-fileName='{1}' data-hasUrl='{2}' data-parentId='{3}' data-mimeType='{4}'".format(obj.fileId,obj.fileName,obj.hasUrl||"false",obj.parentId||"",obj.mimeType)
            },
            onDone: function(Data){
            	$(".btnUpload").attr({"data-parentId":Data.parentId});
            	$(".btnCreateFolder").attr({"data-parentId":Data.parentId});
            },
            columnDef:[
                {
                    text:"FileName",
                    attrs: "style='width:15%; word-break: break-word; cursor:pointer;'",
                    render:function(obj){
                    	return "<a src=\"#\" class=\"fileSelf\"><span><img src=\"{0}\" alt=\"img\"></img><span>{1}</span></span></a>".format(obj.iconLink,obj.fileName);
                    	}
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
                    text:"MimeType",
                    attrs: "style='width:20%; word-break: break-word;'",
                    render:function(obj){return obj.mimeType}
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
                    attrs: "style='width:10%; cursor:pointer;'",
                    render: function (obj) {
                    	var functionString = "";
                    	if(obj.mimeType == "application/vnd.google-apps.folder"){
                    		functionString = "<span><a src=\"#\" class=\"trash\">"+"trash"+"</a> <a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
                    	}else{
                    		functionString = "<span><a src=\"#\" class=\"copy\">"+"copy"+"</a> <a src=\"#\" class=\"trash\">"+"trash"+"</a> <a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
                    	}
                        return functionString;
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