package com.bookstore.v1.controllers;

import com.bookstore.v1.dto.ReviewCreationDTO;
import com.bookstore.v1.dto.ReviewDTO;
import com.bookstore.v1.exception.DuplicateObjectException;
import com.bookstore.v1.exception.EmptyFieldException;
import com.bookstore.v1.exception.EntityNotFoundException;
import com.bookstore.v1.exception.InvalidDoubleRange;
import com.bookstore.v1.services.ReviewService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MeterRegistry metricsRegistry;
    private final AtomicLong badRequestCounter = new AtomicLong();

    @PostMapping("/add-review")
    @ResponseBody
    @Timed(value = "bookstore.review.add.review.time", description = "Time taken to add a review")
    @Counted(value = "bookstore.review.add.review.count", description = "Number of times a review is added")
    public ReviewDTO addReview(@RequestBody ReviewCreationDTO reviewCreationDTO) throws EmptyFieldException,
            InvalidDoubleRange, EntityNotFoundException, DuplicateObjectException {
        return reviewService.addReview(reviewCreationDTO);
    }

    @PutMapping("/update-review")
    @ResponseBody
    @Timed(value = "bookstore.review.update.review.time", description = "Time taken to update a review")
    @Counted(value = "bookstore.review.update.review.count", description = "Number of times a review is updated")
    public ReviewDTO updateReview(@RequestBody ReviewCreationDTO reviewUpdateDTO) throws EmptyFieldException,
            InvalidDoubleRange, EntityNotFoundException {
        return reviewService.updateReview(reviewUpdateDTO);
    }

    @DeleteMapping("/delete-review/{reviewId}")
    @ResponseBody
    @Timed(value = "bookstore.review.delete.review.time", description = "Time taken to delete a review")
    @Counted(value = "bookstore.review.delete.review.count", description = "Number of times a review is deleted")
    public void deleteReview(@PathVariable String reviewId) throws EntityNotFoundException {
        reviewService.deleteReviewById(reviewId);
    }

    @GetMapping("/get-review/{reviewId}")
    @ResponseBody
    @Timed(value = "bookstore.review.get.review.time", description = "Time taken to get a review")
    @Counted(value = "bookstore.review.get.review.count", description = "Number of times a review is retrieved")
    public ReviewDTO getReviewById(@PathVariable String reviewId) throws EntityNotFoundException {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping("/get-reviews")
    @ResponseBody
    @Timed(value = "bookstore.review.get.reviews.time", description = "Time taken to get all reviews")
    @Counted(value = "bookstore.review.get.reviews.count", description = "Number of times all reviews are retrieved")
    public List<ReviewDTO> getReviews() {
        return reviewService.getReviews();
    }

    @GetMapping("/get-book-reviews/{bookId}")
    @ResponseBody
    @Timed(value = "bookstore.review.get.book.reviews.time", description = "Time taken to get all reviews for a book")
    @Counted(value = "bookstore.review.get.book.reviews.count", description = "Number of times all reviews for a book are retrieved")
    public List<ReviewDTO> getBookReviews(@PathVariable String bookId) throws EntityNotFoundException {
        return reviewService.getBookReviews(bookId);
    }

    @GetMapping("/get-user-reviews/{userId}")
    @ResponseBody
    @Timed(value = "bookstore.review.get.user.reviews.time", description = "Time taken to get all reviews for a user")
    @Counted(value = "bookstore.review.get.user.reviews.count", description = "Number of times all reviews for a user are retrieved")
    public List<ReviewDTO> getUserReviews(@PathVariable String userId) throws EntityNotFoundException {
        return reviewService.getUserReviews(userId);
    }

    @GetMapping("/get-review-broken/{reviewId}")
    @ResponseBody
    @Timed(value = "bookstore.review.get.review.broken.time", description = "Time taken to get a review broken method")
    @Counted(value = "bookstore.review.get.review.broken.count", description = "Number of times a review is retrieved broken method")
    public ReviewDTO getReviewBroken(@PathVariable String reviewId) throws EntityNotFoundException,
            InterruptedException {
        // coin flip to simulate broken return of review for metric testing
        var isBroken = new Random().nextInt(2);
        if (isBroken == 0) {
            metricsRegistry
                    .counter("error_on_return_review_broken_count", "endpoint", "get_review_broken")
                    .increment(badRequestCounter.incrementAndGet());
            throw new EntityNotFoundException("Review not found");
        }
        // simulate long response time for slow metric testing
        Thread.sleep(100 * new Random().nextInt(20));
        return reviewService.getReviewById(reviewId);
    }
}