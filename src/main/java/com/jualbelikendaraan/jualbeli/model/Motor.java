package com.jualbelikendaraan.jualbeli.model;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Motor")
public class Motor extends  Kendaraan{
    private String jenisMotor;

    public Motor() {
        super();
    }

    @Override
    public String getJenis(){
        return "Motor";
    }
    public String getJenisMotor() {
        return jenisMotor;
    }
    public void setJenisMotor(String jenisMotor) {
        this.jenisMotor = jenisMotor;
    }
}
