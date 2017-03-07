package com.android.community;

import com.android.community.models.Community;
import com.android.community.models.Group;

/**
 * Created by adamc on 3/7/17.
 */

public class GroupService {
	private static GroupService instance;
	public static GroupService Instance() {
		if(instance == null) {
			instance = new GroupService();
		}

		return instance;
	}

	public Group mGroup = new Group();
	public Group getmGroup() { return this.mGroup; }
	public void setmGroup(Group mGroup) { this.mGroup = mGroup; }

	public String mAuthToken = null;
	private GroupService() {	FetchAuthenticationToken();	}

	public void FetchAuthenticationToken() {
		if(mAuthToken == null) {
			return;
		}
	}
}

