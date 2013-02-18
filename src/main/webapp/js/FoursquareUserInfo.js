(function(){
	brite.registerView("FoursquareUserInfo",{emptyParent:true, parent:".FoursquareScreen-content"},{
		create:function(data,config){
            return showUserInfo.call(this);
		},
		events:{

		}
	});

    function showUserInfo(){
        var dfd = $.Deferred();
        app.foursquareApi.getUserInfo().done(function(result){

            if(result.result.meta.code=='200'){
               var html =  app.render("tmpl-FoursquareUserInfo",result.result.result);
               console.log(html)
               dfd.resolve(html);
            }
        });
        return dfd.promise();

    }
})();