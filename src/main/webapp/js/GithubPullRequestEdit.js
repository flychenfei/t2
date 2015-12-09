(function(){
    brite.registerView("GithubPullRequestEdit",{emptyParent:false},{
        create:function(data,config){
            return app.render("tmpl-GithubPullRequestEdit",{
                id:data.id,
                title:data.title,
                body:data.body,
                login: data.login,
                state:data.state,
                repoName: data.repoName,
                detail: data.detail,
                layout:data.layout
            });
        },
        events: {
            "click;.dialogCloseBtn": function (event) {
                this.$el.remove();
            },
            "click;.btn.cancel": function (event) {
                this.$el.remove();
            },
            "click;.btn.save":function(event){
                var dialogContent = $(event.target).closest(".dialogContent");
                var dialogBody = $(event.target).closest(".dialogBody");
                var repoName =  dialogBody.attr("data-repository-name");
                var login = dialogBody.attr("data-login");
                var state = dialogBody.attr("data-state");
                var pullRequestId = dialogBody.attr("data-pullrequest-id");
                var title = $(":input[name='pullRequestTitle']",dialogContent).val();
                var body = dialogBody.find(".pullRequest-body").val();
                var view = this;
                var saveBtn = $(event.target);
                var loading = $(event.target).parent().find(".githubloading.save");
                $(loading).toggleClass("hide");
                $(saveBtn).toggleClass("hide");

                app.githubApi.editPullRequest({
                    repoName:repoName,
                    pullRequestId:pullRequestId,
                    login:login,
                    title:title,
                    body:body
                }).pipe(function (json) {
                    refresh(view,json,repoName,login,state,loading,saveBtn);
                });
            }
        }
    });

    function refresh(view,json,repoName,login,state,loading,saveBtn){
        if (json.success) {
            app.githubApi.getPullRequests({
                name:repoName,
                login:login,
                state:state
            }).pipe(function(json){
                view.$el.remove();
                brite.display("GithubPullRequests",$(".tab-content"),{
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
            $(saveBtn).toggleClass("hide");
        }
    }
})();