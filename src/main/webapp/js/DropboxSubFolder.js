(function(){
	brite.registerView("DropboxSubFolder",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-DropboxSubFolder",{data:data});
		}
	});
})();