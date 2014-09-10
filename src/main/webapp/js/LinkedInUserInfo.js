(function(){
	
	brite.registerView("LinkedInUserInfo", {emptyParent:false}, {
		create: function(data,config){
			return render("tmpl-LinkedInUserInfo",{data:data});
		},
		postDisplay: function () {
        	
        },
		events:{
			"click;.dialogCloseBtn":function(event){
				this.$el.remove();
			}
		}
	});

})();