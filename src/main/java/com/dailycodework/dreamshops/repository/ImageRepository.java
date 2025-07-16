package com.dailycodework.dreamshops.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailycodework.dreamshops.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long id);
}
