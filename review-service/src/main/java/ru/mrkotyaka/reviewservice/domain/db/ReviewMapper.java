package ru.mrkotyaka.reviewservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRqDto;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    @Mapping(source = "id", target = "reviewId")
    ReviewRsDto toReviewRsDto(ReviewEntity entity);

    ReviewEntity toEntity(ReviewRqDto reviewRqDto);
}
