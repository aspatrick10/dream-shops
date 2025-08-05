package com.dailycodework.dreamshops.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.request.CreateUserRequest;
import com.dailycodework.dreamshops.request.LoginRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.response.LoginResponse;
import com.dailycodework.dreamshops.security.jwt.JwtUtils;
import com.dailycodework.dreamshops.security.user.ShopUserDetails;
import com.dailycodework.dreamshops.service.user.IUserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        ShopUserDetails userDetails = (ShopUserDetails) authentication
                .getPrincipal();

        // Generate JWT token
        String jwtToken = jwtUtils.generateTokenFromUser(userDetails);

        // Set JWT token in HTTP-only cookie
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwtToken)
                .httpOnly(true).secure(true) // Set to true in production with
                                             // HTTPS
                .path("/").maxAge(24 * 60 * 60) // 24 hours
                .sameSite("Strict").build();

        response.addHeader("Set-Cookie", jwtCookie.toString());

        // Extract roles from user details
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority()).toList();

        // Return response without the token (since it's in cookie)
        LoginResponse loginResponse = new LoginResponse(
                userDetails.getUsername(), roles, null); // Set token to null
                                                         // since it's in cookie

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody CreateUserRequest userRequest) {
        try {
            User user = userService.createUser(userRequest);
            ApiResponse response = new ApiResponse(
                    "User registered successfully!", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (AlreadyExistsException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", e.getMessage());
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Registration failed: " + e.getMessage());
            map.put("status", false);
            return new ResponseEntity<Object>(map,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Clear the JWT cookie
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true) // Set to true in production with
                                             // HTTPS
                .path("/").maxAge(0) // Expire immediately
                .sameSite("Strict").build();

        response.addHeader("Set-Cookie", jwtCookie.toString());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "User logged out successfully");
        responseMap.put("status", true);

        return ResponseEntity.ok(responseMap);
    }
}
