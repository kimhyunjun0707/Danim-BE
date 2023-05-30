package com.project.danim_be.review.entity;

import com.project.danim_be.member.entity.Member;
import com.project.danim_be.post.entity.Post;

import com.project.danim_be.review.dto.ReviewRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Double point;		//점수

	@Column(nullable = false)
	private String review;		//후기

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	public Review(ReviewRequestDto reviewRequestDto, Post post, Member member) {
		this.review = reviewRequestDto.getComment();
		this.point = reviewRequestDto.getScore();
		this.post = post;
		this.member = member;
	}
}
