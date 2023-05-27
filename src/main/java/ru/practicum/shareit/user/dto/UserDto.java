package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Null(groups = MarkerUserDto.onCreate.class)
    private Long id;
    @NotNull(groups = MarkerUserDto.onCreate.class)
    private String name;
    @NotNull(groups = MarkerUserDto.onCreate.class)
    @Email(groups = MarkerUserDto.onCreate.class, message = "Неправильный формат электронной почты")
    @Email(groups = MarkerUserDto.onUpdate.class, message = "Неправильный формат электронной почты")
    private String email;
}
