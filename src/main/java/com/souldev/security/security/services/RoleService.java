package com.souldev.security.security.services;

import com.souldev.security.security.entities.Role;
import com.souldev.security.security.enums.RoleList;
import com.souldev.security.security.respositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Optional<Role> getByRoleName(RoleList roleName){
        return roleRepository.findByName(roleName.name());
    }

    public Optional<Role> getByRoleId(String id){
        return roleRepository.findById(id);
    }
    public List<Role> getAllRoles (){
        return roleRepository.findAll();
    }
    
    
}
