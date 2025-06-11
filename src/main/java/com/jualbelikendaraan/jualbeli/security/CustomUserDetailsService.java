package com.jualbelikendaraan.jualbeli.security;

import com.jualbelikendaraan.jualbeli.model.Admin;
import com.jualbelikendaraan.jualbeli.model.User;
import com.jualbelikendaraan.jualbeli.repository.AdminRepository;
import com.jualbelikendaraan.jualbeli.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Coba cari di Admin repository berdasarkan username atau email
        Optional<Admin> adminOptional = adminRepository.findByUsername(identifier);
        if (adminOptional.isEmpty()) {
            adminOptional = adminRepository.findByEmail(identifier);
        }

        if (adminOptional.isPresent()) {
            return new CustomUserDetails(adminOptional.get());
        }

        // Coba cari di User repository berdasarkan username atau email
        Optional<User> userOptional = userRepository.findByUsername(identifier);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(identifier);
        }

        if (userOptional.isPresent()) {
            return new CustomUserDetails(userOptional.get());
        }

        throw new UsernameNotFoundException("User not found: " + identifier);
    }
} 