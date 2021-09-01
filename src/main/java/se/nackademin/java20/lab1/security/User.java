package se.nackademin.java20.lab1.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class User implements UserDetails {
    private final String username;
    private final List<Role> roles;

    public User(String username, List<Role> roles) {
        this.username = username;
        this.roles = new ArrayList<>(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> setAuths = new HashSet<>();
        for (Role userRole : roles) {
            setAuths.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
        }

        return Collections.unmodifiableSet(setAuths);
    }

    public void makeAdmin() {
        roles.add(Role.ADMIN);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
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
}
