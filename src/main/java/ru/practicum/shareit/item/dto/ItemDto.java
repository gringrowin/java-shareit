package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    @Null(groups = MarkerItemDto.OnCreate.class)
    private Long id;

    @NotNull(groups = MarkerItemDto.OnCreate.class)
    @NotBlank(groups = MarkerItemDto.OnCreate.class)
    private String name;

    @NotNull(groups = MarkerItemDto.OnCreate.class)
    @NotBlank(groups = MarkerItemDto.OnCreate.class)
    private String description;

    @NotNull(groups = MarkerItemDto.OnCreate.class)
    private Boolean available;

    @JsonIgnore
    private Long request;

}
