package com.sloyardms.trackerapi.tag_summary;

import com.sloyardms.trackerapi.security.AuthUtils;
import com.sloyardms.trackerapi.tag_summary.dto.TagSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@RequestMapping("/api/v1/tag-summaries")
public class TagSummaryController {

    private final TagSummaryService tagSummaryService;

    public TagSummaryController(TagSummaryService tagSummaryService) {
        this.tagSummaryService = tagSummaryService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(){
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/group/{groupUuid}")
    public ResponseEntity<Page<TagSummaryDto>> getAllByGroupUuid(
            @PathVariable UUID groupUuid,
            @PageableDefault(size = 10, sort = "tagName") Pageable pageable){
        UUID userUuid = AuthUtils.getCurrentUserId();
        Page<TagSummaryDto> result = tagSummaryService.findAllByUserAndGroup(userUuid, groupUuid, pageable);
        return ResponseEntity.ok(result);
    }

}
