package com.relferreira.gitnotify.repository;

import com.relferreira.gitnotify.model.Event;

import java.util.List;

/**
 * Created by relferreira on 1/28/17.
 */
public interface EventRepository {

    void storeEvents(List<Event> events);
}
