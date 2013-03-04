(function(){
	brite.registerView("GithubDialog",{emptyParent:false},{
		create:function(data,config){
			return app.render("tmpl-GithubDialog",{data:data});
		},
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			},
			"click;.btn.cancel":function(event){
				this.$el.remove();
			}
		}
	})
})();