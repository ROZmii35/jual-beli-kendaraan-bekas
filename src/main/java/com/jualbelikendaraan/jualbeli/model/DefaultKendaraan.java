package com.jualbelikendaraan.jualbeli.model;

import jakarta.persistence.Entity;

// This class is a concrete implementation of Kendaraan for form binding purposes
@Entity
public class DefaultKendaraan extends Kendaraan {

    // You might need to adjust constructors if Kendaraan has specific ones
    public DefaultKendaraan() {
        // Default constructor
    }

    @Override
    public String getJenis() {
        if (this.getJenisKendaraan() != null) {
            return this.getJenisKendaraan().name();
        } else {
            return "LAINNYA"; // Default jika jenis kendaraan belum diatur
        }
    }

    // You might also need to explicitly declare and override getters/setters here
    // if Kendaraan's fields are not automatically inherited or if you need specific logic.
    // However, Spring typically handles inherited fields through reflection for @ModelAttribute.

    // If Kendaraan has @Id and other annotations, they are typically inherited.
    // If you need specific ID generation for DefaultKendaraan, you might need:
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;
} 