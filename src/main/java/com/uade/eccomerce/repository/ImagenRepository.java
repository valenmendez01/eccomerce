package com.uade.eccomerce.repository;

import com.uade.eccomerce.entity.ImagenProductos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagenRepository extends JpaRepository<ImagenProductos, Long> {

}