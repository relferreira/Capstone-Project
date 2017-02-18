package com.relferreira.gitnotify.repository.interfaces;

import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Organization;

import java.util.List;

/**
 * Created by relferreira on 1/28/17.
 */
public interface EventRepository {

    void storeEvents(List<Event> events, List<Organization> organizations);

    void removeEvents();
}
