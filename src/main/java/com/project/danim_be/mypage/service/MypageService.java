package com.project.danim_be.mypage.service;

import com.project.danim_be.common.exception.CustomException;
import com.project.danim_be.common.exception.ErrorCode;
import com.project.danim_be.common.util.Message;
import com.project.danim_be.common.util.S3Uploader;
import com.project.danim_be.common.util.StatusEnum;
import com.project.danim_be.member.entity.Member;
import com.project.danim_be.member.repository.MemberRepository;
import com.project.danim_be.mypage.dto.RequestDto.MypageRequestDto;
import com.project.danim_be.mypage.dto.ResponseDto.MypageResponseDto;
import com.project.danim_be.mypage.dto.ResponseDto.MypageReviewResponseDto;
import com.project.danim_be.post.dto.ResponseDto.MypagePostResponseDto;
import com.project.danim_be.post.entity.Post;
import com.project.danim_be.post.entity.QPost;
import com.project.danim_be.post.repository.PostRepository;
import com.project.danim_be.review.entity.QReview;
import com.project.danim_be.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.danim_be.common.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private final JPAQueryFactory queryFactory;
    private final PostRepository postRepository;


    //마이페이지 - 사용자 정보
    @Transactional(readOnly = true)
    public ResponseEntity<Message> memberInfo(Long ownerId, Long memberId) {
        Member owner = findMember(ownerId);
        Member member = findMember(memberId);
        MypageResponseDto mypageResponseDto;
        if (ownerId.equals(memberId)){
            mypageResponseDto = new MypageResponseDto(member, true);
        } else {
            mypageResponseDto = new MypageResponseDto(owner, false);
        }
        return ResponseEntity.ok(Message.setSuccess(StatusEnum.OK, "조회 성공", mypageResponseDto));
    }

    //마이페이지 게시물 정보
    @Transactional(readOnly = true)
    public ResponseEntity<Message> memberPosts(Long ownerId, Long memberId) {
        Member owner = findMember(ownerId);
        Member member = findMember(memberId);
        List<MypagePostResponseDto> mypagePostResponseDtoList;
        if (ownerId.equals(memberId)) {
            mypagePostResponseDtoList = validMember(member, true);
        } else {
            mypagePostResponseDtoList = validMember(owner, false);
        }
        return ResponseEntity.ok(Message.setSuccess(StatusEnum.OK, "조회 성공", mypagePostResponseDtoList));
    }

    //마이페이지 내가 받은 후기
    @Transactional(readOnly = true)
    public ResponseEntity<Message> memberReview(Long ownerId, Long memberId) {
        Member owner = findMember(ownerId);
        Member member = findMember(memberId);
        List<MypageReviewResponseDto> reviewList;

        if (ownerId.equals(memberId)){
            reviewList = getReview(member.getId());
        } else {
            reviewList = getReview(owner.getId());
        }
        return ResponseEntity.ok(Message.setSuccess(StatusEnum.OK, "조회 성공", reviewList));
    }

    //마이페이지 회원정보 수정
    @Transactional
    public ResponseEntity<Message> editMember(Long ownerId, MypageRequestDto mypageRequestDto, Member member) throws IOException {
        Member owner = findMember(ownerId);

        if (owner.getId().equals(member.getId())) {
            String imageUrl = s3Uploader.upload(mypageRequestDto.getImage());
            member.editMember(mypageRequestDto, imageUrl);

            memberRepository.save(member);

        } else throw new CustomException(ErrorCode.DO_NOT_HAVE_PERMISSION);
        return ResponseEntity.ok(Message.setSuccess(StatusEnum.OK, "수정 완료", member));
    }

    //마이페이지 게시물 공통 메서드
    private List<MypagePostResponseDto> validMember(Member member, Boolean owner) {
        List<Post> postList = postRepository.findAllByMemberOrderByCreatedAtDesc(member);
        List<MypagePostResponseDto> mypagePostResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            mypagePostResponseDtoList.add(new MypagePostResponseDto(post, owner));
        }
        return mypagePostResponseDtoList;
    }

    //멤버 검증 공통 메서드
    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    //마이페이지 리뷰 공통 메서드
    private List<MypageReviewResponseDto> getReview(Long memberId) {
        QReview qReview = QReview.review1;
        QPost qPost = QPost.post;

        List<Review> reviewList = queryFactory
                .selectFrom(qReview)
                .join(qReview.post, qPost)
                .where(qPost.member.id.eq(memberId))
                .fetch();
        List<MypageReviewResponseDto> mypageReviewResponseDtoList = new ArrayList<>();
        for (Review review : reviewList) {
            mypageReviewResponseDtoList.add(new MypageReviewResponseDto(review));
        }
        return mypageReviewResponseDtoList;
    }
}