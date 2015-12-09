package com.britesnow.samplesocial.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_ISSUES;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_EVENTS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;

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

    public Pair<List<Issue>,Integer> searchContentByKeyWord(String repoId,String searchContent,int pageStart,int pageSize) throws IOException {
        pageStart++;
        if(pageSize < 1)pageSize=10;
        GitHubRequest request = new GitHubRequest();
        StringBuilder uriBuilder = new StringBuilder("/search/issues?");
        uriBuilder.append("per_page=").append(pageSize);
        uriBuilder.append("&page=").append(pageStart);
        uriBuilder.append("&q=repo:").append(repoId);
        if(searchContent != null){
            uriBuilder.append((" "+searchContent).replaceAll("\\s+","+"));
        }
        request.setUri(uriBuilder);
        request.setType(new TypeToken<Map>(){}.getType());
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getStream(request),CHARSET_UTF8),1024);
        Gson gson = GsonUtils.getGson();
        SearchIssuesContainer searchIssuesContainer = gson.fromJson(reader,SearchIssuesContainer.class);
        return new Pair<>(searchIssuesContainer.items,searchIssuesContainer.totalCount);
    }

    class SearchIssuesContainer{
        List<Issue> items;
        Integer totalCount;
    }

}
