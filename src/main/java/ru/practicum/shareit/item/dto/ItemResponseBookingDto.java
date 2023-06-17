package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingState;

@Data
@Builder
public class ItemResponseBookingDto {

    private Long id;

    private Long bookerId;

    private BookingState status;

}

