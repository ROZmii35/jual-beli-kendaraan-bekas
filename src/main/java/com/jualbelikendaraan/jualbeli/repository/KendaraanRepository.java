package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import com.jualbelikendaraan.jualbeli.model.VehicleStatus;
import com.jualbelikendaraan.jualbeli.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KendaraanRepository extends JpaRepository<Kendaraan, Long> {
    List<Kendaraan> findByStatus(VehicleStatus status);
    List<Kendaraan> findByStatusIn(List<VehicleStatus> statuses);
    List<Kendaraan> findBySeller(User seller);
}