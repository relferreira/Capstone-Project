package com.relferreira.gitnotify.ui.pages;

import android.content.Context;

import java.util.List;

/**
 * Created by relferreira on 2/11/17.
 */

public class PagesFactory {

    @SuppressWarnings("unchecked")
    public static PagesAdapter getAdapter(Context context, List items, String type) {
        switch (type){
            case "PullRequestEvent":
                return new PullRequestAdapter(context, items);
            case "PushEvent":
                return new PushAdapter(context, items);
            case "IssueCommentEvent":
                return new IssueCommentAdapter(context, items);
//            case "PullRequestReviewCommentEvent":
//                return new PullRequestReviewCommentEventDecoder(context, event);
//            case "CommitCommentEvent":
//                return new CommitCommentEventDecoder(context, event);
            case "CreateEvent":
                return new TextAdapter(context, items);
            case "DeleteEvent":
                return new TextAdapter(context, items);
//            case "WatchEvent":
//                return new StarredDecoder(context, event);
//            case "ForkEvent":
//                return new ForkEventDecoder(context, event);
            case "GollumEvent":
                return new WikiAdapter(context, items);
            case "IssuesEvent":
                return new IssuesEventAdapter(context, items);
            case "MemberEvent":
                return new TextAdapter(context, items);
//            case "PublicEvent":
//                return new PublicEventDecoder(context, event);
//            case "PullRequestReviewEvent":
//                return new PullRequestReviewEventDecoder(context, event);
            case "ReleaseEvent":
                return new ReleaseAdapter(context, items);
        }
        return null;
    }
}
