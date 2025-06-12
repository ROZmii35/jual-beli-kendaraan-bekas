package com.jualbelikendaraan.jualbeli.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import com.jualbelikendaraan.jualbeli.repository.KendaraanRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kendaraan")
public class KendaraanController {
    @Autowired private KendaraanRepository kendaraanRepository;
    @GetMapping
    public List<Kendaraan> getAllKendaraan() {
        return kendaraanRepository.findAll();
    }
    @PostMapping
    public Kendaraan createKendaraan(@RequestBody Kendaraan kendaraan) {
        return kendaraanRepository.save(kendaraan);
    }
}
