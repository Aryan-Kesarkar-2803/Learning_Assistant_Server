package com.example.learning_assistant.service;

import com.cloudinary.Cloudinary;
import com.example.learning_assistant.model.User;
import com.example.learning_assistant.model.io.user.ApiResponse;
import com.example.learning_assistant.model.io.user.ImageData;
import com.example.learning_assistant.model.io.user.UserProfileRequest;
import com.example.learning_assistant.model.io.user.UserProfileResponse;
import com.example.learning_assistant.repository.UserRepo;
import com.example.learning_assistant.security.config.jwt.JwtService;
import com.example.learning_assistant.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private CloudinaryService cloudinaryService;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, CloudinaryService cloudinaryService){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
    }


    public ResponseEntity<Object> registerUser(String email, String password) {

        if(email == null || email.isEmpty() || !Utils.isValidEmail(email)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please enter valid email");
        }
        if(!Utils.isStrongPassword(password)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please enter strong password");
        }

        email = email.toLowerCase();

        if (userRepo.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User Already Exist");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("user");
        User savedUser = userRepo.save(user);

        if(savedUser == null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error Saving User");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201,"User registered successfully",null));
    }

    public ResponseEntity<Object> loginUser(String email, String password) {

        if(email == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide email");
        }
        if(password == null || password == ""){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide password");
        }

        email = email.toLowerCase();

        User savedUser = userRepo.findByEmail(email);
        if(savedUser == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password");
        }

        Map<String,Object> responseData = new HashMap<>();
        responseData.put("role",savedUser.getRole());
        responseData.put("email",savedUser.getEmail());
        responseData.put("token", jwtService.generateToken(email, "user"));
        responseData.put("id", savedUser.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(),"Login Successful",responseData));

    }

    public ResponseEntity<Object> updateUserProfile(MultipartFile file , UserProfileRequest userProfile)  {

        if((file != null && !userProfile.getProfileImageData().getPublicId().isEmpty()) ||
                (!userProfile.getProfileImageData().getPublicId().isEmpty() && userProfile.getProfileImageData().getUrl().isEmpty())){
            // delete the image from cloud
            try{
                Map deleteFileResult = cloudinaryService.deleteSingleFile(userProfile.getProfileImageData().getPublicId());
                if(deleteFileResult.get("result").equals("ok")){
                    userProfile.setProfileImageData(new ImageData());
                }
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        User presentUser = userRepo.findByEmail(authentication.getName());
        if(presentUser == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        }

        presentUser.setGender(userProfile.getGender());
        presentUser.setFullName(userProfile.getFullName());
        presentUser.setPhoneNo(userProfile.getPhoneNo());

        if( file!= null ){
            Map uploadedFileResult = null;
            try{
                uploadedFileResult = cloudinaryService.uploadSingleFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ImageData profileImageData = new ImageData();
            profileImageData.setUrl(uploadedFileResult.get("secure_url").toString());
            profileImageData.setPublicId(uploadedFileResult.get("public_id").toString());
            presentUser.setProfileImageData(profileImageData);
        }else{
            presentUser.setProfileImageData(ImageData.builder()
                    .url(userProfile.getProfileImageData().getUrl())
                    .publicId(userProfile.getProfileImageData().getPublicId())
                    .build());
        }

        User savedUser = userRepo.save(presentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(),
                        "Profile Updated Successfully",
                        UserProfileResponse.builder()
                                .fullName(savedUser.getFullName())
                                .profileImageData(savedUser.getProfileImageData())
                                .gender(savedUser.getGender())
                                .phoneNo(savedUser.getPhoneNo())
                                .build()
                ));

    }

    public ResponseEntity<Object> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User presentUser = userRepo.findByEmail(email);

        if(presentUser == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(),"successful",UserProfileResponse
                        .builder()
                        .fullName(presentUser.getFullName())
                        .profileImageData(presentUser.getProfileImageData())
                        .gender(presentUser.getGender())
                        .phoneNo(presentUser.getPhoneNo())
                        .email(presentUser.getEmail())
                        .id(presentUser.getId())
                        .build()
                ));
    }
}
