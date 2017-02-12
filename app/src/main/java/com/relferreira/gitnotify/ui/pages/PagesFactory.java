package com.relferreira.gitnotify.ui.pages;

import android.content.Context;

import java.util.List;

/**
 * Created by relferreira on 2/11/17.
 */

public class PagesFactory {

    public static PagesAdapter getAdapter(Context context, List items, String type) {
        switch (type){
//            case "PullRequestEvent":
//                return new PullRequestDecoder(context, event);
//            case "PushEvent":
//                return new PushEventDecoder(context, event);
            case "IssueCommentEvent":
                return new IssueCommentAdapter(context, items);
//            case "PullRequestReviewCommentEvent":
//                return new PullRequestReviewCommentEventDecoder(context, event);
//            case "CommitCommentEvent":
//                return new CommitCommentEventDecoder(context, event);
//            case "CreateEvent":
//                return new CreateDeleteEventDecoder(context, event, true);
//            case "DeleteEvent":
//                return new CreateDeleteEventDecoder(context, event, false);
//            case "WatchEvent":
//                return new StarredDecoder(context, event);
//            case "ForkEvent":
//                return new ForkEventDecoder(context, event);
//            case "GollumEvent":
//                return new WikiDecoder(context, event);
//            case "IssuesEvent":
//                return new IssuesEventDecoder(context, event);
//            case "MemberEvent":
//                return new MemberEventDecoder(context, event);
//            case "PublicEvent":
//                return new PublicEventDecoder(context, event);
//            case "PullRequestReviewEvent":
//                return new PullRequestReviewEventDecoder(context, event);
//            case "ReleaseEvent":
//                return new ReleaseEventDecoder(context, event);
        }
        return null;
    }
}
