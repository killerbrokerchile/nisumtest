package ms.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        log.info("LoginRequest" + request.toString());
       // return ResponseEntity.ok(new AuthResponse());
        AuthResponse response = authService.login(request);
        log.info("LoginResponse " + response.toString());
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        } else {
               return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request)
    {
    	AuthResponse response = authService.register(request);
        log.info("response " +response.toString());
         if (response.getCode() == 201) {
            return ResponseEntity.ok(response);
        } else {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
