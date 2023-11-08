package com.web.stard.Login;


//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class LoginLoginTestEntity {

//    @Mock
//    private TestRepository testRepository;
//
//    @InjectMocks
//    private TestService testService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @InjectMocks
//    private MemberDetailsService memberDetailsService;
//
//    @Test
//    public void testLoadUserByUsername_ExistingUser() {
//        // 테스트를 위해 가상의 사용자 정보를 생성합니다.
//        String username = "test";
//        String rawPassword = "testtest";
//        String auth = "USER";
//
//        String username2 = "test2";
//        String rawPassword2 = "testtest";
//        String auth2 = "USER";
//
//        // 가상 사용자 정보를 저장합니다.
//        LoginTestEntity member = new LoginTestEntity(username, new BCryptPasswordEncoder().encode(rawPassword), auth);
//        testRepository.save(member);
//
//        LoginTestEntity member2 = new LoginTestEntity(username2, new BCryptPasswordEncoder().encode(rawPassword2), auth2);
//        testRepository.save(member2);

//        String username = "test";
//        String rawPassword = "testtest";
//
//        // 사용자 정보를 조회합니다.
//        UserDetails userDetails = testService.loadUserByUsername(username);
//
//        // 조회한 사용자 정보의 비밀번호가 암호화된 비밀번호와 일치하는지 확인합니다.
//        assertEquals(new BCryptPasswordEncoder().encode(rawPassword), userDetails.getPassword());
//    }
//
//    @Test
//    public void pwTest(){
//
//        // 원본 패스워드
//        String originalPassword = "testtest";
//
//        // 패스워드 암호화
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword1 = passwordEncoder.encode(originalPassword);
//        String encodedPassword2 = passwordEncoder.encode(originalPassword);
//
//        // 두 암호화된 패스워드가 일치하는지 비교
//        assertTrue(passwordEncoder.matches(originalPassword, encodedPassword1));
//        assertTrue(passwordEncoder.matches(originalPassword, encodedPassword2));
//    }
//
//
//    @Test
//    public void testLoadUserByUsername_NonExistingUser() {
//        // 존재하지 않는 사용자를 조회하려고 할 때 UsernameNotFoundException이 발생해야 합니다.
//        String nonExistingUsername = "nonexistinguser";
//        assertThrows(UsernameNotFoundException.class, () -> {
//            testService.loadUserByUsername(nonExistingUsername);
//        });
//    }




}
