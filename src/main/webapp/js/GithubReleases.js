(function(){
    brite.registerView("GithubReleases",{emptyParent:true},{
        create:function(data,config){
            return app.render("tmpl-GithubReleases",{releases:data.releases,name:data.name,login:data.login});
        },
        events:{


        }
    });
})();