package com.bookstore.v1.services;

import com.bookstore.v1.data.*;
import com.bookstore.v1.dto.ReviewCreationDTO;
import com.bookstore.v1.dto.ReviewDTO;
import com.bookstore.v1.exception.DuplicateObjectException;
import com.bookstore.v1.exception.EmptyFieldException;
import com.bookstore.v1.exception.EntityNotFoundException;
import com.bookstore.v1.exception.InvalidDoubleRange;
import com.bookstore.v1.validations.ReviewValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    Logger logger = LoggerFactory.getLogger(ReviewService.class);

    public ReviewDTO addReview(ReviewCreationDTO reviewCreationDTO) throws EmptyFieldException, InvalidDoubleRange,
            EntityNotFoundException, DuplicateObjectException {
        // validate review creation dto without the id since it will be auto-generated
        ReviewValidations.validateReviewCreationDTO(reviewCreationDTO, false);

        // convert to review entity and get relations
        Review review = reviewCreationDTO.toReview(true);

        Optional<User> user = userRepository.findById(reviewCreationDTO.getUserId());
        if (user.isEmpty()) {
            logger.warn("User not found with id: " + reviewCreationDTO.getUserId());
            throw new EntityNotFoundException("user");
        }
        Optional<Book> book = bookRepository.findById(reviewCreationDTO.getBookId());
        if (book.isEmpty()) {
            logger.warn("Book not found with id: " + reviewCreationDTO.getBookId() + " requested by user: " +
                        reviewCreationDTO.getUserId());
            throw new EntityNotFoundException("book");
        }
        Optional<Review> existingReview = reviewRepository.findByUserAndBook(user.get(), book.get());
        if (existingReview.isPresent()) {
            logger.warn("Review already exists for book: " + book.get().getId() + " by user: " + user.get().getId());
            throw new DuplicateObjectException("review");
        }

        review.setUser(user.get());
        review.setBook(book.get());
        review = reviewRepository.save(review);

        logger.info("Review created with id: " + review.getId() + " for book: " + book.get().getId() + " by user: " +
                    user.get().getId());

        return new ReviewDTO(review, true, true);
    }

    public ReviewDTO updateReview(ReviewCreationDTO reviewUpdateDTO) throws EmptyFieldException, InvalidDoubleRange,
            EntityNotFoundException {
        // validate review update dto with the id since the object should already exist
        ReviewValidations.validateReviewCreationDTO(reviewUpdateDTO, true);

        Optional<Review> oldReviewOpt = reviewRepository.findById(reviewUpdateDTO.getId());
        if (oldReviewOpt.isEmpty()) {
            logger.warn("Review not found with id: " + reviewUpdateDTO.getId() + " requested by user: " +
                        reviewUpdateDTO.getUserId());
            throw new EntityNotFoundException("review");
        }

        Review newReview = oldReviewOpt.get();
        newReview.setTitle(reviewUpdateDTO.getTitle());
        newReview.setDescription(reviewUpdateDTO.getDescription());
        newReview.setRating(reviewUpdateDTO.getRating());
        reviewRepository.save(newReview);

        logger.info("Review updated with id: " + newReview.getId() + " for book: " + newReview.getBook().getId() +
                    " by user: " + newReview.getUser().getId());

        return new ReviewDTO(newReview, true, true);
    }

    public void deleteReviewById(String reviewId) throws EntityNotFoundException {
        Optional<Review> reviewToDelete = reviewRepository.findById(reviewId);
        if (reviewToDelete.isEmpty()) {
            logger.warn("Review not found with id: " + reviewId);
            throw new EntityNotFoundException("review");
        }
        reviewRepository.delete(reviewToDelete.get());

        logger.info("Review deleted with id: " + reviewId);
    }

    public ReviewDTO getReviewById(String reviewId) throws EntityNotFoundException {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isEmpty()) {
            logger.warn("Review not found with id: " + reviewId);
            throw new EntityNotFoundException("review");
        }

        logger.info("Review retrieved with id: " + reviewId);

        return new ReviewDTO(review.get(), true, true);
    }

    public List<ReviewDTO> getReviews() {
        return reviewRepository
                .findAll()
                .stream()
                .map(review -> new ReviewDTO(review, true, true))
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getBookReviews(String bookId) throws EntityNotFoundException {
        Optional<Book> bookFilter = bookRepository.findById(bookId);
        if (bookFilter.isEmpty()) {
            logger.warn("Book not found with id: " + bookId);
            throw new EntityNotFoundException("book");
        }
        return reviewRepository
                .findAllByBook(bookFilter.get())
                .stream()
                .map(review -> new ReviewDTO(review, true, false))
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getUserReviews(String userId) throws EntityNotFoundException {
        Optional<User> userFilter = userRepository.findById(userId);
        if (userFilter.isEmpty()) {
            logger.warn("User not found with id: " + userId);
            throw new EntityNotFoundException("user");
        }
        return reviewRepository
                .findAllByUser(userFilter.get())
                .stream()
                .map(review -> new ReviewDTO(review, false, true))
                .collect(Collectors.toList());
    }
}
