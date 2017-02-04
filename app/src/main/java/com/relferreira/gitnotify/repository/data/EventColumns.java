package com.relferreira.gitnotify.repository.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by relferreira on 1/25/17.
 */
public interface EventColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @NotNull @Unique(onConflict = ConflictResolutionType.REPLACE)
    String ID = "id";

    @DataType(DataType.Type.TEXT) @NotNull
    String TYPE = "type";

    @DataType(DataType.Type.TEXT) @NotNull
    String ACTOR_ID = "actor_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String ACTOR_NAME = "actor_name";

    @DataType(DataType.Type.TEXT)
    String ACTOR_IMAGE = "actor_image";

    @DataType(DataType.Type.INTEGER) @NotNull
    String REPO_ID = "repo_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String REPO_NAME = "repo_name";

    @DataType(DataType.Type.REAL) @NotNull
    String CREATED_AT = "created_at";

    @DataType(DataType.Type.INTEGER)
    String ORG_ID = "org_id";

    @DataType(DataType.Type.TEXT)
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    String SUB_TITLE = "sub_title";

    @DataType(DataType.Type.TEXT)
    String PAYLOAD = "payload";

    @DataType(DataType.Type.INTEGER)
    String USER_ORG = "user_org";
}
