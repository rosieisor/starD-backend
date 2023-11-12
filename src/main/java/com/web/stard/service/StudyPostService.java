package com.web.stard.service;

import com.web.stard.domain.Member;
import com.web.stard.domain.Study;
import com.web.stard.domain.StudyPost;
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

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    /* 게시글 전체 조회 */
    public List<StudyPost> getStudyPostList(Long studyId) {
        Study study = studyService.findById(studyId);

        return studyPostRepository.findByStudyOrderByCreatedAtDesc(study);
    }

    /* 게시글 상세 조회 */
    public StudyPost getStudyPost(Long postId) {
        Optional<StudyPost> result = studyPostRepository.findById(postId);

        if (result.isPresent())
            return result.get();

        return null;
    }

    /* 게시글 등록 */
    public StudyPost registerPost(Long studyId, String title, String content,
                                  MultipartFile file, Authentication authentication) {
        Study study = studyService.findById(studyId); // 스터디
        Member member = memberService.find(authentication.getName()); // 작성자
        StudyPost studyPost = new StudyPost(study, member, title, content);

        File folder = new File(uploadFolder);

        if (!folder.exists()) {
            folder.mkdirs(); // 디렉토리 생성
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File destinationFile = new File(uploadFolder + studyId);

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

    /* 게시글 수정 */
    public StudyPost updatePost(Long postId, String title, String content, MultipartFile file) {
        StudyPost studyPost = getStudyPost(postId);

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
        StudyPost studyPost = getStudyPost(postId);

        studyPostRepository.delete(studyPost);

        if (getStudyPost(postId) == null) { // 삭제됨
            return true;
        } return false;
    }
}
