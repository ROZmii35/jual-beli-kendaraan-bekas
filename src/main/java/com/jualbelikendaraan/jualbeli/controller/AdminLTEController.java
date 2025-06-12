package com.jualbelikendaraan.jualbeli.controller;

import com.jualbelikendaraan.jualbeli.model.Transaksi;
import com.jualbelikendaraan.jualbeli.model.User;
import com.jualbelikendaraan.jualbeli.repository.TransaksiRepository;
import com.jualbelikendaraan.jualbeli.repository.UserRepository;
import com.jualbelikendaraan.jualbeli.repository.KendaraanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminLTEController {

    @Autowired
    private TransaksiRepository transaksiRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KendaraanRepository kendaraanRepository;

    @ModelAttribute(name = "_csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }

    // --- Transaksi CRUD --- //

    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        List<Transaksi> transactions = transaksiRepository.findAll();
        model.addAttribute("transactions", transactions);
        return "admin/transactions";
    }

    @GetMapping("/transactions/add")
    public String addTransactionForm(Model model) {
        model.addAttribute("transaksi", new Transaksi());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("kendaraans", kendaraanRepository.findAll());
        return "admin/transaction-form";
    }

    @PostMapping("/transactions")
    public String saveTransaction(@ModelAttribute("transaksi") Transaksi transaksi, RedirectAttributes redirectAttributes) {
        transaksiRepository.save(transaksi);
        redirectAttributes.addFlashAttribute("message", "Transaksi berhasil disimpan!");
        return "redirect:/admin/transactions";
    }

    @GetMapping("/transactions/edit/{id}")
    public String editTransactionForm(@PathVariable Long id, Model model) {
        Transaksi transaksi = transaksiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction Id:" + id));
        model.addAttribute("transaksi", transaksi);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("kendaraans", kendaraanRepository.findAll());
        return "admin/transaction-form";
    }

    @PostMapping("/transactions/edit/{id}")
    public String updateTransaction(@PathVariable Long id, @ModelAttribute("transaksi") Transaksi transaksi, RedirectAttributes redirectAttributes) {
        transaksi.setId(id); // Pastikan ID diset untuk update
        transaksiRepository.save(transaksi);
        redirectAttributes.addFlashAttribute("message", "Transaksi berhasil diperbarui!");
        return "redirect:/admin/transactions";
    }

    @GetMapping("/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transaksiRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Transaksi berhasil dihapus!");
        return "redirect:/admin/transactions";
    }

    // --- User CRUD --- //

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Pengguna berhasil disimpan!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        user.setId(id); // Pastikan ID diset untuk update
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Pengguna berhasil diperbarui!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Pengguna berhasil dihapus!");
        return "redirect:/admin/users";
    }
} 