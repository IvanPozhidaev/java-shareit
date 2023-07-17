package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    PostResponseRequestDto createRequest(PostRequestDto dto, Long userId);

    List<RequestWithItemsDto> findAllByUserId(Long userId);

    List<RequestWithItemsDto> findAll(int from, int size, Long userId);

    RequestWithItemsDto findById(Long requestId, Long userId);
}