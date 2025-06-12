package com.jualbelikendaraan.jualbeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import com.jualbelikendaraan.jualbeli.repository.KendaraanRepository;
import com.jualbelikendaraan.jualbeli.repository.UserRepository;
import com.jualbelikendaraan.jualbeli.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.jualbelikendaraan.jualbeli.model.Orang;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.web.csrf.CsrfToken;
import com.jualbelikendaraan.jualbeli.repository.TransaksiRepository;
import com.jualbelikendaraan.jualbeli.model.Kendaraan;
import com.jualbelikendaraan.jualbeli.model.VehicleStatus;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import com.jualbelikendaraan.jualbeli.repository.MessageRepository;
import com.jualbelikendaraan.jualbeli.model.Message;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import com.jualbelikendaraan.jualbeli.model.DefaultKendaraan;
import com.jualbelikendaraan.jualbeli.model.JenisKendaraan;
import com.jualbelikendaraan.jualbeli.model.Transaksi;
import java.time.LocalDate;
import java.math.BigDecimal;

@Controller
public class PageController {
    @Autowired
    private KendaraanRepository kendaraanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Helper class to uniquely identify a conversation
    public static class ConversationIdentifier {
        private Kendaraan kendaraan;
        private User otherUser;

        public ConversationIdentifier(Kendaraan kendaraan, User otherUser) {
            this.kendaraan = kendaraan;
            this.otherUser = otherUser;
        }

        public Kendaraan getKendaraan() {
            return kendaraan;
        }

