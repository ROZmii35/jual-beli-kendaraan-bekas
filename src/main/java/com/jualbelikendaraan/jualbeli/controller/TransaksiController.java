package com.jualbelikendaraan.jualbeli.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.jualbelikendaraan.jualbeli.model.Transaksi;
import com.jualbelikendaraan.jualbeli.repository.TransaksiRepository;
import java.util.List;

@RestController
@RequestMapping("/api/transaksi")
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
    @PutMapping("/{id}")
    public Transaksi updateTransaksi(@PathVariable Long id, @RequestBody Transaksi updatedTransaksi) {
        return transaksiRepository.findById(id).map(t -> {
            t.setTanggal(updatedTransaksi.getTanggal());
            t.setStatus(updatedTransaksi.getStatus());
            return transaksiRepository.save(t);
        }).orElseThrow();
    }
    @DeleteMapping("/{id}")
    public void deleteTransaksi(@PathVariable Long id) {
        transaksiRepository.deleteById(id);
    }
}
