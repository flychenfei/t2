(function(){
	brite.registerView("FoursquareUserInfo",{emptyParent:true},{
		create:function(data,config){
            showUserInfo.call(this);
		},
		events:{

		}
	});

    function showUserInfo(){
        app.foursquareApi.getUserInfo().done(function(result){
            return  app.render("tmpl-FoursquareUserInfo",result.result);
        })

    }
})();