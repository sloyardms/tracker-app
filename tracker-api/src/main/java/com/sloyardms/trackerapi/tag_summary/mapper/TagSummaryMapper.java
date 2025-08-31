package com.sloyardms.trackerapi.tag_summary.mapper;

import com.sloyardms.trackerapi.tag_summary.dto.TagSummaryDto;
import com.sloyardms.trackerapi.tag_summary.projection.TagUsageProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagSummaryMapper {

    TagSummaryDto fromProjection(TagUsageProjection projection);


}
