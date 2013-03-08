;(function(){
	brite.registerView("GithubRepositories",{emptyParent:true},{
		create:function(data,config){
			return app.render("tmpl-GithubRepositories",{repositories:data.repositories});
		},
		events:{
			"click;.createRepository":function(event){
				var btn = $(event.target);
				if($(btn).hasClass("disabled"))
					return false;
				var name=$(":input[name='newRepository']");
				var description = $(":input[name='description']");
				if(name.val().length==0){
					alert("The repository name is required");
					return false;
				}
				$(btn).toggleClass("disabled");
				$(".githubloading.create").toggleClass("hide");
				app.githubApi.createRepository({
					name:$(name).val(),
					description:$(description).val()
					}).pipe(function(json){
					if(json.success){
						alert("adding "+json.result.name+" successfully.");
					}else{
						alert(json.errorMessage);
					}
					$(btn).toggleClass("disabled");
					$(".githubloading.create").toggleClass("hide");
					$(name).val("");
					$(description).val("");
				});
			},
			"click;.icon-edit":function(event){
				var repositoryId = $(event.target).closest("td").attr("data-repository-id");
				var name = $(event.target).closest("td").attr("data-repository-name");
				var description = $(event.target).closest("td").attr("data-repository-description");
				var login = $(event.target).closest("td").attr("data-login");
				brite.display("GithubRepositoryEdit",$("body"),{id:repositoryId,name:name,description:description,login:login});
			},
			"click;.commits":function(event){
				var name = $(event.target).closest("td").attr("data-repository-name");
				var login = $(event.target).closest("td").attr("data-login");
				app.githubApi.getCommits({
					name:name,
					login:login
				}).pipe(function(json){
					alert(json.result.length);
					brite.display("GithubCommits",$(".tab-content"),{commits:json.result,name:name,login:login});
				});
			},
			"click;.events":function(event){
				app.githubApi.getPublicEvents().pipe(function(json){
					console.log(json);
				});
			},
			"click;.reponame":function(event){
				var repo = $(event.target).attr("data-repo");
				app.githubApi.getContents({repo:repo}).pipe(function(files){
					files = JSON.parse(files.result);
					app.githubApi.getReadme({repo:repo}).pipe(function(json){
						if(!json.result.content)
							json.result.content = "";
						brite.display("GithubDialog",$("body"),{
							layout:{width:'80%',height:'75%',left:'10%',top:'15%'},
							readme:json.result,
							files:files,
							type:"showRepoDetails",
							title:repo,
							repo:repo,
							archiveLink:json.archiveLink
					    });
					});
				});
			},
			"click;.createdownload":function(event){
				var name = $(event.target).closest("td").attr("data-repository-name");
				app.githubApi.getDownloads({repo:name}).pipe(function(json){
					var downloads = json.result;
					brite.display("GithubDialog",$("body"),{
						layout:{width:'50%',height:'auto',left:'25%',top:'15%'},
						type:"createDownload",
						title:name,
						repo:name,
						downloads:downloads
				    });
				});
			},
			"click;.forks":function(event){
				var name = $(event.target).closest("td").attr("data-repository-name");
				app.githubApi.getForks({repo:name}).pipe(function(repositories){
					console.log(repositories);
					repositories = repositories.result;
					brite.display("GithubRepositories",$(".tab-content"),{repositories:repositories});
				});
			}
		}
	});
	
	Handlebars.registerHelper('date', function(dateObj) {
		 var year = parseInt(dateObj.year)+1900;
		 var month = parseInt(dateObj.month)+1;
		 var date = dateObj.date;
		 return new Handlebars.SafeString(
			year+"-"+month+"-"+date
		 );
		});
})();