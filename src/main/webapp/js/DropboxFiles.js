(function(){
	brite.registerView("DropboxFiles",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxFiles",{metadata:data.metadata});
		},
		postDisplay:function(){
			$("img[data-thumb='true']").each(function(e,index){
				$(this).attr("src",contextPath+"/dropbox/thumbnails"+$(this).closest("tr").attr("data-path"));
			});
			$(".loading").toggleClass("hide");
		},
		events:{
			"click;.pointer":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				var isDir = $(event.target).closest("tr").attr("data-dir");
				$(".loading").toggleClass("hide");
				app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
					metadata = metadata.result;
					brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
				});
			},
			"click;.download":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				$.ajax("dropbox/getFile",{type:"Get",data:{path:path}});
			},
			"click;.s_web_folder_add":function(event){
				var path = $(event.target).closest("span").attr("data-path");
				brite.display("DropboxDialog",$("body"),{path:path});
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
	Handlebars.registerHelper('filesize', function(size) {
		if(size=="0 bytes")
			return new Handlebars.SafeString("--");
		return new Handlebars.SafeString(size);
		});
	Handlebars.registerHelper('localDate', function(date) {
		var dateTimeObject = new Date(date);
		return new Handlebars.SafeString(dateTimeObject.toLocaleString());
		});
})();