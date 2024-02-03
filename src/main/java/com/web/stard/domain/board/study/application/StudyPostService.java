package com.web.stard.domain.board.study.application;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.StudyPost;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.global.repository.StarScrapRepository;
import com.web.stard.domain.board.study.repository.StudyPostRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter @Setter
@Service
public class StudyPostService {

    @Autowired
    MemberService memberService;
    @Autowired StudyService studyService;
    @Autowired StudyPostRepository studyPostRepository;
    @Autowired StarScrapRepository starScrapRepository;

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    /* 게시글 전체 조회 */
    public List<StudyPost> getStudyPostList(Long studyId) {
        Study study = studyService.findById(studyId);
        List<StudyPost> posts = studyPostRepository.findByStudyOrderByCreatedAtDesc(study);

        for (StudyPost p : posts) {
            List<StarScrap> allStarList = starScrapRepository.findAllByStudyPostAndTypeAndTableType(p, ActType.STAR, PostType.STUDYPOST);

            p.setStarCount(allStarList.size());
        }

        return posts;
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
        StudyPost studyPost = new StudyPost(study, member, title, content, PostType.STUDYPOST);

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
    public StudyPost updatePost(Long postId, String title, String content, MultipartFile file, Boolean fileUpdateStatus) {
        StudyPost studyPost = getStudyPost(postId, null);

        studyPost.setTitle(title);
        studyPost.setContent(content);

        if (fileUpdateStatus) {
            // 파일 수정된 경우
            String originFileUrl = "";
            if (studyPost.getFileUrl() != null) {
                originFileUrl = studyPost.getFileUrl();
            }

            if (file == null) {
                // 새로운 파일이 없는 경우
                studyPost.setFileName(null);
                studyPost.setFileUrl(null);
            } else {
                File folder = new File(uploadFolder + studyPost.getStudy().getId());

                if (!folder.exists()) {
                    folder.mkdirs(); // 디렉토리 생성
                }

                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadFolder + studyPost.getStudy().getId() + "/" + fileName);

                try {
                    file.transferTo(destinationFile);

                    studyPost.setFileName(file.getOriginalFilename());
                    studyPost.setFileUrl("/" + fileName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            studyPostRepository.save(studyPost);

            // 기존 파일 삭제
            deleteFile(studyPost.getStudy().getId(), originFileUrl);
        } else {
            studyPostRepository.save(studyPost);
        }

        return studyPost;
    }

    /* 게시글 삭제 */
    public boolean deletePost(Long postId) {
        StudyPost studyPost = getStudyPost(postId, null);

        Long studyId = studyPost.getStudy().getId();
        String fileUrl = studyPost.getFileUrl();

        studyPostRepository.delete(studyPost);

        if (getStudyPost(postId, null) == null) { // 삭제됨
            deleteFile(studyPost.getStudy().getId(), studyPost.getFileUrl()); // 파일도 삭제
            
            return true;
        } return false;
    }

    /* 파일 삭제 */
    private void deleteFile(Long studyId, String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                // 파일 경로를 이용하여 파일 객체 생성
                File fileToDelete = new File(uploadFolder + studyId + filePath);

                // 파일이 존재하면 삭제
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            } catch (Exception e) {
                // 파일 삭제 중 오류가 발생할 경우 예외 처리
                e.printStackTrace();
            }
        }
    }

    /* 검색 */
    public List<StudyPost> searchStudyPost(Long studyId, String searchType, String searchWord) {
        Study study = studyService.findById(studyId);
        List<StudyPost> posts;

        if (searchType.equals("제목")) {
            posts = studyPostRepository.findByStudyAndTitleContainingOrderByCreatedAtDesc(study, searchWord);
        } else if (searchType.equals("내용")) {
            posts = studyPostRepository.findByStudyAndContentContainingOrderByCreatedAtDesc(study, searchWord);
        } else { // 닉네임
            Member member = memberService.findByNickname(searchWord);
            if (member == null) {
                return null;
            }
            posts = studyPostRepository.findByStudyAndMemberOrderByCreatedAtDesc(study, member);
        }

        for (StudyPost p : posts) {
            List<StarScrap> allStarList = starScrapRepository.findAllByStudyPostAndTypeAndTableType(p, ActType.STAR, PostType.STUDYPOST);

            p.setStarCount(allStarList.size());
        }

        return posts;
    }

    /* 파일 다운로드 */
    public ResponseEntity<Resource> download(Long postId) {
        StudyPost post = getStudyPost(postId, null);

        try {
            Path filePath = Paths.get(uploadFolder, String.valueOf(post.getStudy().getId()), post.getFileUrl());

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            // 파일 다운로드 중 오류가 발생할 경우 예외 처리
            throw new RuntimeException(e);
        }
    }
}
