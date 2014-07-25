(function(){
	brite.registerView("LinkedInGroupPost",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-LinkedInGroupPost",{data:data});
		}
	});
})();