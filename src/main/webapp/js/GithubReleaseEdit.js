(function(){
    brite.registerView("GithubReleaseEdit",{emptyParent:false},{
        create:function(data,config){
            return app.render("tmpl-GithubReleaseEdit",{
                id:data.id,
                name:data.name,
                login: data.login,
                repoName: data.repoName,
                layout:data.layout
            });
        },
        events:{
            "click;.dialogCloseBtn":function(event){
                this.$el.remove();
            },
            "click;.btn.cancel":function(event){
                this.$el.remove();
            },
            "click;.btn.save":function(event){
                var dialogContent = $(event.target).closest(".dialogContent");
                var dialogBody = $(event.target).closest(".dialogBody");
                var repoName =  dialogBody.attr("data-repository-name");
                var login = dialogBody.attr("data-login");
                var releaseId = dialogBody.attr("data-release-id");
                var name = $(":input[name='releaseName']",dialogContent);
                var view = this;
                var saveBtn = $(event.target);
                var loading = $(".githubloading.save");
                $(loading).toggleClass("hide");
                $(saveBtn).toggleClass("hide");
                app.githubApi.editRelease({
                    repoName: repoName,
                    name:$(name).val(),
                    releaseId: releaseId,
                    login: login
                }).pipe(function(json){
                    if(json.success){
                        window.location.reload();
                        view.$el.remove();
                    }else{
                        alert(json.errorMessage);
                        $(loading).toggleClass("hide");
                        $(saveBtn).toggleClass("hide");
                    }
                });
            }
        }
    });
})();