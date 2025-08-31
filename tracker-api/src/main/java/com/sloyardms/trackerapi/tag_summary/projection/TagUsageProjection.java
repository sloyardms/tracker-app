package com.sloyardms.trackerapi.tag_summary.projection;

import java.util.UUID;

public interface TagUsageProjection {

    UUID getTagUuid();

    String getTagName();

    int getBookmarkCount();

}
