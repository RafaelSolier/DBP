package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    @Query("SELECT c FROM Proveedor c JOIN c.user u WHERE u.email = :email")
    Optional<Proveedor> findByEmail(@Param("email") String email);
}
