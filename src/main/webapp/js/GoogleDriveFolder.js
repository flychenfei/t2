;
(function () {
        brite.registerView("DriveFolder", {emptyParent:false}, {
            create:function (data, config) {
                this.model = {};
                if(data||data.callback) {
                    this.model.callback = data.callback;
                }
                return app.render("tmpl-DriveFolder", {data:data});
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
    			"click;.driveFolderPlus":function(event){
    				alert("not implement!");
    				/*var expandIco = $(event.target);
    				var param = {};
    				param.parentId = $(event.target).closest(".folderitem").attr("data-selfId");
                  	app.googleDriveApi.foldersInfo(param).done(function (result) {
                  		 result=result.result;
                  		 brite.display("DriveSubFolder",$(expandIco).closest("div.itemDiv"),{result:result});
                      });*/
    			},
    			"click;.foldername":function(event){
    				$(".foldername.select").toggleClass("select");
    				$(event.currentTarget).toggleClass("select");
    			},
    			"click;.move":function(event){
    				var moveBtn = $(event.target);
    				var view = this;
    				var param = {};
    				param.fileId = $(".dialogBody").attr("data-fileId");
    				param.parentId = $(".dialogBody").attr("data-parentId");
    				param.moveId = $(".foldername.select").attr("data-selfId");
    				if(param.fileId === param.moveId){
    					alert("Can't move file to itself!");
    					return false;
    				}
    				if($(moveBtn).hasClass("disabled"))
    					return false;
    				$(moveBtn).addClass("disabled");
    				app.googleDriveApi.moveFile(param).done(function(result){
    					 if(result.success === true){
		                    	alert("move success");
		                    }else{
		                    	alert("move fail");
		                    }
    					view.$el.remove();
    					var params = {};
                    	params.selfId = param.parentId;
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