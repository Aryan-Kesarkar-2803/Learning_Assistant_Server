package com.example.learning_assistant.security.config.jwt;

import com.example.learning_assistant.security.service.CustomUserDetailsService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
//import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext applicationContext ;
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();


    public JwtFilter(JwtService jwtService, ApplicationContext applicationContext){
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        if (!(authHeader.startsWith("Bearer "))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - Give valid token");
            return;
        }

        String token = authHeader.substring(7);
        String email = "";
        try{
            email = jwtService.extractEmailFromToken(token);
        }catch(Exception e){
            System.out.println("Jwt Exception - "+e.getMessage());
            throw new AuthenticationException("Invalid Token or Token expired");
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = null;
            try{
                userDetails = applicationContext.getBean(CustomUserDetailsService.class).loadUserByUsername(email);
            }catch (Exception e){
                System.out.println("Exception - "+ e.getMessage());
                response.setStatus(400);
                response.getWriter().write("User/Vendor doesn't exist");
                return;
            }

            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String excudedPaths = dotenv.get("JWT_EXCLUDED_PATHS");
        if(excudedPaths == null){
            excudedPaths = System.getenv("JWT_EXCLUDED_PATHS");
        }
        String[] excudedPathsArray = excudedPaths.split(",");
        String path = request.getServletPath();
        return Arrays.asList(excudedPathsArray).contains(path);
    }

}
