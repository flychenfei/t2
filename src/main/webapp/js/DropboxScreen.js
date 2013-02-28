(function(){
	brite.registerView("DropboxScreen",{parent:".MainScreen-main",emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-DropboxScreen");
		},
		postDisplay:function(){
			app.dropboxApi.getAccountInfo().pipe(function(account){
				console.log(account);
				brite.display("DropboxAccountInfo",$(".tab-content"),{account:account.result});
			});
		},
		events:{
			"click;.nav-tabs li":function(event){
				this.$el.find("li").removeClass("active");
				var $li = $(event.currentTarget);
				$li.addClass("active");
				var menu = $li.attr("data-nav");
				$(".tab-content").html("<div class=\"alert alert-info\">Tring to load data,Please wait...</div>");
				if(menu=="accountInfo"){
					app.dropboxApi.getAccountInfo().pipe(function(account){
						console.log(account);
						brite.display("DropboxAccountInfo",$(".tab-content"),{account:account.result});
					});
				}else if(menu=="files"){
					app.dropboxApi.getMetadata().pipe(function(metadata){
						metadata = metadata.result;
						brite.display("DropboxFiles",$(".tab-content"),{metadata:metadata});
					});
				}
			}
		}
	});
})();