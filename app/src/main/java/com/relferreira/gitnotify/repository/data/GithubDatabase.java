package com.relferreira.gitnotify.repository.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by relferreira on 1/25/17.
 */
@Database(version = GithubDatabase.VERSION)
public final class GithubDatabase {

    public static final int VERSION = 1;

    @Table(EventColumns.class) public static final String EVENTS = "events";

    @Table(OrganizationColumns.class) public static final String ORGANIZATIONS = "organizations";

}
