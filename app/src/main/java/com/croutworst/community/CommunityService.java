package com.croutworst.community;

import com.croutworst.community.models.Community;

/**
 * Created by adamc on 3/6/17.
 */

public class CommunityService {
	private static CommunityService instance;
	public static CommunityService Instance() {
		if(instance == null) {
			instance = new CommunityService();
		}

		return instance;
	}

	public Community mCommunity = new Community();
	public Community getmCommunity() { return this.mCommunity; }
	public void setmCommunity(Community mCommunity) { this.mCommunity = mCommunity; }

	public String mAuthToken = null;
	private CommunityService() {	FetchAuthenticationToken();	}

	public void FetchAuthenticationToken() {
		if(mAuthToken == null) {
			return;
		}
	}
}
