package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterOrderByCreatedDesc(User user);

    List<ItemRequest> findByRequesterNotInOrderByCreatedDesc(List<User> user, Pageable page);

    List<ItemRequest> findByRequesterNotIn(List<User> user, PageRequest pageRequest);
}
