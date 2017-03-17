package com.croutworst.community;


import com.croutworst.community.models.Account;
import com.croutworst.community.models.AccountRegistration;
import com.croutworst.community.models.GroupModel_;
import com.croutworst.community.models.Profile;

import java.util.ArrayList;

/**
 * Created by adamc on 2/15/17.
 */

public class AccountService {
	private static AccountService instance;
	public static AccountService Instance() {
		if(instance == null) {
			instance = new AccountService();
		}

		return instance;
	}

	public String mAuthToken = null;
	public Account mAccount = new Account();
	public Account getmAccount() { return this.mAccount; }
	public void setmAccount(Account account) { mAccount = account; }

	public Profile mProfile = new Profile();
	public Profile getmProfile() { return this.mProfile; }
	public void setmProfile(Profile profile) { mProfile = profile; }

	public AccountRegistration mAccountReg = new AccountRegistration();
	public AccountRegistration getmAccountReg() { return this.mAccountReg; }
	public void setmAccountReg(AccountRegistration accountReg) { mAccountReg = accountReg; }

	public ArrayList<GroupModel_> groupModels = new ArrayList<>();
	public ArrayList<GroupModel_> getGroupModels() { return groupModels; }
	public void setGroupModels(ArrayList<GroupModel_> groupModels) { this.groupModels = groupModels; }

	public Boolean ReadyToSignIn() {
		return !(mAuthToken.isEmpty());
	}

	private AccountService() {
		FetchAuthenticationToken();
	}

	public void FetchAuthenticationToken() {

		// NOTE: Store in local storage to check when app is opened
		// This is shared preferences in Android

		if(mAuthToken == null) {
			return;
		}
	}
}
