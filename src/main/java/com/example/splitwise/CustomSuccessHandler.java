package com.example.splitwise.security;

import com.example.splitwise.model.User;
import com.example.splitwise.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final String frontendRedirectUrl; // e.g. http://localhost:8081

    public CustomSuccessHandler(UserService userService, String frontendRedirectUrl) {
        this.userService = userService;
        this.frontendRedirectUrl = frontendRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email != null) {
                Optional<User> opt = userService.findByEmailOptional(email);
                if (opt.isEmpty()) {
                    // create a minimal User record
                    User u = new User();
                    String local = email.split("@")[0];
                    u.setUsername(local);
                    u.setEmail(email);
                    u.setEmailVerified(true);
                    // set total if your model requires BigDecimal.ZERO
                    u.setTotal(java.math.BigDecimal.ZERO);
                    userService.createUser(u);
                }
                // debug print token (optional)
                try {
                    String idToken = oidcUser.getIdToken().getTokenValue();
                    System.out.println("===== GOOGLE ID TOKEN =====");
                    System.out.println(idToken);
                    System.out.println("===========================");
                } catch (Exception ignored) {}
            }
        }

        // clear saved request to avoid Spring redirecting to a protected API path
        clearAuthenticationAttributes(request);

        // redirect to frontend dashboard (change path if needed)
        String target = frontendRedirectUrl != null ? frontendRedirectUrl + "/dashboard" : "/";

        response.sendRedirect(target);
    }
}
