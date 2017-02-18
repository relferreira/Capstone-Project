package com.relferreira.gitnotify.domain;

import android.accounts.Account;

import com.relferreira.gitnotify.repository.interfaces.AuthRepository;

/**
 * Created by relferreira on 2/5/17.
 */

public class AuthInteractor {

    private AuthRepository authRepository;

    public AuthInteractor(AuthRepository authRepository) {

        this.authRepository = authRepository;
    }

    public void addAccount(String username, String token) {
        authRepository.addAccount(username, token);
    }

    public void removeAccount() {
        authRepository.removeAccount();
    }

    public Account getAccount() {
        return authRepository.getAccount();
    }

    public String getToken() {
        return authRepository.getToken();
    }

    public String getUsername(Account account) {
        return authRepository.getUsername(account);
    }

}

