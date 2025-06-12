package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Message;
import com.jualbelikendaraan.jualbeli.model.User;
import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Temukan pesan antara dua pengguna untuk kendaraan tertentu
    List<Message> findBySenderAndReceiverAndVehicleOrderByTimestampAsc(User sender, User receiver, Kendaraan vehicle);
    List<Message> findByReceiverAndSenderAndVehicleOrderByTimestampAsc(User receiver, User sender, Kendaraan vehicle);

    // Temukan semua pesan yang melibatkan pengguna tertentu (sebagai pengirim atau penerima)
    List<Message> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
} 