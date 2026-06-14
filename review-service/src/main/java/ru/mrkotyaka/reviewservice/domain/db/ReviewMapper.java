package ru.mrkotyaka.reviewservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRqDto;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    ReviewRsDto toReviewRsDto(ReviewEntity entity);

    ReviewEntity toEntity(ReviewRqDto reviewRqDto);
}
