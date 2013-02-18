(function(){
	brite.registerView("DropboxScreen",{parent:".MainScreen-main",emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxScreen");
		},
		events:{
			"click;.btn":function(event){
			/*	 var params = {mehotd:"Get"};
				params.oauth_consumer_key = "4cc6msigipo5b67";
				params.oauth_signature="x6qtympv9ibikzm";
				params.oauth_version = "1.0";
				params.oauth_timestamp=1361166735;
				app.getJsonData("https://api.dropbox.com/1/oauth/request_token",params).pipe(function(json){
					console.log(json);
				});*/
				app.oauth.authorize("Dropbox");
			}
		}
	});
})();