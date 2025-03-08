package com.arjun.services;

import com.arjun.dto.UserDto;
import com.arjun.exceptions.UserException;
import com.arjun.jwt.JwtProvider;
import com.arjun.models.User;
import com.arjun.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    public UserDto updateUser(String jwt,UserDto userDto) throws UserException {
        //fetch user
        User existUser = fetchUserFromJwtByEmail(jwt);

        if(existUser==null){
            throw new UserException("User not found !");
        }

        //set first name of the user
        if(userDto.getFirstName()!=null){
            existUser.setFirstName(userDto.getFirstName());
        }

        //set last name of the user
        if(userDto.getLastName()!=null){
            existUser.setLastName(userDto.getLastName());
        }

        //set email of the user
        if(userDto.getEmail()!=null){
            existUser.setEmail(userDto.getEmail());
        }

        //set address of the user
        if(userDto.getAddress()!=null && userDto.getAddress().getCity()!=null
                && userDto.getAddress().getCountry()!=null && userDto.getAddress().getStreet()!=null
                && userDto.getAddress().getPinCode()!=null && userDto.getAddress().getState()!=null){

                    existUser.getAddress().setStreet(userDto.getAddress().getStreet());
                    existUser.getAddress().setCity(userDto.getAddress().getCity());
                    existUser.getAddress().setState(userDto.getAddress().getState());
                    existUser.getAddress().setPinCode(userDto.getAddress().getPinCode());
                    existUser.getAddress().setCountry(userDto.getAddress().getPinCode());

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
}
