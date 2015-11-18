(function(){
    brite.registerView("GithubPullRequestEdit",{emptyParent:false},{
        create:function(data,config){
            return app.render("tmpl-GithubPullRequestEdit",{
                id:data.id,
                title:data.title,
                body:data.body,
                login: data.login,
                repoName: data.repoName,
                layout:data.layout
            });
        },
        events: {
            "click;.dialogCloseBtn": function (event) {
                this.$el.remove();
            },
            "click;.btn.cancel": function (event) {
                this.$el.remove();
            }
        }
    });
})();