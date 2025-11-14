import org.springframework.util.DigestUtils;

public class TestMD5 {
    public static void main(String[] args) {
        String password = "123456";
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("MD5 of '123456': " + md5);
        System.out.println("Expected: e10adc3949ba59abbe56e057f20f883e");
        System.out.println("Match: " + md5.equals("e10adc3949ba59abbe56e057f20f883e"));
    }
}
