package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Transaksi;
import com.jualbelikendaraan.jualbeli.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {
    List<Transaksi> findByUser(User user);
}