        public User getOtherUser() {
            return otherUser;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConversationIdentifier that = (ConversationIdentifier) o;
            return kendaraan.equals(that.kendaraan) && otherUser.equals(that.otherUser);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kendaraan, otherUser);
        }
    }

    @ModelAttribute(name = "_csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping("/")
    public String dashboard(Model model, CsrfToken csrfToken) {
        List<Kendaraan> activeVehicles = kendaraanRepository.findByStatusIn(Arrays.asList(VehicleStatus.BELUM_DIPENDING, VehicleStatus.SUDAH_DIPENDING));
        model.addAttribute("kendaraanList", activeVehicles);

        // Dapatkan user yang sedang login dan tampilkan kendaraan mereka
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loggedInUserId = null; // Inisialisasi dengan null

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails) {
            com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
            Orang currentUserOrang = customUserDetails.getOrang();
            if (currentUserOrang instanceof User) {
                User currentUser = (User) currentUserOrang;
                List<Kendaraan> myVehicles = kendaraanRepository.findBySeller(currentUser);
                model.addAttribute("myKendaraanList", myVehicles);
                loggedInUserId = currentUser.getId(); // Set ID jika user login
            }
        }
        model.addAttribute("loggedInUserId", loggedInUserId); // Selalu tambahkan ke model
        model.addAttribute("_csrf", csrfToken);
        return "index";
    }

    @GetMapping("/user-login")
    public String userLogin(@RequestParam(name = "error", required = false) String error, Model model, CsrfToken csrfToken) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        model.addAttribute("_csrf", csrfToken);
        return "UserLogin";
    }

    @GetMapping("/admin-login")
    public String adminLogin(@RequestParam(name = "error", required = false) String error, Model model, CsrfToken csrfToken) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        model.addAttribute("_csrf", csrfToken);
        return "AdminLogin";
    }

    @GetMapping("/register")
    public String registerUser() {
        return "Register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username, @RequestParam String password, @RequestParam String email, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("registrationError", "Username sudah terdaftar!");
            return "redirect:/register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Password disimpan plain text karena NoOpPasswordEncoder
        newUser.setEmail(email);
        newUser.setRole("USER");

        userRepository.save(newUser);

        try {
            // Lakukan autentikasi menggunakan AuthenticationManager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticated = authenticationManager.authenticate(authToken);

            // Set autentikasi di SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            // Simpan SecurityContext ke sesi secara manual
            SecurityContext securityContext = SecurityContextHolder.getContext();
            HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
            httpSessionSecurityContextRepository.saveContext(securityContext, request, response);

            redirectAttributes.addFlashAttribute("registrationSuccess", "Pendaftaran berhasil! Anda otomatis login.");
            return "redirect:/"; // Redirect ke halaman utama
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registrationError", "Terjadi kesalahan saat login otomatis: " + e.getMessage());
            return "redirect:/user-login"; // Kembali ke login jika auto-login gagal
        }
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails) {
            com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
            Orang currentUser = customUserDetails.getOrang();
            model.addAttribute("user", currentUser);
            return "UserProfile";
        } else {
            return "redirect:/user-login";
        }
    }

    @GetMapping("/AdminDashboard")
    public String adminDashboard() {
        return "AdminDashboard";
    }

    @GetMapping("/my-account")
    public String myAccount() {
        return "my-account";
    }

    @GetMapping("/profil")
    public String profil() {
        return "profil";
    }

    @GetMapping("/transaksi")
    public String transaksi() {
        return "transaksi";
    }

    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails) {
            com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
            Orang currentUser = customUserDetails.getOrang();
            if (currentUser instanceof User) {
                User user = (User) currentUser;
                model.addAttribute("transactions", transaksiRepository.findByBuyerOrSeller(user, user));
            } else {
                return "redirect:/user-login";
            }
            return "transactions";
        } else {
            return "redirect:/user-login";
        }
    }

    @GetMapping("/my-vehicles")
    public String myVehicles(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            return "redirect:/user-login"; // Redirect to login if not authenticated
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        Orang currentUserOrang = customUserDetails.getOrang();

        if (currentUserOrang instanceof User) {
            User currentUser = (User) currentUserOrang;
            List<Kendaraan> myVehicles = kendaraanRepository.findBySeller(currentUser);
            model.addAttribute("myKendaraanList", myVehicles);
        } else {
            return "redirect:/user-login"; // Should not happen if authenticated as USER
        }

        return "my-vehicles";
    }

    @GetMapping("/sell")
    public String sellVehicle(Model model) {
        model.addAttribute("kendaraan", new DefaultKendaraan());
        model.addAttribute("jenisKendaraanOptions", JenisKendaraan.values());
        return "sell-vehicle";
    }

    @PostMapping("/sell")
    public String processSellVehicle(@ModelAttribute DefaultKendaraan kendaraan, @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            // Dapatkan user yang sedang login
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails) {
                com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
                if (customUserDetails.getOrang() instanceof User) {
                    currentUser = (User) customUserDetails.getOrang();
                }
            }

            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk menjual kendaraan.");
                return "redirect:/user-login";
            }

            if (imageFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Harap pilih file gambar untuk diunggah.");
                return "redirect:/sell";
            }

            String uploadDir = "src/main/resources/static/uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(imageFile.getInputStream(), filePath);

            // Simpan path relatif ke database
            String relativePath = "/uploads/" + uniqueFileName;
            kendaraan.setImageUrl(relativePath);

            kendaraan.setStatus(VehicleStatus.BELUM_DIPENDING);
            kendaraan.setSeller(currentUser); // Set seller kendaraan
            kendaraanRepository.save(kendaraan);
            redirectAttributes.addFlashAttribute("successMessage", "Kendaraan berhasil diajukan untuk dijual! Menunggu konfirmasi admin.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat mengunggah gambar: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat mengajukan kendaraan: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/sell";
    }

    @GetMapping("/vehicle/{id}")
    public String viewVehicleDetail(@PathVariable Long id, Model model) {
        kendaraanRepository.findById(id).ifPresent(kendaraan -> {
            model.addAttribute("kendaraan", kendaraan);

            // Dapatkan ID pengguna yang sedang login
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails) {
                com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
                if (customUserDetails.getOrang() instanceof User) {
                    User currentUser = (User) customUserDetails.getOrang();
                    model.addAttribute("loggedInUserId", currentUser.getId());
                    model.addAttribute("currentUser", currentUser); // Tambahkan currentUser ke model
                }
            }
        });
        return "vehicle-detail";
    }

    @GetMapping("/vehicle/{id}/buy")
    public String buyVehicleForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk membeli kendaraan.");
            return "redirect:/user-login";
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User currentUser = (User) customUserDetails.getOrang();
        model.addAttribute("currentUser", currentUser); // Tambahkan currentUser ke model

        Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(id);
        if (!kendaraanOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan tidak ditemukan.");
            return "redirect:/"; // Kembali ke dashboard
        }

        Kendaraan kendaraan = kendaraanOptional.get();

        // Validasi: pastikan pembeli bukan pemilik kendaraan
        if (kendaraan.getSeller() != null && kendaraan.getSeller().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak dapat membeli kendaraan Anda sendiri.");
            return "redirect:/vehicle/" + id; // Kembali ke detail kendaraan
        }

        model.addAttribute("kendaraan", kendaraan);
        return "buy-vehicle";
    }

    @GetMapping("/chat/{vehicleId}/{targetUserId}")
    public String viewChatInterface(@PathVariable Long vehicleId, @PathVariable Long targetUserId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            return "redirect:/user-login"; // Arahkan ke login jika tidak terautentikasi
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User currentUser = (User) customUserDetails.getOrang(); // Dapatkan User yang sedang login
        model.addAttribute("currentUser", currentUser); // Tambahkan currentUser ke model

        Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(vehicleId);
        Optional<User> targetUserOptional = userRepository.findById(targetUserId);

        if (!kendaraanOptional.isPresent() || !targetUserOptional.isPresent()) {
            return "redirect:/"; // Kendaraan atau penerima tidak ditemukan, kembali ke dashboard
        }
        Kendaraan kendaraan = kendaraanOptional.get();
        User targetUser = targetUserOptional.get();

        // Dapatkan penjual kendaraan
        User sellerOfVehicle = kendaraan.getSeller();

        // Validasi: Pastikan pengguna yang login (currentUser) dan pengguna target (targetUser)
        // adalah dua pihak yang terlibat dalam percakapan yang valid (yaitu, salah satunya adalah penjual kendaraan).
        // Dan juga pastikan pengguna yang login tidak sama dengan pengguna target.
        if (currentUser.getId().equals(targetUser.getId()) || // Mencegah chat dengan diri sendiri
            !(
                (currentUser.getId().equals(sellerOfVehicle.getId()) && !targetUser.getId().equals(sellerOfVehicle.getId())) || // currentUser adalah penjual, targetUser adalah pembeli
                (!currentUser.getId().equals(sellerOfVehicle.getId()) && targetUser.getId().equals(sellerOfVehicle.getId()))    // currentUser adalah pembeli, targetUser adalah penjual
            )) {
            return "redirect:/"; // Redirect jika akses tidak sah
        }

        // Ambil riwayat pesan antara kedua pengguna untuk kendaraan ini
        List<Message> chatMessages = messageRepository.findBySenderAndReceiverAndVehicleOrderByTimestampAsc(currentUser, targetUser, kendaraan);
        chatMessages.addAll(messageRepository.findByReceiverAndSenderAndVehicleOrderByTimestampAsc(currentUser, targetUser, kendaraan));
        chatMessages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

        model.addAttribute("kendaraan", kendaraan);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chatMessages", chatMessages);

        return "chat-interface";
    }

    @PostMapping("/chat/send")
    public String sendMessage(@RequestParam Long vehicleId, @RequestParam Long receiverId, @RequestParam String messageContent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            return "redirect:/user-login";
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User sender = (User) customUserDetails.getOrang();

        Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(vehicleId);
        Optional<User> receiverOptional = userRepository.findById(receiverId);

        if (!kendaraanOptional.isPresent() || !receiverOptional.isPresent()) {
            return "redirect:/vehicle/" + vehicleId;
        }

        Kendaraan kendaraan = kendaraanOptional.get();
        User receiver = receiverOptional.get();

        Message message = new Message(sender, receiver, kendaraan, messageContent);
        messageRepository.save(message);

        return "redirect:/chat/" + vehicleId + "/" + receiver.getId();
    }

    @PostMapping("/send-message")
    public String sendMessageFromDetail(@RequestParam Long vehicleId, @RequestParam Long receiverId, @RequestParam String messageContent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            return "redirect:/user-login";
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User sender = (User) customUserDetails.getOrang();

        Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(vehicleId);
        Optional<User> receiverOptional = userRepository.findById(receiverId);

        if (!kendaraanOptional.isPresent() || !receiverOptional.isPresent()) {
            return "redirect:/vehicle/" + vehicleId;
        }

        Kendaraan kendaraan = kendaraanOptional.get();
        User receiver = receiverOptional.get();

        // Validasi: pastikan pengirim dan penerima tidak sama
        if (sender.getId().equals(receiver.getId())) {
            return "redirect:/vehicle/" + vehicleId;
        }

        Message message = new Message(sender, receiver, kendaraan, messageContent);
        messageRepository.save(message);

        return "redirect:/vehicle/" + vehicleId;
    }

    @PostMapping("/process-purchase")
    public String processPurchase(@RequestParam Long vehicleId,
                                  @RequestParam Long sellerId,
                                  @RequestParam BigDecimal hargaAkhir,
                                  @RequestParam String metodePembayaran,
                                  @RequestParam(required = false) String catatanTambahan,
                                  RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk melakukan pembelian.");
                return "redirect:/user-login";
            }

            com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
            User buyer = (User) customUserDetails.getOrang();

            Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(vehicleId);
            Optional<User> sellerOptional = userRepository.findById(sellerId);

            if (!kendaraanOptional.isPresent() || !sellerOptional.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan atau penjual tidak valid.");
                return "redirect:/";
            }

            Kendaraan kendaraan = kendaraanOptional.get();
            User seller = sellerOptional.get();

            // Validasi: pastikan pembeli bukan pemilik kendaraan
            if (kendaraan.getSeller() != null && kendaraan.getSeller().getId().equals(buyer.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak dapat membeli kendaraan Anda sendiri.");
                return "redirect:/vehicle/" + vehicleId; // Kembali ke detail kendaraan
            }

            // Buat objek Transaksi baru
            Transaksi transaksi = new Transaksi(
                buyer,
                seller,
                kendaraan,
                LocalDate.now(), // Tanggal transaksi otomatis
                hargaAkhir,
                "SELESAI", // Status default untuk transaksi yang berhasil
                metodePembayaran,
                catatanTambahan,
                buyer // Tambahkan user sebagai buyer dalam transaksi
            );

            transaksiRepository.save(transaksi);

            // Ubah status kendaraan menjadi TERJUAL
            kendaraan.setStatus(VehicleStatus.TERJUAL);
            kendaraanRepository.save(kendaraan);

            redirectAttributes.addFlashAttribute("successMessage", "Pembelian berhasil dikonfirmasi! Transaksi Anda telah dicatat.");
            return "redirect:/transactions"; // Redirect ke halaman daftar transaksi pembeli

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat memproses pembelian: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/vehicle/" + vehicleId; // Kembali ke form pembelian jika ada error
        }
    }

    @GetMapping("/messages")
    public String myMessages(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            return "redirect:/user-login"; // Arahkan ke login jika tidak terautentikasi
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User currentUser = (User) customUserDetails.getOrang();

        List<Message> allMessages = messageRepository.findBySenderOrReceiverOrderByTimestampDesc(currentUser, currentUser);

        // Group messages by conversation (other user and vehicle)
        Map<ConversationIdentifier, Message> latestMessages = new HashMap<>();
        Set<ConversationIdentifier> uniqueConversations = new HashSet<>();

        for (Message message : allMessages) {
            User otherUser = message.getSender().getId().equals(currentUser.getId()) ? message.getReceiver() : message.getSender();
            Kendaraan vehicle = message.getVehicle();
            ConversationIdentifier identifier = new ConversationIdentifier(vehicle, otherUser);

            // Only add if it\'s a new conversation or this message is newer
            if (!latestMessages.containsKey(identifier) || message.getTimestamp().isAfter(latestMessages.get(identifier).getTimestamp())) {
                latestMessages.put(identifier, message);
            }
        }

        // Sort conversations by the timestamp of the latest message
        List<Map.Entry<ConversationIdentifier, Message>> sortedConversations = new java.util.ArrayList<>(latestMessages.entrySet());
        sortedConversations.sort(Comparator.comparing(entry -> entry.getValue().getTimestamp(), Comparator.reverseOrder()));

        model.addAttribute("conversations", sortedConversations);
        return "messages";
    }

    @PostMapping("/chat/send-purchase-form")
    public String sendPurchaseFormLink(@RequestParam Long vehicleId, @RequestParam Long receiverId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof com.jualbelikendaraan.jualbeli.security.CustomUserDetails)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda harus login untuk mengirim form pembelian.");
            return "redirect:/user-login";
        }

        com.jualbelikendaraan.jualbeli.security.CustomUserDetails customUserDetails = (com.jualbelikendaraan.jualbeli.security.CustomUserDetails) authentication.getPrincipal();
        User sender = (User) customUserDetails.getOrang(); // Ini adalah penjual (seller)

        Optional<Kendaraan> kendaraanOptional = kendaraanRepository.findById(vehicleId);
        Optional<User> receiverOptional = userRepository.findById(receiverId);

        if (!kendaraanOptional.isPresent() || !receiverOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kendaraan atau penerima tidak valid.");
            return "redirect:/messages"; // Kembali ke daftar percakapan jika ada masalah
        }

        Kendaraan kendaraan = kendaraanOptional.get();
        User receiver = receiverOptional.get(); // Ini adalah pembeli (buyer)

        // Validasi: Pastikan pengirim adalah penjual kendaraan yang bersangkutan
        if (!sender.getId().equals(kendaraan.getSeller().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Anda bukan penjual kendaraan ini.");
            return "redirect:/chat/" + vehicleId + "/" + receiver.getId();
        }

        // Buat pesan otomatis dengan link ke form pembelian
        String purchaseFormLink = "/vehicle/" + kendaraan.getId() + "/buy";
        String messageContent = "Silakan lengkapi form pembelian kendaraan Anda di sini: " + purchaseFormLink;

        Message message = new Message(sender, receiver, kendaraan, messageContent);
        messageRepository.save(message);

        redirectAttributes.addFlashAttribute("successMessage", "Link form pembelian berhasil dikirim!");
        return "redirect:/chat/" + vehicleId + "/" + receiver.getId(); // Kembali ke halaman chat
    }
}
