(function(){
    brite.registerView("GithubPullRequests",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubPullRequests",{pullrequests:data.pullrequests,reponame:data.name,login:data.login,pullRequestState:data.pullRequestState,openCount: data.openCount,closedCount:data.closedCount});
        },
        events:{
            "click;.openPullRequests":function(event){
                refreshPullRequests(event,"open");
            },
            "click;.closedPullRequests":function(event){
                refreshPullRequests(event,"closed");
            },
            "click;.btn-open":function(event){
                changePullRequestState(event,this,"open");
            },
            "click;.btn-close":function(event){
                changePullRequestState(event,this,"closed");
            },
            "click;.edit":function(event){
                var repoName = $(event.target).closest("table").attr("data-name");
                var login = $(event.target).closest("table").attr("data-login");
                var pullRequestId = $(event.target).closest("tr").attr("data-pullrequest-id");
                editPullRequest(event,pullRequestId,repoName,login);
            },
            "click;.messageTitle":function(event){
                var repoName = $(event.target).closest("table").attr("data-name");
                var login = $(event.target).closest("table").attr("data-login");
                var pullRequestId = $(event.target).closest("tr").attr("data-pullrequest-id");
                editPullRequest(event,pullRequestId,repoName,login,true);
            }
        }
    });

    function refreshPullRequests(event,state){
        var name = $(event.target).closest("div").attr("data-name");
        var login = $(event.target).closest("div").attr("data-login");
        app.githubApi.getPullRequests({
            name:name,
            login:login,
            state:state
        }).pipe(function(json){
            brite.display("GithubPullRequests",$(".tab-content"),{pullrequests:json.result.pullRequests,name:name,login:login,pullRequestState:state,openCount: json.result.openCount,closedCount:json.result.closedCount});
        });
    }

    function changePullRequestState(event,view,state){
        var repoName = $(event.target).closest("table").attr("data-name");
        var login = $(event.target).closest("table").attr("data-login");
        var pullRequestId = $(event.target).closest("tr").attr("data-pullrequest-id");
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
            if (json.success) {
                state = state == "open"?"closed":"open";
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
        });
    }

    function editPullRequest(event,pullRequestId,repoName,login,detail){
        app.githubApi.getPullRequest({
            name:repoName,
            login:login,
            pullRequestId:pullRequestId
        }).pipe(function(json){
            if(json.success) {
                var pullRequest = json.result;
                brite.display("GithubPullRequestEdit", $("body"), {
                    id: pullRequestId,
                    title: pullRequest.title,
                    body: pullRequest.body,
                    login: login,
                    repoName: repoName,
                    detail: detail,
                    layout: {
                        left: '20%',
                        top:"100px",
                        width:'60%',
                        height: 'auto'
                    }
                });
            }
        });
    }
})();