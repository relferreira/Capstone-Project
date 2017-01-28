package com.relferreira.gitnotify.repository;

import com.relferreira.gitnotify.model.Organization;

import java.util.List;

/**
 * Created by relferreira on 1/27/17.
 */
public interface OrganizationRepository {

    void storeOrganizations(List<Organization> organizations);
}
