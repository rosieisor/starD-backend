package com.web.stard.controller;

import com.web.stard.domain.Member;
import com.web.stard.domain.Profile;
import com.web.stard.repository.ProfileRepository;
import com.web.stard.service.MemberService;
import com.web.stard.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @Value("${file.profileImagePath}")
    private String uploadFolder;

    @PostMapping("/imageTest")
    public Profile imageTest(@RequestParam(value = "introduce") String introduce, MultipartFile file){

        Member member = memberService.find("final");

        System.out.println("uploadFolder 의 값 : " + uploadFolder);

        UUID uuid = UUID.randomUUID();
        System.out.println("uuid 의 값 : " + uuid);

        String imageFileName = uuid + "_" + file.getOriginalFilename();
        System.out.println("imageFileName 의 값 : " + imageFileName);

        File destinationFile = new File(uploadFolder + imageFileName);
        System.out.println("destinationFile 의 값 : " + destinationFile);

        try {
            file.transferTo(destinationFile);
            Profile profile = member.getProfile();

            if (profile != null) {
                // 이미지가 이미 존재하면 url 업데이트
                profile.setImgUrl("/profileImages/" + imageFileName);
            } else {
                // 이미지가 없으면 객체 생성 후 저장
                profile = Profile.builder()
                        .imgUrl("/profileImages/" + imageFileName)
                        .imgName(file.getOriginalFilename())
                        .build();
            }
            profile.setIntroduce(introduce);
            profileRepository.save(profile);
            return profile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
