package com.sloyardms.trackerapi.tag_summary;

import com.sloyardms.trackerapi.tag_summary.dto.TagSummaryDto;
import com.sloyardms.trackerapi.tag_summary.entity.TagSummary;
import com.sloyardms.trackerapi.tag_summary.entity.TagSummaryId;
import com.sloyardms.trackerapi.tag_summary.mapper.TagSummaryMapper;
import com.sloyardms.trackerapi.tag_summary.projection.TagUsageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TagSummaryService {

    private final TagSummaryRepository tagSummaryRepository;
    private final TagSummaryMapper tagSummaryMapper;

    public TagSummaryService(TagSummaryRepository tagSummaryRepository, TagSummaryMapper tagSummaryMapper) {
        this.tagSummaryRepository = tagSummaryRepository;
        this.tagSummaryMapper = tagSummaryMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void incrementTagCount(UUID userUuid, UUID groupUuid, UUID tagUuid){
        TagSummaryId id = new TagSummaryId(userUuid, groupUuid, tagUuid);

        //Find or create new object
        TagSummary tagSummary = tagSummaryRepository.findById(id)
                .orElse(new TagSummary(userUuid, groupUuid, tagUuid, 0));

        tagSummary.setBookmarkCount(tagSummary.getBookmarkCount() + 1);
        tagSummaryRepository.save(tagSummary);
    }

    @Transactional(rollbackFor = Exception.class)
    public void decrementTagCount(UUID userUuid, UUID groupUuid, UUID tagUuid){
        TagSummaryId id = new TagSummaryId(userUuid, groupUuid, tagUuid);

        tagSummaryRepository.findById(id).ifPresent(tagSummary -> {
            int newCount = Math.max(0, tagSummary.getBookmarkCount() - 1);
            tagSummary.setBookmarkCount(newCount);
            tagSummaryRepository.save(tagSummary);
        });
    }

    @Transactional(readOnly = true)
    public Page<TagSummaryDto> findAllByUserAndGroup(UUID userUuid, UUID groupUuid, Pageable pageable) {
        Page<TagUsageProjection> tagUsageProjections = tagSummaryRepository.findTagUsageByUserAndGroup(userUuid, groupUuid, pageable);
        return tagUsageProjections.map(tagSummaryMapper::fromProjection);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID userUuid, UUID groupUuid, UUID tagUuid){
        TagSummaryId id = new TagSummaryId(userUuid, groupUuid, tagUuid);
        tagSummaryRepository.deleteById(id);
    }

}
