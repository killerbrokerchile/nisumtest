package ms.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestEncrypt {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword1 = encoder.encode("alejandro123");
        String encodedPassword2 = encoder.encode("alex123");

        System.out.println(">>> 'alejandro123': " + encodedPassword1);
        System.out.println(">>> 'alex123': " + encodedPassword2);
    }
}
