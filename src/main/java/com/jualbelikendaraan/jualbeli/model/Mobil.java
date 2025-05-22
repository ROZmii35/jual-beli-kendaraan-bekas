package com.jualbelikendaraan.jualbeli.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Mobil")
public class Mobil extends Kendaraan {
    private int jumlahPintu;
    @Override
    public String getJenis(){
        return "Mobil";
    }
    public int getJumlahPintu() {
        return jumlahPintu;
    }
    public void setJumlahPintu(int jumlahPintu) {
        this.jumlahPintu = jumlahPintu;
    }
}
