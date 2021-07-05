package com.nexsol.grape.repository;

import com.nexsol.grape.domain.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRepository {

    public Image save(Image image);
    public List<Image> findById(Long id);
    public Optional<Image> getById(String name);

}
