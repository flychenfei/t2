(function(){
	brite.registerView("DropboxFiles",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxFiles",{metadata:data.metadata,showDeleted:data.showDeleted});
		},
		postDisplay:function(){
			$("img[data-thumb='true']").each(function(e,index){
				$(this).attr("src",contextPath+"/dropbox/thumbnails"+$(this).closest("tr").attr("data-path"));
			});
			if(!$(".loading").hasClass("hide"))
				$(".loading").addClass("hide");
		},
		events:{
			"click;.pointer":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				var isDir = $(event.target).closest("tr").attr("data-dir");
				$(".loading").removeClass("hide");
				$(".loading").addClass("hide");
				if(isDir)//if dir,show the files under this folder
					app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
					});
				else//if file,show the file content
					app.dropboxApi.getMedia({path:path}).pipe(function(media){
						media = media.result;
						window.open(media.url,"","height=400,width=600")
					});
					
			},
			"click;.download":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				$.ajax("dropbox/getFile",{type:"Get",data:{path:path}});
			},
			"click;.s_web_folder_add":function(event){
				var path = $(event.target).closest(".commonoperation").attr("data-path");
				brite.display("DropboxDialog",$("body"),{path:path,type:"folder",displayName:'Create Folder'});
			},
			"click;.upload":function(event){
				var path = $(event.target).closest(".commonoperation").attr("data-path");
				brite.display("DropboxDialog",$("body"),{path:path,type:"upload",displayName:'Upload File'});
			},
			"click;.delete":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				$(".loading").toggleClass("hide");
				app.dropboxApi.delete({path:path}).pipe(function(json){
					app.dropboxApi.getMetadata({path:path.substring(0,path.lastIndexOf("/"))}).pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
					});
				});
			},
			"click;.share":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				app.dropboxApi.share({path:path}).pipe(function(json){
					brite.display("DropboxDialog",$("body"),{type:"sharelink",sharelink:json.result.url,displayName:'Share Link'});
				});
			},
			"click;.s_web_show-deleted,.s_web_hide-deleted":function(event){//show deleted files
				$(".loading").toggleClass("hide");
				var path = $(event.target).closest(".commonoperation").attr("data-path");
				var showDeleted = $(event.target).hasClass("s_web_show-deleted");
				app.dropboxApi.getMetadata({path:path,include_deleted:showDeleted}).pipe(function(metadata){
					metadata = metadata.result;
					brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata,showDeleted:showDeleted});
					
				});
			},
			"click;.restore":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				var rev = $(event.target).closest("tr").attr("data-rev");
				app.dropboxApi.getRevisions({path:path}).pipe(function(json){
					brite.display("DropboxDialog",$("body"),{revisions:JSON.parse(json.result),type:'restore',displayName:'Restore File'});
				});
			},
			"click;.copy":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				app.dropboxApi.getMetadata({path:"/"}).pipe(function(metadata){
					brite.display("DropboxDialog",$("body"),{metadata:metadata.result,path:path,type:'copy',displayName:'Copy to...'});
				});
			},
			"click;.move":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				app.dropboxApi.getMetadata({path:"/"}).pipe(function(metadata){
					brite.display("DropboxDialog",$("body"),{metadata:metadata.result,path:path,type:'move',displayName:'Move to...'});
				});
			},
			"click;.search":function(event){
				var path = $(event.target).closest(".commonoperation").attr("data-path");
				var query = $(":input.searchbox").val();
				app.dropboxApi.search({path:path,query:query}).pipe(function(searchResult){
					searchResult = JSON.parse(searchResult.result);
					if(searchResult.error){
						alert(searchResult.error.query);
						return false;
					}
					var metadata = {contents:searchResult,search:true};
					brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
				});
			}
		}
	});
})();

(function(){
	Handlebars.registerHelper('filepath', function(filename) {
		if(filename=="/")
			return new Handlebars.SafeString("Dropbox");
		return new Handlebars.SafeString(
				filename.substring(1)
		  );
		});
	Handlebars.registerHelper('filename', function(filename) {
		return new Handlebars.SafeString(
				filename.substring(filename.lastIndexOf("/")+1)
		  );
		});
	Handlebars.registerHelper('filesize', function(size,deleted) {
		if(deleted)
			return new Handlebars.SafeString("Deleted");
		if(size=="0 bytes")
			return new Handlebars.SafeString("--");
		return new Handlebars.SafeString(size);
		});
	Handlebars.registerHelper('localDate', function(date) {
		var dateTimeObject = new Date(date);
		return new Handlebars.SafeString(dateTimeObject.toLocaleString());
		});
	Handlebars.registerHelper('isEmpty', function(list,options) {
		 if(list.length==0)
			 return options.fn(this);
		 return options.inverse(this);
	});
	
})();