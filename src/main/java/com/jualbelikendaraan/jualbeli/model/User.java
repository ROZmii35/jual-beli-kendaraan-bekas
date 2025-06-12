package com.jualbelikendaraan.jualbeli.model;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class User extends Orang {
    @OneToMany(mappedBy = "user")
    private List<Transaksi> transaksiList;
    public List<Transaksi> getTransaksiList() {
        return transaksiList;
    }
    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }
}
