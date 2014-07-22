(function(){
	brite.registerView("DriveSubFolder",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-DropboxSubFolder",{data:data});
		}
	});
})();