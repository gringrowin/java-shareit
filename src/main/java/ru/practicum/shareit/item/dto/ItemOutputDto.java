package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comments.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemOutputDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemResponseBookingDto lastBooking;

    private ItemResponseBookingDto nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}
