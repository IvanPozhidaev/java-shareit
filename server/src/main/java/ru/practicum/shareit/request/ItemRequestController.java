package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;

    @PostMapping
    public PostResponseRequestDto createRequest(@RequestBody PostRequestDto postRequestDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return service.createRequest(postRequestDto, userId);
    }

    @GetMapping
    public List<RequestWithItemsDto> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return service.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<RequestWithItemsDto> findAll(@RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return service.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public RequestWithItemsDto findById(@PathVariable Long requestId,
                                        @RequestHeader(USER_ID_HEADER) Long userId) {
        return service.findById(requestId, userId);
    }
}