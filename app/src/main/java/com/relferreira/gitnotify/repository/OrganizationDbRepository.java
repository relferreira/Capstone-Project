package com.relferreira.gitnotify.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.data.GithubProvider;
import com.relferreira.gitnotify.repository.data.OrganizationColumns;

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
            builder.withValue(OrganizationColumns.NAME, organization.login());
            builder.withValue(OrganizationColumns.URL, organization.reposUrl());
            builder.withValue(OrganizationColumns.IMAGE, organization.avatarUrl());

            batchOperations.add(builder.build());
        }

        try {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.applyBatch(GithubProvider.AUTHORITY, batchOperations);

        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }
}
