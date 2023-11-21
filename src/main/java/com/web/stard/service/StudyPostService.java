package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.StarScrapRepository;
import com.web.stard.repository.StudyPostRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter @Setter
@Service
public class StudyPostService {

    @Autowired MemberService memberService;
    @Autowired StudyService studyService;
    @Autowired StudyPostRepository studyPostRepository;
    @Autowired StarScrapRepository starScrapRepository;

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    /* 게시글 전체 조회 */
    public List<StudyPost> getStudyPostList(Long studyId) {
        Study study = studyService.findById(studyId);

        return studyPostRepository.findByStudyOrderByCreatedAtDesc(study);
    }

    /* 게시글 상세 조회 */
    public StudyPost getStudyPost(Long postId, String userId) {
        Optional<StudyPost> result = studyPostRepository.findById(postId);

        if (result.isPresent()) {
            StudyPost studyPost = result.get();

            if (userId != null) {
                if (!studyPost.getMember().getId().equals(userId)) {
                    // 작성자 != 현재 로그인 한 유저
                    studyPost.setViewCount(studyPost.getViewCount()+1);
                    studyPostRepository.save(studyPost);
                }
            }

            List<StarScrap> allStarList = starScrapRepository.findAllByStudyPostAndTypeAndTableType(studyPost, ActType.STAR, PostType.STUDYPOST);

            studyPost.setStarCount(allStarList.size());

            return studyPost;
        }
        return null;
    }

    /* 게시글 등록 */
    public StudyPost registerPost(Long studyId, String title, String content,
                                  MultipartFile file, Authentication authentication) {
        Study study = studyService.findById(studyId); // 스터디
        Member member = memberService.find(authentication.getName()); // 작성자
        StudyPost studyPost = new StudyPost(study, member, title, content);

        if (file == null) {
            studyPostRepository.save(studyPost);

            return studyPost;
        } else {
            File folder = new File(uploadFolder + studyId);

            if (!folder.exists()) {
                folder.mkdirs(); // 디렉토리 생성
            }

            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadFolder + studyId + "/" + fileName);

            try {
                file.transferTo(destinationFile);

                studyPost.setFileName(file.getOriginalFilename());
                studyPost.setFileUrl("/" + fileName);

                studyPostRepository.save(studyPost);

                return studyPost;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* 게시글 수정 */
    public StudyPost updatePost(Long postId, String title, String content, MultipartFile file) {
        StudyPost studyPost = getStudyPost(postId, null);

        studyPost.setTitle(title);
        studyPost.setContent(content);

        File folder = new File(uploadFolder);

        if (!folder.exists()) {
            folder.mkdirs(); // 디렉토리 생성
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File destinationFile = new File(uploadFolder + studyPost.getStudy().getId());

        try {
            file.transferTo(destinationFile);

            studyPost.setFileName(file.getOriginalFilename());
            studyPost.setFileUrl("/" + fileName);

            studyPostRepository.save(studyPost);

            return studyPost;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* 게시글 삭제 */
    public boolean deletePost(Long postId) {
        StudyPost studyPost = getStudyPost(postId, null);

        studyPostRepository.delete(studyPost);

        if (getStudyPost(postId, null) == null) { // 삭제됨
            return true;
        } return false;
    }
}
