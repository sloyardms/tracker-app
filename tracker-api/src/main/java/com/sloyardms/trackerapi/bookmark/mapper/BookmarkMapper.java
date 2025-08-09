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

    @Mapping(target = "group", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "userUuid", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(BookmarkUpdateDto bookmarkUpdateDto, @MappingTarget Bookmark bookmark);

}
