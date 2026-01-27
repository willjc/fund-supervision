import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$atMGDa3haPxyLWAIaw4lk.heltf4lU8sLnw/p5NYtPy6chOwTd1Oa";
        
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password matches: " + matches);
        
        // 生成新的加密密码
        String newEncoded = encoder.encode(rawPassword);
        System.out.println("New encoded password: " + newEncoded);
    }
}
