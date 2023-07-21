package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.validationmarkers.Create;

import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Validated({Create.class})
                                                @RequestBody PostRequestDto postRequestDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.createRequest(postRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = "0")
                                          @Min(0) int from,
                                          @RequestParam(defaultValue = "20")
                                          @Min(1) int size,
                                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.findById(requestId, userId);
    }
}