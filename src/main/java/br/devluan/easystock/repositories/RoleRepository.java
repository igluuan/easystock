package br.devluan.easystock.repositories;

import br.devluan.easystock.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
