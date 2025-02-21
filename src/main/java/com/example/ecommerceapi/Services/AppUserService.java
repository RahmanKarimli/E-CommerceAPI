package com.example.ecommerceapi.Services;

import com.example.ecommerceapi.Exceptions.AuthenticationFailedException;
import com.example.ecommerceapi.Models.AppUser;
import com.example.ecommerceapi.Repositories.AppUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final AppUserRepo appUserRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (appUser != null) {
            return User.withUsername(appUser.getUsername())
                    .password(appUser.getPassword())
                    .roles(String.valueOf(appUser.getRole()))
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }

    public AppUser loadAppUserById(long id) {
        return appUserRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUser getCurrentUser(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        return appUserRepository.findByUsername(user.getUsername()).orElseThrow(() -> new AuthenticationFailedException("User not found"));
    }
}
