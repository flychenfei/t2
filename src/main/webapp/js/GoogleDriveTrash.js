;
(function ($) {
	brite.registerView("GoogleDriveTrash",{parent:".GoogleScreen-content",emptyParent:true}, {
		create: function (data, config) {
			if(data && data.results) {
				 this.results = data.results;
			}else{
				this.results = app.googleDriveApi.trashList;
			}
			return app.render("tmpl-GoogleDriveTrash");
		},

		postDisplay: function (data, config) {
			var view = this;
			showTrash.call(view);
		},

		events: {
			"click;.btnRestoreTrash":function(e){
				var parma = {};
				app.googleDriveApi.restoreTrash(parma).done(function (result) {
					if(result.success === true){
						alert("RestoreTrash success");
					}else{
						alert("RestoreTrash fail");
					}
					brite.display("GoogleDriveTrash",".GoogleScreen-content");
				});
			},
			"click;.btnEmptyTrash":function(e){
				var parma = {};
				app.googleDriveApi.emptyTrash(parma).done(function (result) {
					if(result.success === true){
						alert("EmptyTrash success");
					}else{
						alert("EmptyTrash fail");
					}
					brite.display("GoogleDriveTrash",".GoogleScreen-content");
				});
			},
			 "click;.restore":function(event){
					var parma = {};
					parma.fileId = $(event.currentTarget).closest("tr").attr("data-fileId");
					app.googleDriveApi.untrashFile(parma).done(function (result) {
						if(result.success === true){
							alert("Restore success");
						}else{
							alert("Restore fail");
						}
						brite.display("GoogleDriveTrash",".GoogleScreen-content");
					});
			}
		},

		docEvents: {
			"DO_REFRESH_DOCS":function(){
				var view = this;
				showTrash.call(view);
			}
		},
		daoEvents: {
		}
	});
	function showTrash() {
		var view = this;
		return brite.display("DataTable", ".trash-container", {
			dataProvider: {list: view.results},
			rowAttrs: function (obj) {
				return " data-fileId='{0}' data-fileName='{1}' data-hasUrl='{2}'".format(obj.fileId,obj.fileName,obj.hasUrl||"false")
			},
			columnDef:[
				{
					text:"FileName",
					attrs: "style='width:15%; word-break: break-word;'",
					render:function(obj){return "<span><img src=\"{0}\" alt=\"img\"></img><span>{1}</span></span>".format(obj.iconLink,obj.fileName);}
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
					render:function(obj){return app.util.formatWithUnit(obj.fileSize);}
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
						return "<span> <a src=\"#\" class=\"restore\" style=\"cursor:pointer;\">"+"restore"+"</a></span>";
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