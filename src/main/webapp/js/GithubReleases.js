(function(){
    brite.registerView("GithubReleases",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubReleases",{releases:data.releases,reponame:data.name,login:data.login});
        },
        events:{
            "click;.edit":function(event){
                var releaseId = $(event.target).closest("tr").find(".message").attr("data-release-id");
                var name = $(event.target).closest("tr").find(".name").text();
                var tagName =$(event.target).closest("tr").find(".tag-name").text();
                var login = $(event.target).closest("table").parent().attr("data-login");
                var repoName = $(event.target).closest("table").parent().attr("data-name");
                brite.display("GithubReleaseEdit",$("body"),{
                    id:releaseId,
                    name:name,
                    tagName:tagName,
                    login: login,
                    repoName: repoName,
                    layout:{
                        left:'20%',
                        height:'auto'
                    }
                });
            },

        }
    });
})();