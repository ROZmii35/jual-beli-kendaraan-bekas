package com.jualbelikendaraan.jualbeli.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "jenis")
public abstract class Kendaraan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String merk;
    private String tipe;
    private int tahun;
    @Column(name = "harga_asli")
    private double hargaAsli;
    @Column(name = "harga_publik")
    private double hargaPublik;
    @Column(name = "harga")
    private double harga;
    private String namaKendaraan;
    private String deskripsi;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_kendaraan", nullable = false)
    private JenisKendaraan jenisKendaraan;

    // No-arg constructor
    public Kendaraan() {
        this.status = VehicleStatus.BELUM_DIPENDING; // Default status
    }

    public Kendaraan(String merk, String tipe, int tahun, double hargaAsli, String namaKendaraan, String deskripsi, String imageUrl, User seller, JenisKendaraan jenisKendaraan) {
        this.merk = merk;
        this.tipe = tipe;
        this.tahun = tahun;
        this.hargaAsli = hargaAsli;
        this.namaKendaraan = namaKendaraan;
        this.deskripsi = deskripsi;
        this.imageUrl = imageUrl;
        this.hargaPublik = hargaAsli * 1.15; // Menambahkan 15% biaya administrasi
        this.harga = this.hargaPublik;
        this.status = VehicleStatus.BELUM_DIPENDING; // Default status
        this.seller = seller;
        this.jenisKendaraan = jenisKendaraan;
    }

    public abstract String getJenis();
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMerk() {
        return merk;
    }
    public void setMerk(String merk) {
        this.merk = merk;
    }
    public String getTipe() {
        return tipe;
    }
    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
    public int getTahun() {
        return tahun;
    }
    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public double getHargaAsli() {
        return hargaAsli;
    }

    public void setHargaAsli(double hargaAsli) {
        this.hargaAsli = hargaAsli;
        this.hargaPublik = hargaAsli * 1.15;
        this.harga = this.hargaPublik;
    }

    public double getHargaPublik() {
        return hargaPublik;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getNamaKendaraan() {
        return namaKendaraan;
    }

    public void setNamaKendaraan(String namaKendaraan) {
        this.namaKendaraan = namaKendaraan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public JenisKendaraan getJenisKendaraan() {
        return jenisKendaraan;
    }

    public void setJenisKendaraan(JenisKendaraan jenisKendaraan) {
        this.jenisKendaraan = jenisKendaraan;
    }
}
