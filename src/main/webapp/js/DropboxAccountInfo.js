(function(){
	brite.registerView("DropboxAccountInfo",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxAccountInfo",{account:data.account});
		},
		postDisplay:function(){
			if(!$(".loading").hasClass("hide"))
				$(".loading").addClass("hide");
		}
	})
})();