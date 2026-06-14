package ru.mrkotyaka.reviewservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRqDto;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;
import ru.mrkotyaka.reviewservice.domain.db.ReviewEntity;
import ru.mrkotyaka.reviewservice.domain.db.ReviewMapper;
import ru.mrkotyaka.reviewservice.domain.db.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewProcessor {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final KafkaTemplate<UUID, ReviewRsDto> kafkaTemplate;

    @Value("${review-topic}")
    private String reviewTopic;

    public List<ReviewRsDto> getAllReviews() {
        List<ReviewRsDto> reviewRsDtos = new ArrayList<>();
        var entities = reviewRepository.findAll();

        for (var entity : entities) {
            reviewRsDtos.add(reviewMapper.toReviewRsDto(entity));
        }

        return reviewRsDtos;
    }

    public Optional<ReviewRsDto> getReviewById(UUID reviewId) {
        var entity = reviewRepository.findById(reviewId);
        return entity.map(reviewMapper::toReviewRsDto);
    }

    public Optional<ReviewRsDto> getReviewByOrderId(UUID orderId) {
        ReviewEntity entity = reviewRepository.findByOrderId(orderId);

        if  (entity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(reviewMapper.toReviewRsDto(entity));
    }

    public ReviewRsDto createReview(ReviewRqDto reviewDto) {
        var orderId = reviewDto.orderId();

        log.info("Finding review for order `{}`", reviewDto.orderId());
        if(getReviewByOrderId(orderId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order `%s` already have review".formatted(orderId));
        }

        var entity = reviewMapper.toEntity(reviewDto);
        var reviewRsDto = reviewMapper.toReviewRsDto(reviewRepository.save(entity));
        log.info("Review saved successfully");

        kafkaTemplate.send(reviewTopic, reviewRsDto);
        log.info("Review sent to Kafka successfully");

        return reviewRsDto;
    }
}
