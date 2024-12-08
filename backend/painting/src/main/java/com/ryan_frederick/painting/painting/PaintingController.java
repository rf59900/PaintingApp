package com.ryan_frederick.painting.painting;

import com.ryan_frederick.painting.rating.Rating;
import com.ryan_frederick.painting.rating.RatingRepository;
import com.ryan_frederick.painting.user.User;
import com.ryan_frederick.painting.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/painting")
public class PaintingController {

    @Autowired
    private final PaintingUploaderService paintingUploader;

    Logger logger = LogManager.getLogger(PaintingController.class);

    @Autowired
    private final RatingRepository ratingRepository;

    @Autowired
    private final PaintingRepository paintingRepository;

    @Autowired
    private final UserRepository userRepository;


    public PaintingController(PaintingUploaderService paintingUploader, RatingRepository ratingRepository, PaintingRepository paintingRepository, UserRepository userRepository) {
        this.paintingUploader = paintingUploader;
        this.ratingRepository = ratingRepository;
        this.paintingRepository = paintingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    List<Painting> findAllPaintings() {
        return paintingRepository.findAllPaintings();
    }

    // only logged in users can create paintings
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    void createPainting(@RequestBody CreatePaintingRequest createPaintingRequest, Authentication authentication) throws IOException {
        String username = authentication.getName();

        // get id of user who created the painting
        Integer id = userRepository.findUserByUsername(username).map(User::id).orElse(null);

        // upload image to s3 and get auto generated image name back
        String imageName = paintingUploader.uploadImage(createPaintingRequest.dataUrl());

        Painting paintingToAdd = new Painting(
                null,
                createPaintingRequest.title(),
                createPaintingRequest.description(),
                LocalDateTime.now(),
                5,
                imageName
        );

        paintingRepository.createPainting(paintingToAdd, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    void deletePainting(@PathVariable Integer id) {
        // find name of painting image
        String imageName = paintingRepository.findPaintingById(1).map(Painting::imageName).orElse(null);

        // delete image on s3 bucket
        paintingUploader.deleteImage(imageName);

        paintingRepository.deletePainting(id);
    }

    @GetMapping("/user/{id}")
    List<Painting> findAllPaintingsByUser(@PathVariable Integer id) {
        return paintingRepository.findAllPaintingsByUser(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/rate")
    @ResponseStatus(HttpStatus.CREATED)
    void ratePainting(@RequestBody PaintingRatingRequest paintingRatingRequest, Authentication authentication) {
        Integer paintingId = paintingRatingRequest.paintingId();
        double requestRating = paintingRatingRequest.rating();

        // find if user has already rated painting
        Integer userId = userRepository.findUserByUsername(authentication.getName()).map(User::id).orElse(null);

        Optional<Rating> userRating = ratingRepository.findRating(paintingRatingRequest.paintingId(), userId);

        // if user has already rated painting update that requestRating
        if (userRating.isPresent()) {
            ratingRepository.updateRating(paintingId, userId, requestRating);
        // otherwise create new requestRating
        } else {
            ratingRepository.createRating(paintingId, userId, requestRating);
        }

        // find all ratings for painting and average them, update average rank
        List<Rating> ratings = ratingRepository.findAllRatingsForPainting(paintingId);

        double averageRating = 0;
        for (Rating rating : ratings) {
            averageRating += rating.rating();
        }
        logger.info(averageRating);

        averageRating /= ratings.size();

        // round average to nearest half
        averageRating = (double) Math.round(averageRating * 2) / 2;

        //logger.info(averageRating);

        paintingRepository.updatePaintingRating(paintingId, averageRating);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/test")
    void test(Authentication authentication) {
        logger.info(authentication.getName());
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.CREATED)
    String getPaintingUrl(@RequestBody String imageName) {
        return paintingUploader.createImageUrl(imageName);
    }
}
