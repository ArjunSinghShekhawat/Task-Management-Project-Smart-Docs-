package com.arjun.services;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserException;
import com.arjun.jwt.JwtProvider;
import com.arjun.models.Address;
import com.arjun.models.User;
import com.arjun.repositories.AddressRepository;
import com.arjun.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.bson.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;
    private final AddressRepository addressRepository;


    public UserDto updateUser(String jwt,UserDto userDto) throws UserException {
        //fetch user
        User existUser = fetchUserFromJwtByEmail(jwt);

        if(existUser==null){
            throw new UserException("User not found !");
        }

        //set first name of the user
        if(userDto.getFirstName()!=null && !userDto.getFirstName().isBlank()){
            existUser.setFirstName(userDto.getFirstName());
        }

        //set last name of the user
        if(userDto.getLastName()!=null && !userDto.getLastName().isBlank()){
            existUser.setLastName(userDto.getLastName());
        }

        //set email of the user
        if(userDto.getEmail()!=null && !userDto.getEmail().isBlank()){
            existUser.setEmail(userDto.getEmail());
        }

        //set address of the user
        if (userDto.getAddress() != null && userDto.getAddress().getCity() != null
                && userDto.getAddress().getCountry() != null && userDto.getAddress().getStreet() != null
                && userDto.getAddress().getPinCode() != null && userDto.getAddress().getState() != null) {

            Address address = existUser.getAddress(); // Get existing address

            if (address == null) {
                address = new Address(); // Create a new address if it doesn't exist
                existUser.setAddress(address);
            }

            address.setStreet(userDto.getAddress().getStreet());
            address.setCity(userDto.getAddress().getCity());
            address.setState(userDto.getAddress().getState());
            address.setPinCode(userDto.getAddress().getPinCode());
            address.setCountry(userDto.getAddress().getCountry());

            // Explicitly save address if not using CascadeType.ALL
            addressRepository.save(address);
        }
        //save updated user
        User updatedUser = userRepository.save(existUser);

        //return update user dto
        return modelMapper.map(updatedUser,UserDto.class);
    }
    public boolean deleteUser(String jwt) throws UserException {

        //fetch user
        User user = fetchUserFromJwtByEmail(jwt);

        if(user!=null){
            userRepository.deleteById(user.getId());
            return true;
        }
        return false;
    }
    public UserDto getUser(String jwt) throws UserException {
        //fetch user
        User user = fetchUserFromJwtByEmail(jwt);
        return modelMapper.map(user,UserDto.class);
    }
    public Page<UserDto> getAllUsers(Integer page, Integer size) {
        if (page == null || size == null) {
            page = 0;
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> all = userRepository.findAll(pageable);

        // Mapping the Page<User> to a Page<UserDto>
        return all.map(user -> modelMapper.map(user, UserDto.class));
    }
    private User fetchUserFromJwtByEmail(String jwt) throws UserException {
        //fetch email from jwt
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);

        if(user==null){
            throw new UserException("User not found with email "+email);
        }
        return userRepository.findByEmail(email);
    }


    public ByteArrayInputStream exportUsersToExcel() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))  // ✅ Corrected mapping
                .toList();
        return ExcelHelper.usersToExcel(users);
    }
    public void importUsersFromExcel(MultipartFile file) {
        List<UserDto> users = ExcelHelper.excelToUsers(file); // ✅ Convert Excel to List<UserDto>

        for (UserDto userDto : users) {
            User user = modelMapper.map(userDto, User.class); // ✅ Convert DTO to Entity

            // ✅ Validate Email Before Saving
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                if(userRepository.findByEmail(user.getEmail())==null){
                    Address savedAddress = addressRepository.save(user.getAddress()); // Save new address
                    user.setAddress(savedAddress); // Link saved address to user
                    userRepository.save(user);
                }
            } else {
                System.out.println("Skipping user with missing email: " + userDto);
            }
        }
    }
}
