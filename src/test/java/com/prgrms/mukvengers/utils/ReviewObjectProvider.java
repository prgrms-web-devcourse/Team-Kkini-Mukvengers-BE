package com.prgrms.mukvengers.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.prgrms.mukvengers.domain.crew.model.Crew;
import com.prgrms.mukvengers.domain.review.dto.request.CreateLeaderReviewRequest;
import com.prgrms.mukvengers.domain.review.dto.request.CreateMemberReviewRequest;
import com.prgrms.mukvengers.domain.review.model.Review;
import com.prgrms.mukvengers.domain.user.model.User;

public class ReviewObjectProvider {

	public static final LocalDateTime PROMISE_TIME = LocalDateTime.now();
	public static final String CONTENT = "추가로 작성하고 싶은 내용을 입력해주세요.";
	public static final Integer MANNER_SCORE = 5;
	public static final Integer TASTE_SCORE = 5;

	public static Review createLeaderReview(User reviewer, User reviewee, Crew crew) {
		return Review.builder()
			.reviewer(reviewer)
			.reviewee(reviewee)
			.crew(crew)
			.promiseTime(PROMISE_TIME)
			.content(CONTENT)
			.mannerScore(MANNER_SCORE)
			.tasteScore(TASTE_SCORE)
			.build();
	}

	public static Review createMemberReview(User reviewer, User reviewee, Crew crew) {
		return Review.builder()
			.reviewer(reviewer)
			.reviewee(reviewee)
			.crew(crew)
			.promiseTime(PROMISE_TIME)
			.content(CONTENT)
			.mannerScore(MANNER_SCORE)
			.tasteScore(0)
			.build();
	}

	public static List<Review> createReviews(User reviewer, User reviewee, Crew crew) {

		return IntStream.range(0, 20)
			.mapToObj(i -> createMemberReview(reviewer, reviewee, crew)).collect(Collectors.toList());
	}

	public static CreateLeaderReviewRequest createLeaderReviewRequest(Long revieweeId) {
		return new CreateLeaderReviewRequest(
			revieweeId,
			CONTENT,
			MANNER_SCORE,
			TASTE_SCORE
		);
	}

	public static CreateMemberReviewRequest createMemberReviewRequest(Long revieweeId) {
		return new CreateMemberReviewRequest(
			revieweeId,
			CONTENT,
			MANNER_SCORE
		);
	}
}