package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<Request, Long> {

    List<Request> findRequestByRequestorIdOrderByCreatedDesc(Long requestor);

    @Query("select r from requests r where r.requestor.id <> ?1")
    Page<Request> findAll(Long userId, Pageable pageable);
}