package com.britesnow.samplesocial.entity;

public abstract class UserScopedEntity<I> extends BaseEntity<I>{

	private Long userId;

	// --------- Accessors --------- //
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	// --------- /Accessors --------- //
}
