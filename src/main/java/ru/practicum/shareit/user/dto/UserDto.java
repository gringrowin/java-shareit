package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Неправильный формат электронной почты")
    private String email;
}
