package com.nexsol.grape.domain;

import org.springframework.web.multipart.MultipartFile;

public class Image {

    private Long id;
    private MultipartFile file; //file.getBytes() 실제 내용
    private String name; // folder + name

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
