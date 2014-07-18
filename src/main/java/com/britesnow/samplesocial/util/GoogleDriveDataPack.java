package com.britesnow.samplesocial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleDriveDataPack {

	private Map<String,Object> generalData = new HashMap<String,Object>();
	private List<Map> detailData = new ArrayList<Map>();
	
	public GoogleDriveDataPack(Map<String, Object> generalData,
			List<Map> detailData) {
		super();
		this.generalData = generalData;
		this.detailData = detailData;
	}

	/**
	 * @return the generalData
	 */
	public Map<String, Object> getGeneralData() {
		return generalData;
	}

	/**
	 * @return the detailData
	 */
	public List<Map> getDetailData() {
		return detailData;
	}
	
	
}
