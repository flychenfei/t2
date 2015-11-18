package com.britesnow.samplesocial.service;


import com.britesnow.samplesocial.entity.GithubRelease;
import com.google.gson.reflect.TypeToken;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.GitHubService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubReleaseService extends GitHubService {
    public GithubReleaseService() {
    }

    public GithubReleaseService(GitHubClient client) {
        super(client);
    }

    public List<GithubRelease> getReleases(IRepositoryIdProvider repository) throws IOException {
        String id = this.getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(id);
        uri.append("/releases");
        PagedRequest<GithubRelease> request = createPagedRequest();
        request.setUri(uri);
        request.setType(new TypeToken<List<GithubRelease>>() {
        }.getType());
        return getAll(request);
    }

    public GithubRelease editRelease(IRepositoryIdProvider repository,GithubRelease release) throws IOException {
        if (repository == null)
            throw new IllegalArgumentException("Repository cannot be null");
        if (release == null)
            throw new IllegalArgumentException("Release cannot be null");

        final String repoId = getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/releases/").append(release.getId());
        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("name",release.getName());
        params.put("tag_name",release.getTag_name());
        return client.post(uri.toString(), params, GithubRelease.class);
    }

    public GithubRelease createRelease(IRepositoryIdProvider repository,GithubRelease release) throws IOException {
        if (repository == null)
            throw new IllegalArgumentException("Repository cannot be null");
        if (release == null)
            throw new IllegalArgumentException("Release cannot be null");

        final String repoId = getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/releases");
        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("name",release.getName());
        params.put("tag_name",release.getTag_name());
        return client.post(uri.toString(), params, GithubRelease.class);
    }

    public void deleteRelease(Repository repository, String releaseId) throws IOException {
        if (repository == null)
            throw new IllegalArgumentException("Repository cannot be null");
        final String repoId = getId(repository);
        StringBuilder uri = new StringBuilder("/repos");
        uri.append('/').append(repoId);
        uri.append("/releases/").append(releaseId);
        client.delete(uri.toString());
    }
}
