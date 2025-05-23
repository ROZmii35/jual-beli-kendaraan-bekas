package com.jualbelikendaraan.jualbeli.repository;

import com.jualbelikendaraan.jualbeli.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}