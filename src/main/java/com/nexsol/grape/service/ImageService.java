package com.nexsol.grape.service;

import com.nexsol.grape.domain.Image;
import com.nexsol.grape.repository.ImageRepository;

import java.util.List;
import java.util.Optional;


public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    public String upload(Image image){
        imageRepository.save(image);
        return image.getName();
    }

    public List<Image> findOne(Long id){
        return imageRepository.findById(id);
    }

    public Optional<Image> downloadByName(String name){
        return imageRepository.getById(name);
    }
}
