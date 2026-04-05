package com.example.financebackend.security;

import com.example.financebackend.entity.User;
import com.example.financebackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FinanceUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public FinanceUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));

    return new FinanceUserDetails(user);
  }
}

