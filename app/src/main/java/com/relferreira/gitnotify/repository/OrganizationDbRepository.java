package com.relferreira.gitnotify.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.relferreira.gitnotify.model.ImmutableOrganization;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.repository.data.OrganizationColumns;
import com.relferreira.gitnotify.repository.interfaces.OrganizationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by relferreira on 1/27/17.
 */
public class OrganizationDbRepository implements OrganizationRepository {

    private static final String LOG_TAG = OrganizationDbRepository.class.getSimpleName();
    private Context context;

    public OrganizationDbRepository(Context context) {

        this.context = context;
    }

    @Override
    public void storeOrganizations(List<Organization> organizations) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(organizations.size());
        for(Organization organization : organizations) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    GithubProvider.Organizations.CONTENT_URI);

            builder.withValue(OrganizationColumns.ID, organization.id());
            builder.withValue(OrganizationColumns.LOGIN, organization.login());
            builder.withValue(OrganizationColumns.REPOS_URL, organization.reposUrl());
            builder.withValue(OrganizationColumns.AVATAR_URL, organization.avatarUrl());

            batchOperations.add(builder.build());
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.applyBatch(GithubProvider.AUTHORITY, batchOperations);

        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    @Override
    public List<Organization> listOrganizations() {
        List<Organization> organization = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(GithubProvider.Organizations.CONTENT_URI, null, null, null, null);
        if(cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    ImmutableOrganization.Builder organizationBuilder = ImmutableOrganization.builder();
                    organizationBuilder.id(cursor.getInt(cursor.getColumnIndex(OrganizationColumns.ID)));
                    organizationBuilder.login(cursor.getString(cursor.getColumnIndex(OrganizationColumns.LOGIN)));
                    organizationBuilder.avatarUrl(cursor.getString(cursor.getColumnIndex(OrganizationColumns.AVATAR_URL)));
                    organizationBuilder.reposUrl(cursor.getString(cursor.getColumnIndex(OrganizationColumns.REPOS_URL)));
                    organization.add(organizationBuilder.build());
                }
            } finally {
                cursor.close();
            }
        }
        return organization;
    }

    @Override
    public void removeOrganizations() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(GithubProvider.Organizations.CONTENT_URI, null, null);
    }
}
