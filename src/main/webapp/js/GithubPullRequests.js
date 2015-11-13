(function(){
    brite.registerView("GithubPullRequests",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubPullRequests",{pullrequests:data.pullrequests,reponame:data.name,login:data.login,pullRequestState:data.pullRequestState,openCount: data.openCount,closedCount:data.closedCount});
        },
        events:{
            "click;.openPullRequests":function(event){
                var name = $(event.target).closest("div").attr("data-name");
                var login = $(event.target).closest("div").attr("data-login");
                app.githubApi.getPullRequests({
                    name:name,
                    login:login,
                    state:"open"
                }).pipe(function(json){
                    brite.display("GithubPullRequests",$(".tab-content"),{pullrequests:json.result.pullRequests,name:name,login:login,pullRequestState:"open",openCount: json.result.openCount,closedCount:json.result.closedCount});
                });
            },
            "click;.closedPullRequests":function(event){
                var name = $(event.target).closest("div").attr("data-name");
                var login = $(event.target).closest("div").attr("data-login");
                app.githubApi.getPullRequests({
                    name:name,
                    login:login,
                    state:"closed"
                }).pipe(function(json){
                    brite.display("GithubPullRequests",$(".tab-content"),{pullrequests:json.result.pullRequests,name:name,login:login,pullRequestState:"closed",openCount: json.result.openCount,closedCount:json.result.closedCount});
                });
            }
        }
    });
})();