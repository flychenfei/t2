package com.britesnow.samplesocial.entity;

import java.time.LocalDateTime;

import org.postgresql.util.PGInterval;

public class GmailAnalytics extends UserScopedEntity<Long> {

	private String messageSubject;
	private LocalDateTime senderTimeStamp;
	private LocalDateTime recipientTimeStamp;
	private String conversationName;
	private String messageType;
	private String senderEmailAddress;
	private String recipientEmailAddress;
	private String recipientType;
	private Long messageLength;
	private Integer countOfAttachments;
	private Long messageSize;
	private PGInterval elapsedTimeSincePreviousMessage;

	public GmailAnalytics(){}

	public GmailAnalytics(String username, String password){
		
	}

	// --------- Persistent Properties --------- //
	
	/**
	 * @return the messageSubject
	 */
	public String getMessageSubject() {
		return messageSubject;
	}

	/**
	 * @param messageSubject the messageSubject to set
	 */
	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}

	/**
	 * @return the senderTimeStamp
	 */
	public LocalDateTime getSenderTimeStamp() {
		return senderTimeStamp;
	}

	/**
	 * @param senderTimeStamp the senderTimeStamp to set
	 */
	public void setSenderTimeStamp(LocalDateTime senderTimeStamp) {
		this.senderTimeStamp = senderTimeStamp;
	}

	/**
	 * @return the recipientTimeStamp
	 */
	public LocalDateTime getRecipientTimeStamp() {
		return recipientTimeStamp;
	}

	/**
	 * @param recipientTimeStamp the recipientTimeStamp to set
	 */
	public void setRecipientTimeStamp(LocalDateTime recipientTimeStamp) {
		this.recipientTimeStamp = recipientTimeStamp;
	}

	/**
	 * @return the convetsationName
	 */
	public String getConvetsationName() {
		return conversationName;
	}

	/**
	 * @param convetsationName the convetsationName to set
	 */
	public void setConvetsationName(String convetsationName) {
		this.conversationName = convetsationName;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the senderEmailAddress
	 */
	public String getSenderEmailAddress() {
		return senderEmailAddress;
	}

	/**
	 * @param senderEmailAddress the senderEmailAddress to set
	 */
	public void setSenderEmailAddress(String senderEmailAddress) {
		this.senderEmailAddress = senderEmailAddress;
	}

	/**
	 * @return the recipientEmailAddress
	 */
	public String getRecipientEmailAddress() {
		return recipientEmailAddress;
	}

	/**
	 * @param recipientEmailAddress the recipientEmailAddress to set
	 */
	public void setRecipientEmailAddress(String recipientEmailAddress) {
		this.recipientEmailAddress = recipientEmailAddress;
	}

	/**
	 * @return the recipientType
	 */
	public String getRecipientType() {
		return recipientType;
	}

	/**
	 * @param recipientType the recipientType to set
	 */
	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	/**
	 * @return the messageLength
	 */
	public Long getMessageLength() {
		return messageLength;
	}

	/**
	 * @param messageLength the messageLength to set
	 */
	public void setMessageLength(Long messageLength) {
		this.messageLength = messageLength;
	}

	/**
	 * @return the countOfAttachments
	 */
	public Integer getCountOfAttachments() {
		return countOfAttachments;
	}

	/**
	 * @param countOfAttachments the countOfAttachments to set
	 */
	public void setCountOfAttachments(Integer countOfAttachments) {
		this.countOfAttachments = countOfAttachments;
	}

	/**
	 * @return the messageSize
	 */
	public Long getMessageSize() {
		return messageSize;
	}

	/**
	 * @param messageSize the messageSize to set
	 */
	public void setMessageSize(Long messageSize) {
		this.messageSize = messageSize;
	}

	/**
	 * @return the elapsedTimeSincePreviousMessage
	 */
	public PGInterval getElapsedTimeSincePreviousMessage() {
		return elapsedTimeSincePreviousMessage;
	}

	/**
	 * @param elapsedTimeSincePreviousMessage the elapsedTimeSincePreviousMessage to set
	 */
	public void setElapsedTimeSincePreviousMessage(
			PGInterval elapsedTimeSincePreviousMessage) {
		this.elapsedTimeSincePreviousMessage = elapsedTimeSincePreviousMessage;
	}

	// --------- /Persistent Properties --------- //

}
