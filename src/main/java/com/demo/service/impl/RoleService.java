package com.demo.service.impl;

import com.demo.repository.RoleRepository;
import org.springframework.stereotype.Service;


@Service
public record RoleService(RoleRepository roleRepository) {

}
