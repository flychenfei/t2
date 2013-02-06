(function(){
	brite.registerView("GithubRepositories",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubRepositories",{repositories:data.repositories});
		}
	});
	
	Handlebars.registerHelper('date', function(dateObj) {
		 var year = parseInt(dateObj.year)+1900;
		 var month = parseInt(dateObj.month)+1;
		 var date = dateObj.date;
		 return new Handlebars.SafeString(
			year+"-"+month+"-"+date
		 );
		});
})();