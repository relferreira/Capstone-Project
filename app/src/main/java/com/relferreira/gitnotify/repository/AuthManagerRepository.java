package com.relferreira.gitnotify.repository;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.sync.EventsSyncAdapter;

import javax.inject.Inject;

/**
 * Created by relferreira on 1/23/17.
 */
public class AuthManagerRepository implements AuthRepository {

    private Context context;

    @Inject
    public AuthManagerRepository(Context context) {
        this.context = context;
    }

    @Override
    public void addAccount(String username, String token) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        Bundle userData = new Bundle();
        userData.putString(context.getString(R.string.sync_account_username), username);
        accountManager.addAccountExplicitly(newAccount, token, userData);
        // Enforce getting the new account
        EventsSyncAdapter.onAccountCreated(getAccount(), context);
    }

    @Override
    public Account getAccount() {
        // Permission check not used because of the intrusive message. Read more at this thread: https://code.google.com/p/android/issues/detail?id=189766#c8
        try {
            Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.sync_account_type));
            if (accounts.length > 0)
                return accounts[0];
        } catch (SecurityException e ){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getToken() {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = getAccount();
        if(account != null)
            return accountManager.getPassword(account);
        return null;
    }

    @Override
    public String getUsername(Account account) {
        return AccountManager.get(context).getUserData(account, context.getString(R.string.sync_account_username));
    }
}
