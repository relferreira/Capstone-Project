package com.relferreira.gitnotify.repository.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by relferreira on 1/25/17.
 */
@ContentProvider(authority = GithubProvider.AUTHORITY, database = GithubDatabase.class)
public final class GithubProvider {

    public static final String AUTHORITY = "com.relferreira.gitnotify";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String EVENTS = "events";
        String ORGANIZATIONS = "organizations";
    }

    private static Uri buildUri(String...paths) {
        Uri.Builder uri = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            uri.appendPath(path);
        }
        return uri.build();
    }

    @TableEndpoint(table = GithubDatabase.ORGANIZATIONS) public static class Organizations {

        @ContentUri(
                path = Path.ORGANIZATIONS,
                type = "vnd.android.cursor.dir/orgs",
                defaultSort = OrganizationColumns.LOGIN + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.ORGANIZATIONS);

    }

    @TableEndpoint(table = GithubDatabase.EVENTS) public static class Events {

        @ContentUri(
                path = Path.EVENTS,
                type = "vnd.android.cursor.dir/events",
                defaultSort = EventColumns.CREATED_AT + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.EVENTS);

    }

}
