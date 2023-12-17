package com.corgan.angularbackend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//First point where incoming requests get through

@Configuration
public class VerifyConfig extends OncePerRequestFilter {

    @Value("${allowed.origins}")
    private String origin;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!request.getHeader("origin").equals(origin)) return;

        //if request path includes a request to login or register it skips cors
        if( (request.getServletPath().compareTo("/api/register") == 0) || (request.getServletPath().compareTo("/api/login") == 0) ) {

            filterChain.doFilter(request, response);

        }else {
            if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {

                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                    response.setHeader("Access-Control-Max-Age", "3600"); // Cache the preflight result for 1 hour

                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
            }

            if (request.getMethod().equals("POST")) {

                String requestToken = request.getHeader("Authorization");
                if(requestToken.compareTo("") == 0){
                    return;
                }
                requestToken = requestToken.replaceAll("Bearer ", "");

                try {
                    FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(requestToken);

                    HttpServletRequest req = request;
                    req.setAttribute("emailRef", firebaseToken.getEmail());
                    filterChain.doFilter(request, response);
                    return;

                } catch (FirebaseAuthException e) {
                    System.out.println("Error on the main function verify: " + e.getMessage());
                    response.setStatus(498);
                    return;
                }
            }
            filterChain.doFilter(request, response);

        }

    }


}
