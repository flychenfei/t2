(function(){
	
	brite.registerView("LinkedInCurrentUserInfo",{emptyParent:true},{

		create: function(data,config){
			return render("tmpl-LinkedInCurrentUserInfo",{data:data});
		},
		postDisplay: function () {
        	
        },
		events:{
			
		}
		
	});

})();