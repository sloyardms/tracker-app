package com.sloyardms.trackerapi.bookmark.mapper;

import com.sloyardms.trackerapi.bookmark.dto.BookmarkCreateDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkDto;
import com.sloyardms.trackerapi.bookmark.dto.BookmarkUpdateDto;
import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.group.mapper.GroupMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {GroupMapper.class})
public interface BookmarkMapper {

    BookmarkDto toDto(Bookmark bookmark);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateFromDto(BookmarkUpdateDto bookmarkUpdateDto, @MappingTarget Bookmark bookmark);

}
