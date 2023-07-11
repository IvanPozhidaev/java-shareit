package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    public static final Sort SORT = Sort.by("created").descending();

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public PostResponseRequestDto createRequest(PostRequestDto dto, Long userId) {
        checkIfUserExists(userId);
        Request request  = RequestMapper.toModel(dto, userId);
        request = requestRepository.save(request);
        return RequestMapper.toPostResponseDto(request);
    }

    @Override
    public List<RequestWithItemsDto> findAllByUserId(Long userId) {
        checkIfUserExists(userId);
        List<Request> requests = requestRepository.findRequestByRequestorOrderByCreatedDesc(userId);
        return RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    @Override
    public List<RequestWithItemsDto> findAll(int from, int size, Long userId) {
        checkIfUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT);
        Page<Request> requests = requestRepository.findAll(userId, pageable);
        return RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    @Override
    public RequestWithItemsDto findById(Long requestId, Long userId) {
        checkIfUserExists(userId);
        Request request = requestRepository.findById(requestId).orElseThrow();
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return RequestMapper.toRequestWithItemsDto(request, items);
    }

    private void checkIfUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow();
    }
}