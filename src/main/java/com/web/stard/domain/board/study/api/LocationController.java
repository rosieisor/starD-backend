package com.web.stard.domain.board.study.api;

import com.web.stard.domain.board.study.domain.Location;
import com.web.stard.domain.board.study.application.LocationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{id}/all")
    public Location getRecommendedPlaceAll(@PathVariable(name = "id") Long id, Authentication authentication) throws Exception {
        // 스터디원 전체로 계산
        return locationService.getRecommendedPlaceAll(id);
    }

    @GetMapping("/{id}")
    public Location getRecommendedPlace(@PathVariable(name = "id") Long id, @RequestParam(name = "participantsStr") String participantsStr,
                                        Authentication authentication) throws Exception {
        // 해당 스터디(studyId)에서 모임에 참여하는 참여자들(participantsStr)로 중간 지점 구하기
        return locationService.getRecommendedPlace(id, participantsStr);
    }

    @GetMapping("/find")
    public Location getFindMidpoint(@RequestParam(name = "placeList") List<String> placeList) throws Exception {
        for (String s : placeList) {
            System.out.println("장소 : " + s);
        }
        return locationService.getFindMidpoint(placeList);
    }

    @GetMapping("/reverse/geocoder")
    public String getAddress(@RequestParam(name = "latitude") Double latitude, @RequestParam(name = "longitude") Double longitude,
                             Authentication authentication) throws Exception {
        return locationService.reverseGeocoder(latitude, longitude);
    }
}
