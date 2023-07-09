package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
