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
			"click;.btnPrevious":function(e){
				var param = {}
				param.currentId = $(e.target).attr("data-currentId");
				brite.display("GoogleDriveFiles",".GoogleScreen-content",{
					results: function(){
						return app.googleDriveApi.previousList(param);
					}
				});
			},
			"click;.btnUpload":function(e){
				var currentId = $(e.target).attr("data-currentId");
				brite.display("GoogleDriveDialog",$("body"),{parentId:currentId,displayName:'Upload File'});
			},
			"click;.btnCreateFolder":function(e){
				var view = this;
				var parentId = $(e.target).attr("data-currentId");
				brite.display("InputValue", ".MainScreen", {
					title: 'Create Folder',
					fields: [
						{label:"Folder Name", name:'folderName', mandatory:false}
					],
					callback: function (params) {
						var params = params || {};
						params.parentId = parentId;
						app.googleDriveApi.createFolder(params).done(function (result) {
							if(result.success === true){
								alert("CreateFolder success");
							}else{
								alert("CreateFolder fail");
							}
							var params = {};
							params.selfId = parentId;
							brite.display("GoogleDriveFiles",".GoogleScreen-content",{
								results: function(){
									return app.googleDriveApi.childList(params);
								}
							});
						});
					}
				});
			},
			"click;.btnStarred":function(event){
				var params= {};
				params.starred = true;
				brite.display("GoogleDriveFiles",".GoogleScreen-content",{
					results: function(){
						return app.googleDriveApi.searchFile(params);
					}
				});


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
			"btap; .starEvent": function(event){
				var view = this;
				var $e = view.$el;
				var $btn = $(event.currentTarget);
				$btn.toggleClass("active");
				var $tr = $btn.closest("tr");
				var $activeBtn = $tr.find(".starEvent.active");
				if($activeBtn.length > 0){
					$btn.removeClass("glyphicon-star-empty").addClass("glyphicon-star");
					starred = true;
				}else{
					$btn.removeClass("glyphicon-star").addClass("glyphicon-star-empty");
					starred = false;
				}
				var fileId = $tr.attr("data-fileid");
				if (fileId) {
					app.googleDriveApi.updateStarred({fileId:fileId, starred:starred});
				}
			},
			"click;.patch":function(event){
				var view = this;
				var parentId = $(".btnUpload").attr("data-currentId");
				var fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
				brite.display("InputValue", ".MainScreen", {
					title: 'Patch File',
					fields: [
						{label:"New Title", name:'title', mandatory:false},
						{label:"Description", name:"description", mandatory:false},
					],
					callback: function (param) {
						var param = param || {};
						param.fileId = fileId;
						app.googleDriveApi.patchFile(param).done(function (result) {
							if(result.success === true){
								alert("Patch success");
							}else{
								alert("Patch fail");
							}
							var params = {};
							params.selfId = parentId;
							view.param = params;
							openFolder.call(view);
						});
					}
				});
			},
			"click;.trash":function(event){
				var view = this;
				var param = {};
				var parentId = $(".btnUpload").attr("data-currentId");
				param.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
				app.googleDriveApi.trashFile(param).done(function (result) {
					if(result.success === true){
						alert("Trash success");
					}else{
						alert("Trash fail");
					}
					var params = {};
					params.selfId = parentId;
					view.param = params;
					openFolder.call(view);
				});
			},
			"click;.touch":function(event){
				var view = this;
				var param = {};
				var parentId = $(".btnUpload").attr("data-currentId");
				param.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
				app.googleDriveApi.touchFile(param).done(function (result) {
					if(result.success === true){
						alert("Touch success");
					}else{
						alert("Touch fail");
					}
					var params = {};
					params.selfId = parentId;
					view.param = params;
					openFolder.call(view);
				});
			},
			"click;.delete":function(event){
					var view = this;
					var param = {};
					var parentId = $(".btnUpload").attr("data-currentId");
					param.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
					app.googleDriveApi.deleteFile(param).done(function (result) {
						if(result.success === true){
							alert("Delete success");
						}else{
							alert("Delete fail");
						}
						var params = {};
						params.selfId = parentId;
						view.param = params;
						openFolder.call(view);
					});
			},
			"click;.copy":function(event){
				var parentId = $(".btnUpload").attr("data-currentId");
				var fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
				var fileName = $(event.currentTarget).closest("tr").attr("data-fileName");
				brite.display("GoogleDriveFolder", $("body"), {
					move:false,
					displayName:'Copy to ...',
					fileName:fileName,
					fileId:fileId,
					parentId:parentId
				});
			},
			"click;.fileSelf":function(event){
				var view = this;
				var param = {};
				param.selfId = $(event.currentTarget).closest("tr").attr("data-fileId");
				mimeType = $(event.currentTarget).closest("tr").attr("data-mimeType");
				if(mimeType == "application/vnd.google-apps.folder"){
					view.param = param;
					openFolder.call(view);
				}else{
					param.fileName = $(event.currentTarget).closest("tr").attr("data-fileName");
					param.fileUrl = $(event.currentTarget).closest("tr").attr("data-hasUrl");
					view.param = param;
					download.call(view);
				}
			},
			"click;.move":function(e){
				var fileId = $(event.target).closest("tr").attr("data-fileId");
				var parentId = $(event.target).closest("tr").attr("data-currentId");
				if(fileId && parentId){
					brite.display("GoogleDriveFolder", $("body"), {
						move:true,
						displayName:'Move to ...',
						fileId:fileId,
						parentId:parentId
					});
				}
				return false;
			},

			// add drag event
			"bdragstart; tr.contextMenu td:nth-child(2)" : function(event) {
				event.stopPropagation();
				event.preventDefault();
				var view = this;
				var $e = view.$el;
				var $tr= $(event.currentTarget).closest("tr");
				var $dragDiv = $("<div id='dragDiv' data-currentid='"+$tr.attr("data-currentid")+"' data-id='"+$tr.attr("data-fileid")+"' data-name='"+$tr.attr("data-filename")+"'>drag: <span class='file' >"+$tr.attr("data-filename")+"</span></div>");

				$e.append($dragDiv);

				$dragDiv.offset({
					left: event.bextra.pageX,
					top: event.bextra.pageY
				});

			},
			"bdragmove; tr.contextMenu td:nth-child(2)" : function(event) {
				event.stopPropagation();
				event.preventDefault();
				var view = this;
				var $e = view.$el;
				var $dragDiv = $e.find("#dragDiv");
				$dragDiv.offset({
					left: event.bextra.pageX,
					top: event.bextra.pageY
				});

				$e.find(".dataTable .table-striped tbody tr td:nth-child(2)").removeClass("holderSpace");
				$e.find(".dataTable .table-striped tbody tr td:nth-child(2)").each(function(idx, td) {
					var $td = $(td);
					var tpos = $td.offset();
					if (event.bextra.pageY > tpos.top && event.bextra.pageY < tpos.top + $td.outerHeight() 
						&& event.bextra.pageY > tpos.left && event.bextra.pageY < tpos.left + $td.outerWidth()
						&& $td.closest("tr").attr("data-mimetype") == 'application/vnd.google-apps.folder') {
						$td.addClass("holderSpace");
					}
				});
			},
			"bdragend; tr.contextMenu td:nth-child(2)" : function(event){
				event.stopPropagation();
				event.preventDefault();
				var view = this;
				var $e = view.$el;
				var $dragDiv = $e.find("#dragDiv");

				if($e.find(".dataTable .holderSpace").size() > 0){
					var $holder = $e.find(".dataTable .holderSpace");
					var $holderTr = $holder.closest("tr");
					var param = {};
					param.fileId = $dragDiv.attr("data-id");
					param.moveId = $holderTr.attr("data-fileid");
					param.parentId = $dragDiv.attr("data-currentid");
					app.googleDriveApi.moveFile(param).done(function(result){				
						showFile.call(view);
					});
				}
				$dragDiv.remove();
				$e.find(".dataTable .table-striped tbody tr td:nth-child(2)").removeClass("holderSpace");
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
		var param = this.param || {};
		brite.display("GoogleDriveFiles",".GoogleScreen-content",{
				results: function(){
					return app.googleDriveApi.childList(param);
				}
			});
	}
	
	function download(){
		var param = this.param || {};
		if(param.selfId && param.fileUrl == "true"){
			window.location.href=contextPath+"/googleDrive/download?fileId="+param.selfId+"&fileName="+param.fileName;
		}else{
			alert("This file is not support download!");
		}
	}

	function showFile() {
		var view = this;
		return brite.display("DataTable", ".files-container", {
			dataProvider: {list: view.results},
			rowAttrs: function (obj) {
				return "class=\"contextMenu\" data-fileId='{0}' data-fileName='{1}' data-hasUrl='{2}' data-currentId='{3}' data-mimeType='{4}'".format(obj.fileId,obj.fileName,obj.hasUrl||"false",obj.parentId||"",obj.mimeType)
			},
			onDone: function(data){
				$(".btnPrevious").attr({"data-currentId":data.parentId});
				$(".btnUpload").attr({"data-currentId":data.parentId});
				$(".btnCreateFolder").attr({"data-currentId":data.parentId});
				if(data.previous === false){
					alert("Can't get the previous folder!");
				}
			},
			columnDef:[
				{
					text:"",
					render:function(obj){
						return (obj.starred) ? "<div class='glyphicon glyphicon-star starEvent active'></div>" : "<div class='glyphicon glyphicon-star-empty starEvent'></div>";
					},
					attrs: "style='width: 2%'"
				},
				{
					text:"FileName",
					attrs: "style='width:15%; word-break: break-word; cursor:pointer;'",
					render:function(obj){
						return "<a src=\"#\" class=\"fileSelf\"><span><img src=\"{0}\" alt=\"img\"></img>&nbsp;&nbsp;<span>{1}</span></span></a>".format(obj.iconLink,obj.fileName);
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
							functionString = "<span><a src=\"#\" class=\"patch\">"+"patch"+"</a> <a src=\"#\" class=\"touch\">"+"touch"+"</a> <a src=\"#\" class=\"move\">"+"move"+"</a> <a src=\"#\" class=\"trash\">"+"trash"+"</a> <a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
						}else{
							functionString = "<span><a src=\"#\" class=\"patch\">"+"patch"+"</a> <a src=\"#\" class=\"touch\">"+"touch"+"</a> <a src=\"#\" class=\"copy\">"+"copy"+"</a> <a src=\"#\" class=\"move\">"+"move"+"</a> <a src=\"#\" class=\"trash\">"+"trash"+"</a> <a src=\"#\" class=\"delete\">"+"delete"+"</a> </span>";
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