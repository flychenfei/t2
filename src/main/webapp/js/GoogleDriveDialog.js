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
			"click;.btn.upload":function(e){
				var uploadBtn = $(e.target);
				var parentId = $(".dialogBody").attr("data-parentid");
				var view = this;
				if($(uploadBtn).hasClass("disabled"))
					return false;
				$(uploadBtn).toggleClass("disabled");
				app.ajaxPost(contextPath+"/googleDrive/upload",{parentId:parentId},$(":input[type='file']")[0].files[0]).done(function(){
					view.$el.remove();
					var params = {};
					params.selfId = parentId;
					brite.display("GoogleDriveFiles",".GoogleScreen-content",{
						results: function(){
							return app.googleDriveApi.childList(params);
						}
					});
				});
			}
		}
	});
})();