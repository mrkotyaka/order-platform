package ru.mrkotyaka.reviewservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRqDto;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;
import ru.mrkotyaka.reviewservice.domain.ReviewProcessor;
import ru.mrkotyaka.reviewservice.external.DeliveryHttpClient;
import ru.mrkotyaka.reviewservice.external.OrderHttpClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewProcessor reviewProcessor;
    private final OrderHttpClient orderHttpClient;
    private final DeliveryHttpClient deliveryHttpClient;

    @GetMapping
    public List<ReviewRsDto> getAllReviews() {
        return reviewProcessor.getAllReviews();
    }

    @GetMapping("/{id}")
    public Optional<ReviewRsDto> getReviewById(@PathVariable("id") UUID reviewId) {
        return reviewProcessor.getReviewById(reviewId);
    }

    @GetMapping("/order/{id}")
    public Optional<ReviewRsDto> getReviewByOrderId(@PathVariable("id") UUID orderId) {
        return reviewProcessor.getReviewByOrderId(orderId);
    }

    @PostMapping
    public ReviewRsDto createReview(
            @RequestBody ReviewRqDto request,
            @RequestHeader("X-User-Id") UUID authUserId
    ) {
        if (!orderHttpClient.canForReview(authUserId, request.orderId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have the rights to create a review");
        }
        log.info("Creator order checked successfully");

        var userIdFromCourierId = deliveryHttpClient.getCourierId(request.orderId());
        log.info("Getting userId from delivery courierId");

        var reviewRqDto = ReviewRqDto.builder()
                .userId(userIdFromCourierId)
                .orderId(request.orderId())
                .orderRating(request.orderRating())
                .courierRating(request.courierRating())
                .productRating(request.productRating())
                .comment(request.comment())
                .build();
        return reviewProcessor.createReview(reviewRqDto);
    }
}
