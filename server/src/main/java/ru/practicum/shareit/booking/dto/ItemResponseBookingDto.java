package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseBookingDto {

    private Long id;

    private Long bookerId;

    private BookingState status;

}

