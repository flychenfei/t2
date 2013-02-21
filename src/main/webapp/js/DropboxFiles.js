(function(){
	brite.registerView("DropboxFiles",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxFiles",{metadata:data.metadata});
		},
		postDisplay:function(){
			$("img[data-thumb='true']").each(function(e,index){
				$(this).attr("src",contextPath+"/dropbox/thumbnails"+$(this).closest("tr").attr("data-path"));
			});
		},
		events:{
			"click;.pointer":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				var isDir = $(event.target).closest("tr").attr("data-dir");
				$(".loading").toggleClass("hide");
				app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
					metadata = metadata.result;
					brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
					$(".loading").toggleClass("hide");
				});
			},
			"click;.download":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				
				$.ajax("dropbox/getFile",{type:"Get",data:{path:path}});
				/*app.dropboxApi.getFile({path:path}).pipe(function(){
					
				});*/
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
})();