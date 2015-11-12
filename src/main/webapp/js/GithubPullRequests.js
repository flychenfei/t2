(function(){
    brite.registerView("GithubPullRequests",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubPullRequests",{pullrequests:data.pullrequests,reponame:data.name,login:data.login});
        },
        events:{
        }
    });
})();