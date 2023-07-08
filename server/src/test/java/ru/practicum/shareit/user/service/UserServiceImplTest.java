package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private final User testUser = mock(User.class);
    private final UserDto testUserDto = mock(UserDto.class);

    private MockedStatic<UserMapper> userMapperMockedStatic;

    @BeforeEach
    void setUserService() {
        userService = new UserServiceImpl(userRepository);
        userMapperMockedStatic = Mockito.mockStatic(UserMapper.class);
        when(UserMapper.toUser(testUser, testUserDto)).thenReturn(testUser);
        when(UserMapper.toUser(testUserDto)).thenReturn(testUser);
        when(UserMapper.toUserDto(testUser)).thenReturn(testUserDto);
    }

    @AfterEach
    void closeUserService() {
       userMapperMockedStatic.close();
    }

    @Test
    void getAllTest() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        List<UserDto> userDtoList = userService.getAll();

        verify(userRepository, times(1)).findAll();
        assertEquals(userDtoList.size(), 1);
    }

    @Test
    void getUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        UserDto userDto = userService.getUser(10L);

        verify(userRepository, times(1)).findById(10L);
        assertEquals(userDto, testUserDto);
    }

    @Test
    void createTest() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        UserDto userDto = userService.create(testUserDto);

        verify(userRepository, times(1)).save(testUser);
        assertEquals(userDto, testUserDto);
    }

    @Test
    void updateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        UserDto userDto = userService.update(10L, testUserDto);

        verify(userRepository, times(1)).save(testUser);
        assertEquals(userDto, testUserDto);
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);
        userService.deleteUser(anyLong());
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void findByIdInvalidUserIdThenThrowsUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(10L));
    }

    @Test
    void findByIdValidUserIdThenReturnedUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        User user = userService.findById(10L);
        assertEquals(user, testUser);
    }

}