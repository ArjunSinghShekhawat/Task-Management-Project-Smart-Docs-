package com.arjun.services;

import com.arjun.dto.UserDto;
import com.arjun.enums.ROLE;
import com.arjun.exceptions.UserAlreadyExistsException;
import com.arjun.exceptions.UserException;
import com.arjun.jwt.JwtProvider;
import com.arjun.models.Address;
import com.arjun.models.User;
import com.arjun.repositories.AddressRepository;
import com.arjun.repositories.UserRepository;
import com.arjun.request.LoginRequest;
import com.arjun.request.SignUpRequest;
import com.arjun.responce.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final CustomUserServiceDetails customUserServiceDetails;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AddressRepository addressRepository;
    private final EmailService emailFetchService;

    public AuthResponse signUp(SignUpRequest request) throws UserAlreadyExistsException {

        User existUser = userRepository.findByEmail(request.getEmail());
        if(existUser!=null){
            throw new UserAlreadyExistsException("User already exist with email please try another email !");
        }

        //create new User
        User user = new User();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setRegistrationDate(new Date());
        user.setImageUrl(String.format("https://api.dicebear.com/5.x/initials/svg?seed=%s %s", request.getFirstName(),request.getLastName()));


        //Create address associate for user
        Address address = new Address();
        address=addressRepository.save(address);
        user.setAddress(address);

        //saved user
        userRepository.save(user);

        UserDto savedUser = modelMapper.map(user, UserDto.class);

        // Prepare authorities for security (ensure ROLE is valid and assigned correctly)
        List<GrantedAuthority>authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(request.getRole().toString()));

        // Authenticate the user in the security context (only after the user is saved)
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(),null,authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse();
        response.setStatus(true);
        response.setJwt(jwt);
        response.setMessage("SignUp Successfully");
        response.setRole(savedUser.getRole());

        return response;
    }
    public AuthResponse signIn(LoginRequest request) throws UserException {

        // Retrieve the username and password from the request and trim whitespace
        String username = request.getEmail().trim();
        String password = request.getPassword().trim();

        System.out.println("Email: " + username);

        // Authenticate the user based on the username
        Authentication authentication = authenticate(username);

        // Authenticate the username and password with the authentication manager
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token for the authenticated user
        String token = jwtProvider.generateToken(authentication);

        // Prepare the response object
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        // Retrieve the authorities from the authentication
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Determine the role name based on the authorities
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();
        System.out.println("Role: " + roleName);

        // Set the role for the response object
        setRoleInResponse(roleName, authResponse);

        return authResponse;
    }

    /**
     * Method to authenticate the user based on the username
     */
    private Authentication authenticate(String username) {
        UserDetails userDetails = customUserServiceDetails.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * Helper method to set the role in the AuthResponse.
     * Removes the "ROLE_" prefix if present and sets the role to USER by default if the role is invalid.
     */
    private void setRoleInResponse(String roleName, AuthResponse authResponse) {
        if (roleName != null) {
            // Remove "ROLE_" prefix if present
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5); // Remove "ROLE_"
            }

            try {
                // Set the role in the response
                authResponse.setRole(ROLE.valueOf(roleName));
            } catch (IllegalArgumentException e) {
                // If role is invalid, default to USER
                authResponse.setRole(ROLE.USER);
            }
        } else {
            // Default to USER role if no role is found
            authResponse.setRole(ROLE.USER);
        }
    }
}
