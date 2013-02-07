(function(){
	brite.registerView("FoursquareUserInfo",{emptyParent:true},{
		create:function(data,config){
            showUserInfo.call(this);
		},
		events:{

		}
	});

    function showUserInfo(){
        console.log("XXXXXXXXXX")
        app.foursquareApi.getUserInfo().done(function(result){
            console.log(result)
            if(result.result.meta.code=='200'){
               return  app.render("tmpl-FoursquareUserInfo",result.result);
            }
        })

    }
})();