package com.example.paymentApi.config;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmailAddress(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
    }


    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }


    @Bean
    public ModelMapper modelMapper () {
            return new ModelMapper();
        }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

}
