package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Null(groups = MarkerUserDto.OnCreate.class)
    private Long id;

    @NotBlank(groups = MarkerUserDto.OnCreate.class)
    private String name;

    @NotBlank(groups = MarkerUserDto.OnCreate.class)
    @Email(groups = MarkerUserDto.OnCreate.class, message = "Неправильный формат электронной почты")
    @Email(groups = MarkerUserDto.OnUpdate.class, message = "Неправильный формат электронной почты")
    private String email;
}
