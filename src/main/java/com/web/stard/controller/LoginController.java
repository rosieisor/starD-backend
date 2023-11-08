package com.web.stard.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class LoginController {

//    private final LoginService loginService;
//    private final MemberDetailsService memberDetailsService;
//
//    @GetMapping("/current-member")
//    public String getCurrentMember() {
//
//        System.out.println("getCurrentMember()");
//
//        // 현재 로그인한 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName(); // 사용자의 아이디 (username) 가져오기
//
//            System.out.println("로그인 O " + username);
//            // 필요에 따라 사용자의 권한 정보도 가져올 수 있습니다.
//            // List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
//
////            return "현재 로그인한 사용자: " + username;
//            return username;
//        } else {
//            System.out.println("로그인 X");
////            return "로그인되지 않은 사용자";
//            return null;
//        }
//    }

//    @PostMapping("/sign-in")
//    public ResponseEntity<String>  signIn_(@RequestBody MemberRequestDto memberRequestDto) throws RuntimeException {
//
//        Member member = loginService.login(memberRequestDto.getUsername(), memberRequestDto.getPassword());
//
//        System.out.println(memberRequestDto.getUsername());
//        if (member != null) {
//            return ResponseEntity.ok("로그인 성공!");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
//        }
//
//    }

//    @ExceptionHandler(value = RuntimeException.class)
//    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
//        HttpHeaders responseHeaders = new HttpHeaders();
//
//        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
//
//        Map<String, String> map =new HashMap<>();
//        map.put("error type", httpStatus.getReasonPhrase());
//        map.put("code", "400");
//        map.put("message", "에러 발생");
//
//        return new ResponseEntity<>(map, responseHeaders, httpStatus);
//    }

}
