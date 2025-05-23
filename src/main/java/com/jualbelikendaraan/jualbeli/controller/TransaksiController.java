package com.jualbelikendaraan.jualbeli.controller;

import com.jualbelikendaraan.jualbeli.model.Transaksi;
import com.jualbelikendaraan.jualbeli.repository.TransaksiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/kendaraan")
public class TransaksiController {
   @Autowired
    private TransaksiRepository transaksiRepository;

    @GetMapping
    public List<Transaksi> getAllTransaksi() {
        return transaksiRepository.findAll();
    }

    @PostMapping
    public Transaksi createTransaksi(@RequestBody Transaksi transaksi) {
        return transaksiRepository.save(transaksi);
    }

}
