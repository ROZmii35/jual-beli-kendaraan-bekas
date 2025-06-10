package com.jualbelikendaraan.jualbeli.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PageController {
    @GetMapping("/")
    public String dashboard() {
        return "index";
    }

    @GetMapping("/wishlist")
    public String wishlist() {
        return "wishlist"; // Akan mencari wishlist.html di templates/
    }
}
