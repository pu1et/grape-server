package com.nexsol.grape.controller;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class ImageForm {

    private Long id;
    private List<MultipartFile> files;
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MultipartFile> getFiles() {
        return this.files;
    }

    public void setFile(List<MultipartFile> files) {
        this.files = new ArrayList<MultipartFile>(files);
    }
}
