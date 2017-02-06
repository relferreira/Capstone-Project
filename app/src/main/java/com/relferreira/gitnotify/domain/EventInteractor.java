package com.relferreira.gitnotify.domain;

import com.relferreira.gitnotify.domain.decoder.CommitCommentEventDecoder;
import com.relferreira.gitnotify.domain.decoder.CreateDeleteEventDecoder;
import com.relferreira.gitnotify.domain.decoder.DescriptionDecoder;
import com.relferreira.gitnotify.domain.decoder.ForkEventDecoder;
import com.relferreira.gitnotify.domain.decoder.IssueCommentEventDecoder;
import com.relferreira.gitnotify.domain.decoder.IssuesEventDecoder;
import com.relferreira.gitnotify.domain.decoder.MemberEventDecoder;
import com.relferreira.gitnotify.domain.decoder.PublicEventDecoder;
import com.relferreira.gitnotify.domain.decoder.PullRequestDecoder;
import com.relferreira.gitnotify.domain.decoder.PullRequestReviewCommentEventDecoder;
import com.relferreira.gitnotify.domain.decoder.PullRequestReviewEventDecoder;
import com.relferreira.gitnotify.domain.decoder.PushEventDecoder;
import com.relferreira.gitnotify.domain.decoder.ReleaseEventDecoder;
import com.relferreira.gitnotify.domain.decoder.StarredDecoder;
import com.relferreira.gitnotify.domain.decoder.WikiDecoder;
import com.relferreira.gitnotify.model.Event;
import com.relferreira.gitnotify.model.ImmutableEvent;
import com.relferreira.gitnotify.model.Organization;
import com.relferreira.gitnotify.repository.interfaces.EventRepository;
import com.relferreira.gitnotify.repository.interfaces.StringRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by relferreira on 2/5/17.
 */

public class EventInteractor {

    private final StringRepository context;
    private EventRepository eventRepository;

    public EventInteractor(StringRepository context, EventRepository eventRepository) {
        this.context = context;
        this.eventRepository = eventRepository;
    }

    public void storeEvents(List<Event> events, List<Organization> organizations) {
        List<Event> eventsToStore = new ArrayList<>();
        for(Event event : events){
            String title = null, subtitle = null;
            Boolean isUserOrg = false;
            String type = event.type();

            Organization org = event.org();
            if(org != null)
                 isUserOrg = checkIfIsUserOrganization(org, organizations);

            DescriptionDecoder encoder = getDecoder(context, event, type);
            if(encoder != null) {
                title = encoder.getTitle();
                subtitle = encoder.getSubtitle();
            }

            Event newEvent = ImmutableEvent.builder()
                    .from(event)
                    .title(title)
                    .subtitle(subtitle)
                    .isUserOrg(isUserOrg)
                    .build();
            eventsToStore.add(newEvent);
        }
        eventRepository.storeEvents(eventsToStore, organizations);
    }

    public DescriptionDecoder getDecoder(StringRepository context, Event event, String type){
        switch (type){
            case "PullRequestEvent":
                return new PullRequestDecoder(context, event);
            case "PushEvent":
                return new PushEventDecoder(context, event);
            case "IssueCommentEvent":
                return new IssueCommentEventDecoder(context, event);
            case "PullRequestReviewCommentEvent":
                return new PullRequestReviewCommentEventDecoder(context, event);
            case "CommitCommentEvent":
                return new CommitCommentEventDecoder(context, event);
            case "CreateEvent":
                return new CreateDeleteEventDecoder(context, event, true);
            case "DeleteEvent":
                return new CreateDeleteEventDecoder(context, event, false);
            case "WatchEvent":
                return new StarredDecoder(context, event);
            case "ForkEvent":
                return new ForkEventDecoder(context, event);
            case "GollumEvent":
                return new WikiDecoder(context, event);
            case "IssuesEvent":
                return new IssuesEventDecoder(context, event);
            case "MemberEvent":
                return new MemberEventDecoder(context, event);
            case "PublicEvent":
                return new PublicEventDecoder(context, event);
            case "PullRequestReviewEvent":
                return new PullRequestReviewEventDecoder(context, event);
            case "ReleaseEvent":
                return new ReleaseEventDecoder(context, event);
        }
        return null;
    }

    private boolean checkIfIsUserOrganization(Organization org, List<Organization> organizations) {
        boolean isUserOrganization = false;
        for(Organization organization : organizations){
            if(organization.id().equals(org.id())) {
                isUserOrganization = true;
                break;
            }
        }
        return isUserOrganization;
    }
}
