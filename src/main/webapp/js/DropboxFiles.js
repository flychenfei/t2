(function(){
	brite.registerView("DropboxFiles",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxFiles",{metadata:data.metadata});
		},
		events:{
			"click;.pointer":function(event){
				var path = $(event.target).closest("tr").attr("data-path");
				app.dropboxApi.getMetadata({path:path}).pipe(function(metadata){
					metadata = metadata.result;
					brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
				});
			}
		}
	});
})();

(function(){
	Handlebars.registerHelper('filename', function(filename) {
		if(filename=="/")
			return new Handlebars.SafeString("Dropbox");
		return new Handlebars.SafeString(
				filename.substring(1)
		  );
		});
})();