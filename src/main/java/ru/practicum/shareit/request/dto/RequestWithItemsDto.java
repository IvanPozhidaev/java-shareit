package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemInRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RequestWithItemsDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemInRequestDto> items;
}