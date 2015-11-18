package com.britesnow.samplesocial.service;

import com.google.gson.reflect.TypeToken;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.IssueService;

import java.util.List;
import java.util.Map;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ISSUES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_EVENTS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

public class GithubIssueService extends IssueService {

    public GithubIssueService(GitHubClient client) {
        super(client);
    }

    public PageIterator<Map<String,Object>> pageIssueEventsInMap(String user,
                                                    String repository, int issueId) {
        return pageIssueEventsInMap(user, repository, issueId, PAGE_SIZE);
    }

    /**
     * Page events for issue in repository
     *
     * @param user
     * @param repository
     * @param issueId
     * @param size
     * @return iterator over issue event pages
     */
    public PageIterator<Map<String,Object>> pageIssueEventsInMap(String user,
                                                    String repository, int issueId, int size) {
        return pageIssueEventsInMap(user, repository, issueId, PAGE_FIRST, size);
    }

    /**
     * Page issue events for repository
     *
     * @param user
     * @param repository
     * @param issueId
     * @param start
     * @param size
     * @return iterator over issue event pages
     */
    public PageIterator<Map<String,Object>> pageIssueEventsInMap(String user,
                                                    String repository, int issueId, int start, int size) {
        verifyRepository(user, repository);

        PagedRequest<Map<String,Object>> request = createPagedRequest(start, size);
        StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
        uri.append('/').append(user).append('/').append(repository);
        uri.append(SEGMENT_ISSUES);
        uri.append('/').append(issueId);
        uri.append(SEGMENT_EVENTS);
        request.setUri(uri);
        request.setType(new TypeToken<List<Map<String,Object>>>() {
        }.getType());
        return createPageIterator(request);
    }
}
