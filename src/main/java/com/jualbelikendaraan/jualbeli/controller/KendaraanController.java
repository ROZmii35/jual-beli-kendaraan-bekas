package com.jualbelikendaraan.jualbeli.controller;

import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import com.jualbelikendaraan.jualbeli.repository.KendaraanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RestMapping("/api/kendaraan")
public class KendaraanController {
    @Autowiredprivate KendaraanRepository kendaraanRepository;

    @GetMapping
    public List<Kendaraan> getAllKendaraan() {
        return kendaraanRepository.findAll();
    }
    @PostMapping
    public Kendaraan createKendaraan(@RequestBody Kendaraan kendaraan) {
        return kendaraanRepository.save(kendaraan);
    }
    
}

