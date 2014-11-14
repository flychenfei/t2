package com.britesnow.samplesocial.entity;



public abstract class  BaseEntity<I>{
	private I id;

	public I getId() {
		return id;
	}
	public void setId(I id) {
		this.id = id;
	}

}
