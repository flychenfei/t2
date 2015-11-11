(function(){
    brite.registerView("GithubReleaseEdit",{emptyParent:false},{
        create:function(data,config){
            return app.render("tmpl-GithubReleaseEdit",{
                id:data.id,
                name:data.name,
                tagName:data.tagName,
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
                var tagName = $(":input[name='releaseTagName']",dialogContent);
                var view = this;
                var saveBtn = $(event.target);
                var loading = $(".githubloading.save");
                $(loading).toggleClass("hide");
                $(saveBtn).toggleClass("hide");
                if(releaseId == ""){
                    app.githubApi.createRelease({
                        repoName: repoName,
                        name: $(name).val(),
                        tagName: $(tagName).val(),
                        login: login
                    }).pipe(function (json) {
                        refresh(view,json,repoName,login,loading,saveBtn);
                    });
                }else{
                    app.githubApi.editRelease({
                        repoName: repoName,
                        name: $(name).val(),
                        tagName: $(tagName).val(),
                        releaseId: releaseId,
                        login: login
                    }).pipe(function (json) {
                        refresh(view,json,repoName,login,loading,saveBtn);
                    });
                }
            }
        }
    });

    function refresh(view,json,repoName,login,loading,saveBtn){
        if (json.success) {
            app.githubApi.getReleases({
                name: repoName,
                login: login
            }).pipe(function (json) {
                view.$el.remove();
                brite.display("GithubReleases", $(".tab-content"), {
                    releases: json.result.releases,
                    name: repoName,
                    login: login
                });
            });
        } else {
            alert(json.errorMessage);
            $(loading).toggleClass("hide");
            $(saveBtn).toggleClass("hide");
        }
    }
})();