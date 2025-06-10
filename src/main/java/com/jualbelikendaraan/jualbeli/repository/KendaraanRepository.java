package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KendaraanRepository extends JpaRepository<Kendaraan, Long> {
    
}