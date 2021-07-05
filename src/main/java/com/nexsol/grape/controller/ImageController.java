package com.nexsol.grape.controller;

import com.nexsol.grape.domain.Image;
import com.nexsol.grape.service.ImageService;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @PostMapping("/users/image/new")
    @ResponseBody
    public Map imageCreate(ImageForm imageForm){
        Map result = new HashMap<String, Object>();
        String path = null;

        for(int i=1;i<=imageForm.getFiles().size();i++){
            Image image = new Image();
            image.setId(imageForm.getId());
            image.setFile(imageForm.getFiles().get(i-1));
            image.setName("/"+imageForm.getId()+"/"+i);

            path = imageService.upload(image);
        }
        result.put("path", path);

        return result;
    }

    @GetMapping("/users/images")
    @ResponseBody
    public JSONObject imageFind(@RequestParam("id") Long id){

        JSONObject jsonObject = new JSONObject();

        List<Image> images = imageService.findOne(id);
        for(int i=1;i<=images.size();i++){
            jsonObject.put(""+i, images.get(i-1).getName());
        }

        return jsonObject;
    }

    @GetMapping("/users/image/download")
    public Object imageDownload(@RequestParam("name") String objectName){

        Optional<Image> result = imageService.downloadByName(objectName);
        if(result.isEmpty()){
            return ResponseEntity.status(HttpStatus.SC_NO_CONTENT);
        }else{
            return result.get().getFile();
        }
    }
}
