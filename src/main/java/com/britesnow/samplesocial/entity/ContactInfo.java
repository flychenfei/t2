package com.britesnow.samplesocial.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "contactinfo")
@javax.persistence.SequenceGenerator(name = "SEQ_STORE", allocationSize = 1, sequenceName = "contactinfo_id_seq")
public class ContactInfo extends BaseEntity {
    private Long   contactId;
    private String birthday;
    private String education;
    private String email;
    private String favoriteAthletes;
    private String favoriteTeams;
    private String firstName;
    private String gender;
    private String hometown;
    private String hometownName;
    private String interestedIn;
    private String languages;
    private String lastName;
    private String link;
    private String locale;
    private String location;
    private String meetingFor;
    private String middleName;
    private String quotes;
    private String relationshipStatus;
    private String religion;
    private String significantOther;
    private String sports;
    private String thirdPartyId;
    private String timezone;
    private String type;
    private String updatedTime;
    private String username;
    private String verified;
    private String website;
    private String work;

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavoriteAthletes() {
        return favoriteAthletes;
    }

    public void setFavoriteAthletes(String favoriteAthletes) {
        this.favoriteAthletes = favoriteAthletes;
    }

    public String getFavoriteTeams() {
        return favoriteTeams;
    }

    public void setFavoriteTeams(String favoriteTeams) {
        this.favoriteTeams = favoriteTeams;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getHometownName() {
        return hometownName;
    }

    public void setHometownName(String hometownName) {
        this.hometownName = hometownName;
    }

    public String getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetingFor() {
        return meetingFor;
    }

    public void setMeetingFor(String meetingFor) {
        this.meetingFor = meetingFor;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getSignificantOther() {
        return significantOther;
    }

    public void setSignificantOther(String significantOther) {
        this.significantOther = significantOther;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}
