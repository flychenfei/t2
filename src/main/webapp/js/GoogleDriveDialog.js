(function(){
	brite.registerView("GoogleDriveDialog",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GoogleDriveDialog",{data:data});
		},
		postDisplay:function(){
			
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			},
			"click;.btn.upload":function(){
				var uploadBtn = $(event.target);
				var view = this;
				if($(uploadBtn).hasClass("disabled"))
					return false;
				$(uploadBtn).toggleClass("disabled");
				console.log($(":input[type='file']")[0].files[0]);
				app.ajaxPost(contextPath+"/googleDocsList/upload",{},$(":input[type='file']")[0].files[0]).done(function(){
					view.$el.remove();
					brite.display("GoogleDocs",".GoogleScreen-content");
				});
			}
		}
	});
})();