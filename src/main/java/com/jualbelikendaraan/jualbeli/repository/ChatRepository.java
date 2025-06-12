package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.jualbelikendaraan.jualbeli.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderAndReceiverOrderByTimestampAsc(User sender, User receiver);
    List<Chat> findBySenderOrReceiverOrderByTimestampAsc(User sender, User receiver);
} 