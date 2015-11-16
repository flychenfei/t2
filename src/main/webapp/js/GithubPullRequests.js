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
            },
            "click;.btn-open":function(event){
                var repoName = $(event.target).closest("table").attr("data-name");
                var login = $(event.target).closest("table").attr("data-login");
                var pullRequestId = $(event.target).closest("tr").attr("data-pullrequest-id");
                var state = "open";
                var view = this;
                var currentBtn = $(event.target);
                var loading = $( $(event.target).closest("tr").find(".githubloading.save"));
                $(loading).toggleClass("hide");
                $(currentBtn).toggleClass("hide");
                app.githubApi.editPullRequest({
                    repoName:repoName,
                    pullRequestId:pullRequestId,
                    state:state,
                    login:login
                }).pipe(function(json){
                    refresh(view,json,repoName,login,loading,currentBtn,"closed");
                });
            },
            "click;.btn-close":function(event){
                var repoName = $(event.target).closest("table").attr("data-name");
                var login = $(event.target).closest("table").attr("data-login");
                var pullRequestId = $(event.target).closest("tr").attr("data-pullrequest-id");
                var state = "closed";
                var view = this;
                var currentBtn = $(event.target);
                var loading = $( $(event.target).closest("tr").find(".githubloading.save"));
                $(loading).toggleClass("hide");
                $(currentBtn).toggleClass("hide");
                app.githubApi.editPullRequest({
                    repoName:repoName,
                    pullRequestId:pullRequestId,
                    state:state,
                    login:login
                }).pipe(function(json){
                    refresh(view,json,repoName,login,loading,currentBtn,"open");
                });
            }
        }
    });

    function refresh(view,json,repoName,login,loading,currentBtn,state){
        if (json.success) {
            app.githubApi.getPullRequests({
                name:repoName,
                login:login,
                state:state
            }).pipe(function(json){
                view.$el.remove();
                brite.display("GithubPullRequests",
                    $(".tab-content"),
                    {
                        pullrequests:json.result.pullRequests,
                        name:repoName,
                        login:login,
                        pullRequestState:state,
                        openCount: json.result.openCount,
                        closedCount:json.result.closedCount
                    });
            });
        } else {
            alert(json.errorMessage);
            $(loading).toggleClass("hide");
            $(currentBtn).toggleClass("hide");
        }
    }
})();