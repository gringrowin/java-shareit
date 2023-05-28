package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageInMemory implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private Long idGenerator = 0L;



    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        final String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new DuplicateEmailException(String.format("Такой email: %s уже существует", email));
        }
        user.setId(getNewId());
        users.put(user.getId(), user);
        emailUniqSet.add(email);
        return user;
    }

    @Override
    public User update(Long id, UserDto userDto) {
        final String email = userDto.getEmail();
        User user = users.get(id);
        if (emailUniqSet.contains(email) && !user.getEmail().equals(email)) {
            throw new DuplicateEmailException(String.format("Такой email: %s уже существует", email));
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null
                && !userDto.getEmail().equals(user.getEmail())) {
            emailUniqSet.remove(user.getEmail());
            user.setEmail(userDto.getEmail());
            emailUniqSet.add(user.getEmail());
        }
        users.put(user.getId(), user);

        return users.get(user.getId());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Long userId) {
        emailUniqSet.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    private Long getNewId() {
        return ++idGenerator;
    }
}
