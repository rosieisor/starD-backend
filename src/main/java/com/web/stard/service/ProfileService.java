package com.web.stard.service;

import com.web.stard.domain.Member;
import com.web.stard.domain.Profile;
import com.web.stard.dto.ProfileDto;
import com.web.stard.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberService memberService;

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    public Profile findById(long profileId) {
        Optional<Profile> result = profileRepository.findById(profileId);

        if (result.isPresent())
            return result.get();

        return null;
    }


    // [C] 프로필 생성
    @Transactional
    public Profile createProfile() {

        // TODO 추후 기본 프로필 사진 등록 코드 추가 필요
        Profile profile = Profile.builder()
                .credibility(5).build();      // 프로필 생성 시, 최초 개인 신뢰도 5

        profileRepository.save(profile);
        return profile;
    }

    // [U] 프로필 수정
    @Transactional
    public Profile updateProfile(String memberId, String introduce, MultipartFile imgFile) {

        Member member = memberService.find(memberId);

        if (member == null)
            return null;

        File folder = new File(uploadFolder);

        if (!folder.exists()) {
            folder.mkdirs(); // 디렉토리 생성
        }

        Profile profile = member.getProfile();

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + imgFile.getOriginalFilename();
        File destinationFile = new File(uploadFolder + imageFileName);

        try {
            imgFile.transferTo(destinationFile);

            profile.setImgUrl("/profileImages/" + imageFileName);
            profile.setImgName(imgFile.getOriginalFilename());
            profile.setIntroduce(introduce);

            return profile;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // [D] 프로필 삭제
    @Transactional
    public void deleteProfile(String memberId) {

        Member member = memberService.find(memberId);

        if (member != null){
            Profile result = member.getProfile();

            if (result != null)
                profileRepository.delete(result);
        }
    }

    // [R] 프로필 조회
    public Profile getProfile(String memberId) {

        Member member = memberService.find(memberId);

        if (member != null)
            return member.getProfile();

        return null;
    }

    // [U] 개인 신뢰도 업데이트
    @Transactional
    public Profile updateCredibility(String memberId) {
        Member member = memberService.find(memberId);

        if(member == null)
            return null;

        Profile profile = member.getProfile();
        float credibility = profile.getCredibility();

        // TODO 개인 신뢰도 알고리즘 추가

        return profile;
    }

    // [R] 개인 신뢰도 조회
    public float getCredibility(String memberId) {
        Member member = memberService.find(memberId);

        if(member != null) {
            Profile profile = member.getProfile();
            return profile.getCredibility();
        }
        return -1;
    }

//    public Profile test(ProfileDto profileDto) {
//
//        MultipartFile file = profileDto.getImgFile();
//
//        UUID uuid = UUID.randomUUID();
//        String imageFileName = uuid + "_" + file.getOriginalFilename();
//
//        File destinationFile = new File(uploadFolder + imageFileName);
//
//        try {
//            file.transferTo(destinationFile);
//            Profile profile = new Profile();
//
////            Image image = imageRepository.findByMember(member);
//            if (profile != null) {
//                // 이미지가 이미 존재하면 url 업데이트
//                profile.setImgUrl("/profileImages/" + imageFileName);
//            } else {
//                // 이미지가 없으면 객체 생성 후 저장
//                profile = Profile.builder()
//                        .imgUrl("/profileImages/" + imageFileName)
//                        .build();
//            }
//            profileRepository.save(profile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
}
