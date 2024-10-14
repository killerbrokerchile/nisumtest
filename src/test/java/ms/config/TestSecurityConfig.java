package ms.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import ms.jwt.JwtAuthenticationFilter;
import ms.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class TestSecurityConfig {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain testSecurityFilterChain() throws Exception {
        return new SecurityFilterChain() {

            public boolean matches(HttpServletRequest request) {
                return false;
            }

            @Override
            public List<Filter> getFilters() {
                List<Filter> filters = new ArrayList<>();

                // Agrega filtros de seguridad a la lista
                filters.add(new JwtAuthenticationFilter(jwtService, userDetailsService));
                filters.add(new UsernamePasswordAuthenticationFilter());
                // Agrega más filtros según sea necesario

                return filters;
            }

            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }
        };
    }
}
