;
(function () {
        brite.registerView("GoogleDriveFolder", {emptyParent:false}, {
            create:function (data, config) {
                this.model = {};
                if(data||data.callback) {
                    this.model.callback = data.callback;
                }
                return app.render("tmpl-GoogleDriveFolder", {data:data});
            },
            postDisplay:function(){
            	$(".move").addClass("disabled");
            	$(".copy").addClass("disabled");
            	param = {};
            	app.googleDriveApi.foldersInfo(param).done(function (result) {
            		 $("div.itemDiv").empty();
             		 brite.display("GoogleDriveSubFolder",$("div.itemDiv"),{result:result.result,root:true});
                 });
    		},
    		events:{
    			"click;.dialogCloseBtn":function(event){
    				this.$el.remove();
    			},
    			"click;.btn.cancel":function(event){
    				this.$el.remove();
    			},
    			"click;.driveFolderPlus":function(event){
    				var expandIco = $(event.target);
    				if(expandIco.attr("class")==="icon-plus"){
        				var param = {};
        				param.parentId = $(event.target).closest(".folderitem").attr("data-selfId");
                      	app.googleDriveApi.foldersInfo(param).done(function (result) {
                      		 brite.display("GoogleDriveSubFolder",$(expandIco).closest("div.itemDiv"),{result:result.result});
                          });
        				expandIco.removeClass("icon-plus").addClass("icon-minus");
        				expandIco.closest(".folderitem").find(".icon-folder-close").removeClass("icon-folder-close").addClass("icon-folder-open");
    				}else if(expandIco.attr("class")==="icon-minus"){
    					var subFolder = $(expandIco).closest("div").next();
    					$(subFolder).remove();
        				expandIco.removeClass("icon-minus").addClass("icon-plus");
        				expandIco.closest(".folderitem").find(".icon-folder-open").removeClass("icon-folder-open").addClass("icon-folder-close");
    				}
    			},
    			"click;.foldername":function(event){
    				$(".foldername.select").toggleClass("select");
    				$(event.target).toggleClass("select");
    				if($(".move").hasClass("disabled"))
    					$(".move").removeClass("disabled");
    				if($(".copy").hasClass("disabled"))
    					$(".copy").removeClass("disabled");
    			},
    			"click;.move":function(event){
    				var moveBtn = $(event.target);
    				var view = this;
    				var param = {};
    				param.fileId = $(".dialogBody").attr("data-fileId");
    				param.parentId = $(".dialogBody").attr("data-parentId");
    				param.moveId = $(".foldername.select").attr("data-selfId");
    				if(param.parentId === param.moveId){
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
    			},
    			"click;.copy":function(event){
    				var copyBtn = $(event.target);
    				var view = this;
    				var param = {};
    				var parentId = $(".dialogBody").attr("data-parentId");
    				param.fileId = $(".dialogBody").attr("data-fileId");
    				param.parentId = $(".dialogBody").attr("data-parentId");
    				param.copyTitle= $(".copyTitle").val();
    				param.targetId = $(".foldername.select").attr("data-selfId");
    				if($(copyBtn).hasClass("disabled"))
    					return false;
    				$(copyBtn).addClass("disabled");
    				app.googleDriveApi.copyFile(param).done(function(result){
    					 if(result.success === true){
		                    	alert("copy success");
		                    }else{
		                    	alert("copy fail");
		                    }
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