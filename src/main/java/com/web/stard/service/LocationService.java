package com.web.stard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.web.stard.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Getter @Setter
//@AllArgsConstructor
@Service
public class LocationService {

    @Autowired StudyService studyService;
    @Autowired MemberService memberService;

    @Value("${naver.client.id}")
    private String clientId;
    @Value("${naver.client.secret}")
    private String clientSecret;

    public Location getRecommendedPlaceAll(Long studyId) throws Exception {
        List<Member> participants = new ArrayList<>();
        List<StudyMember> studyMembers = studyService.findStudyMember(studyId, null);
        List<Location> participantsLocation = null;

        for (StudyMember sm : studyMembers) {
            participants.add(sm.getMember());
        }

        // 겹치는 주소 가중치 부여
        for (Member m : participants) {
            if (m.getCity() != null && m.getDistrict() != null
                    && !m.getCity().equals("") && !m.getDistrict().equals("")) { // 입력되어 있는 사용자만 추출
                if (participantsLocation == null) {
                    participantsLocation = new ArrayList<>();
                }

                String address = m.getCity() + " " + m.getDistrict();

                boolean exists = false;
                for (Location location : participantsLocation) {
                    if (address.equals(location.getAddress())) { // 주소가 겹치면 가중치 증가
                        exists = true;
                        location.setWeight(location.getWeight() + 1);
                    }
                }

                if (!exists) { // 겹치지 않으면 새로 추가
                    participantsLocation.add(new Location(address, 1));
                }
            }
        }

        if (participantsLocation == null) { // 아무도 주소 등록을 해두지 않은 경우 서울 시청 기본
            Location location = new Location();

            location.setLatitude(126.9779);
            location.setLongitude(37.5665);

            return location;
        }

        for (Location location : participantsLocation) {
            Location geoLoc = geocoder(location);

            location.setLatitude(geoLoc.getLatitude());
            location.setLongitude(geoLoc.getLongitude());

            System.out.println(location.toString());
        }

        return Location.calculate(participantsLocation);
    }

    public Location getRecommendedPlace(Long studyId, String participantsStr) throws Exception {
        List<Member> participants = new ArrayList<>();
        String[] participantsSplit = participantsStr.split(",");
        List<StudyMember> studyMembers = studyService.findStudyMember(studyId, null);
        List<Location> participantsLocation = null;

        for (String s : participantsSplit) {
            if (studyMembers.stream()
                    .map(StudyMember::getMember)
                    .anyMatch(member -> member.getId().equals(s))) { // studyMembers에 존재하면
                participants.add(memberService.find(s));
            }
        }

        // 겹치는 주소 가중치 부여
        for (Member m : participants) {
            if (m.getCity() != null && m.getDistrict() != null
                    && !m.getCity().equals("") && !m.getDistrict().equals("")) { // 입력되어 있는 사용자만 추출
                if (participantsLocation == null) {
                    participantsLocation = new ArrayList<>();
                }

                String address = m.getCity() + " " + m.getDistrict();

                boolean exists = false;
                for (Location location : participantsLocation) {
                    if (address.equals(location.getAddress())) { // 주소가 겹치면 가중치 증가
                        exists = true;
                        location.setWeight(location.getWeight() + 1);
                    }
                }

                if (!exists) { // 겹치지 않으면 새로 추가
                    participantsLocation.add(new Location(address, 1));
                }
            }
        }

        if (participantsLocation == null) { // 아무도 주소 등록을 해두지 않은 경우 서울 시청 기본
            Location location = new Location();

            location.setLatitude(126.9779);
            location.setLongitude(37.5665);

            return location;
        }

        for (Location location : participantsLocation) {
            Location geoLoc = geocoder(location);

            location.setLatitude(geoLoc.getLatitude());
            location.setLongitude(geoLoc.getLongitude());

            System.out.println(location.toString());
        }

        return Location.calculate(participantsLocation);
    }

    public Location getFindMidpoint(List<String> placeList) throws Exception {
        List<Location> participantsLocation = new ArrayList<>();

        // 겹치는 주소 가중치 부여
        for (String s : placeList) {
            boolean exists = false;
            for (Location location : participantsLocation) {
                if (s.equals(location.getAddress())) { // 주소가 겹치면 가중치 증가
                    exists = true;
                    location.setWeight(location.getWeight() + 1);
                }
            }

            if (!exists) { // 겹치지 않으면 새로 추가
                participantsLocation.add(new Location(s, 1));
            }
        }

        for (Location location : participantsLocation) {
            Location geoLoc = geocoder(location);

            location.setLatitude(geoLoc.getLatitude());
            location.setLongitude(geoLoc.getLongitude());

            System.out.println(location.toString());
        }

        return Location.calculate(participantsLocation);
    }

    /* address를 위도, 경도로 변환 */
    public Location geocoder(Location loc) {
        Location location = new Location();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode")
                    .queryParam("query", loc.getAddress());

            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 200
                String jsonResponse = response.getBody();

                System.out.println(jsonResponse);

                // JSON 파싱을 위해 Jackson ObjectMapper를 사용합니다.
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode addressesNode = rootNode.path("addresses");

                if (addressesNode.isArray() && addressesNode.size() > 0) {
                    JsonNode addressNode = addressesNode.get(0);

                    location.setLatitude(Double.parseDouble(addressNode.get("x").asText()));
                    location.setLongitude(Double.parseDouble(addressNode.get("y").asText()));
                } else {
                    System.out.println("정보 찾을 수 없음");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return location;
    }

    /* 위도, 경도를 address로 변환 */
    public String reverseGeocoder(Double latitude, Double longitude) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            String coords = String.valueOf(longitude)+","+String.valueOf(latitude);

            System.out.println("파라미터 : " + coords);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc")
                    .queryParam("coords", coords)
                    .queryParam("orders", "roadaddr")
                    .queryParam("output", "json");

            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(builder.toUriString()));

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 200
                String jsonResponse = response.getBody();

                System.out.println(jsonResponse);

                // 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                JsonNode resultNode = jsonNode.path("results").get(0);
                String area1 = resultNode.path("region").path("area1").path("name").asText();
                String area2 = resultNode.path("region").path("area2").path("name").asText();
//                String area3 = resultNode.path("region").path("area3").path("name").asText();
//                String area4 = resultNode.path("region").path("area4").path("name").asText();
                String roadName1 = resultNode.path("land").path("name").asText();
                String roadName2 = resultNode.path("land").path("number1").asText();
                String roadName3 = resultNode.path("land").path("number2").asText();

                String address = area1 + " " + area2;
//                if (!area3.isBlank()) {
//                    address += " " + area3;
//
//                    if (!area4.isBlank()) {
//                        address += " " + area4;
//                    }
//                }
                address += " " + roadName1;
                if (!roadName2.isBlank()) {
                    address += " " + roadName2;

                    if (!roadName3.isBlank()) {
                        address += "-" + roadName3;
                    }
                }

                System.out.println("도로명주소 : " + address);

                return address;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
