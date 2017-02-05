package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;

import java.util.List;

/**
 * Created by relferreira on 2/5/17.
 */

public class EventInteractor {

    private EventRepository eventRepository;

    public EventInteractor(EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    public void storeEvents(List<Event> events, List<Organization> organizations) {
        eventRepository.storeEvents(events, organizations);
    }

}
