package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.OrganizationRepository;

import java.util.List;

/**
 * Created by relferreira on 2/5/17.
 */

public class OrganizationInteractor {

    private OrganizationRepository organizationRepository;

    public OrganizationInteractor(OrganizationRepository organizationRepository) {

        this.organizationRepository = organizationRepository;
    }

    public void storeOrganizations(List<Organization> organizations){
        organizationRepository.storeOrganizations(organizations);
    }

    public List<Organization> listOrganizations() {
        return organizationRepository.listOrganizations();
    }
}
