package com.relferreira.gitnotify.repository.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by relferreira on 1/27/17.
 */
public interface OrganizationColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String NAME = "name";

    @DataType(DataType.Type.INTEGER) @NotNull @Unique(onConflict = ConflictResolutionType.REPLACE)
    String ID = "id";

    @DataType(DataType.Type.TEXT) @NotNull
    String URL = "url";

    @DataType(DataType.Type.TEXT) @NotNull
    String IMAGE = "image";
}
