package com.jualbelikendaraan.jualbeli.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User buyer;
    @ManyToOne
    private User seller;
    @ManyToOne
    private Kendaraan kendaraan;
    private LocalDate tanggal;
    @Column(precision = 18, scale = 2)
    private BigDecimal hargaAkhir;
    private String status;
    private String metodePembayaran;
    @Column(length = 1000)
    private String catatanTambahan;

    @ManyToOne
    private User user;

    public Transaksi() {
    }

    public Transaksi(User buyer, User seller, Kendaraan kendaraan, LocalDate tanggal, BigDecimal hargaAkhir, String status, String metodePembayaran, String catatanTambahan, User user) {
        this.buyer = buyer;
        this.seller = seller;
        this.kendaraan = kendaraan;
        this.tanggal = tanggal;
        this.hargaAkhir = hargaAkhir;
        this.status = status;
        this.metodePembayaran = metodePembayaran;
        this.catatanTambahan = catatanTambahan;
        this.user = user;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getBuyer() {
        return buyer;
    }
    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }
    public User getSeller() {
        return seller;
    }
    public void setSeller(User seller) {
        this.seller = seller;
    }
    public Kendaraan getKendaraan() {
        return kendaraan;
    }
    public void setKendaraan(Kendaraan kendaraan) {
        this.kendaraan = kendaraan;
    }
    public LocalDate getTanggal() {
        return tanggal;
    }
    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }
    public BigDecimal getHargaAkhir() {
        return hargaAkhir;
    }
    public void setHargaAkhir(BigDecimal hargaAkhir) {
        this.hargaAkhir = hargaAkhir;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMetodePembayaran() {
        return metodePembayaran;
    }
    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }
    public String getCatatanTambahan() {
        return catatanTambahan;
    }
    public void setCatatanTambahan(String catatanTambahan) {
        this.catatanTambahan = catatanTambahan;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
