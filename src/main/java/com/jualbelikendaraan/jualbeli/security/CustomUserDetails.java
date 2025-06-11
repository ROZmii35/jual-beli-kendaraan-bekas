package com.jualbelikendaraan.jualbeli.security;

import com.jualbelikendaraan.jualbeli.model.Orang;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private Orang orang;

    public CustomUserDetails(Orang orang) {
        this.orang = orang;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + orang.getRole()));
    }

    @Override
    public String getPassword() {
        return orang.getPassword();
    }

    @Override
    public String getUsername() {
        return orang.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Orang getOrang() {
        return orang;
    }
} 