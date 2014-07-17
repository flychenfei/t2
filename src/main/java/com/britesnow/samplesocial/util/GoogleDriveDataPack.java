package com.britesnow.samplesocial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleDriveDataPack {

	private Map<String,String> generalData = new HashMap<String,String>();
	private List<Map> detailData = new ArrayList<Map>();
	
	public GoogleDriveDataPack(Map<String, String> generalData,
			List<Map> detailData) {
		super();
		this.generalData = generalData;
		this.detailData = detailData;
	}

	/**
	 * @return the generalData
	 */
	public Map<String, String> getGeneralData() {
		return generalData;
	}

	/**
	 * @return the detailData
	 */
	public List<Map> getDetailData() {
		return detailData;
	}
	
	
}
