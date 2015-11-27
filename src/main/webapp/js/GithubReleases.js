(function(){
    brite.registerView("GithubReleases",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubReleases",{releases:data.releases,reponame:data.name,login:data.login});
        },
        events:{
            "click;.message":function(event){
                var releaseId = $(event.target).closest("tr").find(".message").attr("data-release-id");
                var name = $(event.target).closest("tr").find(".message").attr("data-release-name");
                var tagName =$(event.target).closest("tr").find(".message").attr("data-release-tagname");
                var detail = true;
                editRelease(event,releaseId,name,tagName,detail);
            },
            "click;.edit":function(event){
                var releaseId = $(event.target).closest("tr").find(".message").attr("data-release-id");
                var name = $(event.target).closest("tr").find(".message").attr("data-release-name");
                var tagName =$(event.target).closest("tr").find(".message").attr("data-release-tagname");
                var detail;
                editRelease(event,releaseId,name,tagName,detail);
            },
            "click;.new-release":function(event){
                var releaseId;
                var name = "";
                var tagName = "";
                var detail;
                editRelease(event,releaseId,name,tagName,detail);
            },
            "click;.remove":function(event){
                var releaseId = $(event.target).closest("tr").find(".message").attr("data-release-id");
                var login = $(event.target).closest("table").parent().attr("data-login");
                var repoName = $(event.target).closest("table").parent().attr("data-name");
                var deleteBtn = $(event.target);
                var loading = $($(event.target).parent().find(".githubloading.save"));
                $(loading).toggleClass("hide");
                $(deleteBtn).toggleClass("hide");
                app.githubApi.deleteRelease({
                    repoName: repoName,
                    login: login,
                    releaseId: releaseId
                }).pipe(function (json) {
                    if (json.success) {
                        app.githubApi.getReleases({
                            name: repoName,
                            login: login
                        }).pipe(function (json) {
                            brite.display("GithubReleases", $(".tab-content"), {
                                releases: json.result.releases,
                                name: repoName,
                                login: login
                            });
                        });
                    } else {
                        alert(json.errorMessage);
                        $(loading).toggleClass("hide");
                        $(deleteBtn).toggleClass("hide");
                    }
                });
            }
        }
    });

    function editRelease(event,releaseId,name,tagName,detail){
        var login = $(event.target).closest("table").parent().attr("data-login");
        var repoName = $(event.target).closest("table").parent().attr("data-name");
        brite.display("GithubReleaseEdit",$("body"),{
            id:releaseId,
            name:name,
            tagName:tagName,
            login: login,
            repoName: repoName,
            layout:{
                left: '20%',
                top:"100px",
                width:'60%',
                height: 'auto'
            },
            detail: detail
        });
    }
})();