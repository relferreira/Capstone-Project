package com.relferreira.gitnotify.repository.interfaces;

import com.relferreira.gitnotify.model.Organization;

import java.util.List;

/**
 * Created by relferreira on 1/27/17.
 */
public interface OrganizationRepository {

    void storeOrganizations(List<Organization> organizations);

    List<Organization> listOrganizations();
}
