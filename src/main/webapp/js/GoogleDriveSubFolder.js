(function(){
	brite.registerView("GoogleDriveSubFolder",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GoogleDriveSubFolder",{data:data});
		}
	});
})();