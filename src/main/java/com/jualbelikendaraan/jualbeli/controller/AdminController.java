package com.jualbelikendaraan.jualbeli.controller;

import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import com.jualbelikendaraan.jualbeli.model.VehicleStatus;
import com.jualbelikendaraan.jualbeli.repository.KendaraanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import com.jualbelikendaraan.jualbeli.model.DefaultKendaraan;
import com.jualbelikendaraan.jualbeli.model.Mobil;
import com.jualbelikendaraan.jualbeli.model.Motor;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Memastikan hanya ADMIN yang bisa mengakses controller ini
public class AdminController {

    @Autowired
    private KendaraanRepository kendaraanRepository;

    @GetMapping("/kendaraan/crud")
    public String listAllVehiclesForCrud(Model model) {
        List<Kendaraan> allVehicles = kendaraanRepository.findAll(); // Mengambil semua kendaraan
        model.addAttribute("allVehicles", allVehicles);
        return "admin-crud-vehicles"; // Akan diubah namanya dari admin-vehicles
    }

    @GetMapping("/kendaraan/pending")
    public String listPendingAndApprovedVehicles(Model model) {
        List<Kendaraan> pendingVehicles = kendaraanRepository.findByStatus(VehicleStatus.BELUM_DIPENDING);
        List<Kendaraan> approvedVehicles = kendaraanRepository.findByStatus(VehicleStatus.SUDAH_DIPENDING);

        model.addAttribute("pendingVehicles", pendingVehicles);
        model.addAttribute("approvedVehicles", approvedVehicles);
        return "admin-pending-vehicles"; // File baru untuk pending
    }

    @GetMapping("/kendaraans/add")
    public String addKendaraanForm(Model model) {
        model.addAttribute("kendaraan", new DefaultKendaraan()); // Gunakan DefaultKendaraan untuk form binding
        return "admin-add-vehicle";
    }

    @PostMapping("/kendaraans/save")
    public String saveKendaraan(@ModelAttribute("kendaraan") DefaultKendaraan kendaraan, @RequestParam("jenisKendaraan") String jenisKendaraan, RedirectAttributes redirectAttributes) {
        Kendaraan kendaraanToSave;
        if ("Mobil".equals(jenisKendaraan)) {
            Mobil mobil = new Mobil();
            mobil.setMerk(kendaraan.getMerk());
            mobil.setTipe(kendaraan.getTipe());
            mobil.setTahun(kendaraan.getTahun());
            mobil.setHargaAsli(kendaraan.getHargaAsli());
            mobil.setNamaKendaraan(kendaraan.getNamaKendaraan());
            mobil.setDeskripsi(kendaraan.getDeskripsi());
            mobil.setImageUrl(kendaraan.getImageUrl());
            mobil.setStatus(kendaraan.getStatus()); // Set status dari form
            mobil.setSeller(kendaraan.getSeller()); // Pastikan seller diset jika diperlukan, atau tangani null
            kendaraanToSave = mobil;
        } else if ("Motor".equals(jenisKendaraan)) {
            Motor motor = new Motor();
            motor.setMerk(kendaraan.getMerk());
            motor.setTipe(kendaraan.getTipe());
            motor.setTahun(kendaraan.getTahun());
            motor.setHargaAsli(kendaraan.getHargaAsli());
            motor.setNamaKendaraan(kendaraan.getNamaKendaraan());
            motor.setDeskripsi(kendaraan.getDeskripsi());
            motor.setImageUrl(kendaraan.getImageUrl());
            motor.setStatus(kendaraan.getStatus()); // Set status dari form
            motor.setSeller(kendaraan.getSeller()); // Pastikan seller diset jika diperlukan, atau tangani null
            kendaraanToSave = motor;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Jenis kendaraan tidak valid!");
            return "redirect:/admin/kendaraan/crud"; // Redirect ke halaman CRUD
        }

        kendaraanRepository.save(kendaraanToSave);
        redirectAttributes.addFlashAttribute("successMessage", "Kendaraan berhasil disimpan!");
        return "redirect:/admin/kendaraan/crud"; // Redirect ke halaman CRUD
    }

    @GetMapping("/kendaraans/edit/{id}")
    public String editVehicle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Kendaraan> optionalKendaraan = kendaraanRepository.findById(id);
        if (optionalKendaraan.isPresent()) {
            model.addAttribute("kendaraan", optionalKendaraan.get());
            return "admin-edit-vehicle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan tidak ditemukan.");
            return "redirect:/admin/kendaraan/crud"; // Redirect ke halaman CRUD
        }
    }

    @PostMapping("/kendaraans/update")
    public String updateVehicle(@ModelAttribute("kendaraan") DefaultKendaraan kendaraan, @RequestParam("jenisKendaraan") String jenisKendaraan, RedirectAttributes redirectAttributes) {
        Optional<Kendaraan> optionalExistingKendaraan = kendaraanRepository.findById(kendaraan.getId());
        if (optionalExistingKendaraan.isPresent()) {
            Kendaraan existingKendaraan = optionalExistingKendaraan.get();

            // Jika jenis kendaraan berubah, hapus yang lama dan buat yang baru
            if (!existingKendaraan.getJenis().equals(jenisKendaraan)) {
                kendaraanRepository.delete(existingKendaraan);
                redirectAttributes.addFlashAttribute("errorMessage", "Perubahan jenis kendaraan tidak didukung.");
                return "redirect:/admin/kendaraans/edit/" + kendaraan.getId();
            }

            // Update fields manually
            existingKendaraan.setMerk(kendaraan.getMerk());
            existingKendaraan.setTipe(kendaraan.getTipe());
            existingKendaraan.setTahun(kendaraan.getTahun());
            existingKendaraan.setHargaAsli(kendaraan.getHargaAsli());
            existingKendaraan.setNamaKendaraan(kendaraan.getNamaKendaraan());
            existingKendaraan.setDeskripsi(kendaraan.getDeskripsi());
            existingKendaraan.setImageUrl(kendaraan.getImageUrl());
            existingKendaraan.setStatus(kendaraan.getStatus());

            kendaraanRepository.save(existingKendaraan);
            redirectAttributes.addFlashAttribute("successMessage", "Kendaraan berhasil diperbarui.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan tidak ditemukan.");
        }
        return "redirect:/admin/kendaraan/crud"; // Redirect ke halaman CRUD
    }

    @PostMapping("/kendaraans/delete")
    public String deleteVehicle(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (kendaraanRepository.existsById(id)) {
            kendaraanRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kendaraan berhasil dihapus.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan tidak ditemukan.");
        }
        return "redirect:/admin/kendaraan/crud"; // Redirect ke halaman CRUD
    }

    @PostMapping("/kendaraans/approve")
    public String approveVehicle(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Kendaraan> optionalKendaraan = kendaraanRepository.findById(id);
        if (optionalKendaraan.isPresent()) {
            Kendaraan kendaraan = optionalKendaraan.get();
            kendaraan.setStatus(VehicleStatus.SUDAH_DIPENDING);
            kendaraanRepository.save(kendaraan);
            redirectAttributes.addFlashAttribute("successMessage", "Status kendaraan berhasil diubah menjadi SUDAH DIPENDING.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan tidak ditemukan.");
        }
        return "redirect:/admin/kendaraan/pending"; // Redirect ke halaman pending
    }
} 