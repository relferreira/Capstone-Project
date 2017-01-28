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
        EventsSyncAdapter.onAccountCreated(newAccount, context);
    }

    @Override
    public String getToken() {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.sync_account_type));
        if(accounts.length > 0)
            return accountManager.getPassword(accounts[0]);
        return null;
    }

    @Override
    public String getUsername(Account account) {
        return AccountManager.get(context).getUserData(account, context.getString(R.string.sync_account_username));
    }
}
