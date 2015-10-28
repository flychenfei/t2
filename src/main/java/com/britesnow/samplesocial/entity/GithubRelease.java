package com.britesnow.samplesocial.entity;


import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.User;

import java.io.Serializable;

public class GithubRelease implements Serializable {
    private static final long serialVersionUID = 2070566274663989459L;

    private String url;
    private String id;
    private String tag_name;
    private String target_commitish;
    private String name;
    private String body;
    private String tarballUrl;
    private String zipballUrl;
    private String created_at;
    private String published_at;

    public GithubRelease() {
    }

    public String getUrl() {
        return url;
    }

    public GithubRelease setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getId() {
        return id;
    }

    public GithubRelease setId(String id) {
        this.id = id;
        return this;
    }

    public String getTag_name() {
        return tag_name;
    }

    public GithubRelease setTag_name(String tag_name) {
        this.tag_name = tag_name;
        return this;
    }

    public String getTarget_commitish() {
        return target_commitish;
    }

    public GithubRelease setTarget_commitish(String target_commitish) {
        this.target_commitish = target_commitish;
        return this;
    }

    public String getBody() {
        return body;
    }

    public GithubRelease setBody(String body) {
        this.body = body;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public GithubRelease setAuthor(User author) {
        this.author = author;
        return this;
    }

    private org.eclipse.egit.github.core.User author;

    public String getCreated_at() {
        return created_at;
    }

    public GithubRelease setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public String getPublished_at() {
        return published_at;
    }

    public GithubRelease setPublished_at(String published_at) {
        this.published_at = published_at;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public GithubRelease setName(String name) {
        this.name = name;
        return this;
    }

    public String getTarballUrl() {
        return this.tarballUrl;
    }

    public GithubRelease setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
        return this;
    }

    public String getZipballUrl() {
        return this.zipballUrl;
    }

    public GithubRelease setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
        return this;
    }
}
