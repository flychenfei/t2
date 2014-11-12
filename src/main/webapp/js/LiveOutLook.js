(function(){
	
	brite.registerView("LiveOutLook",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			return showUserInfo.call(this);
		},
		postDisplay: function () {
			
		},
		events:{
			
		}
	});

	function showUserInfo(){
		var dfd = $.Deferred();
		app.liveOutLookApi.getUserInfo().done(function(result){
			if(result.success){
				var html =  app.render("tmpl-LiveOutLook",result.result);
				dfd.resolve(html);
			}
		});
		return dfd.promise();
	}
})();