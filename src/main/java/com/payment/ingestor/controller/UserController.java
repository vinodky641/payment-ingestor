package com.payment.ingestor.controller;

import com.payment.ingestor.dto.user.UpdateUserRequest;
import com.payment.ingestor.dto.user.UserResponse;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(

            @Valid
            @RequestBody
            UpdateUserRequest request,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        UserResponse response = userService.updateProfile(request, userDetails);
        return ResponseEntity.ok(response);
    }

}
