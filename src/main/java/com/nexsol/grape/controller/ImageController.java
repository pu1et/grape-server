package com.nexsol.grape.controller;

import com.nexsol.grape.domain.Image;
import com.nexsol.grape.service.ImageService;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.util.*;

@Controller
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    /**
     * @param images
     * @return path
     */
    @PostMapping("/users/image/new")
    @ResponseBody
    public int imageTest(@RequestParam("upload") List<MultipartFile> images, @RequestParam("id") long id){

        String path = null;

        for(int i=0;i<images.size();i++){
            try {
                MultipartFile tmpFile = images.get(i);
                String name = id + "/" + (i + 1);

                Image image = new Image(1L, tmpFile, name);
                path = imageService.upload(image);
            }catch (NullPointerException e){
                e.printStackTrace();
                return HttpStatus.SC_NO_CONTENT;
            }
        }
        return HttpStatus.SC_OK;
    }

    @PostMapping("/users/image/test")
    @ResponseBody
    public Map imageCreate(ImageForm imageForm){
        Map result = new HashMap<String, Object>();
        String path = null;

        for(int i=1;i<=imageForm.getFiles().size();i++){
            long id = imageForm.getId();
            MultipartFile file = imageForm.getFiles().get(i-1);
            String name = imageForm.getId()+"/"+i;

            Image image = new Image(id, file, name);
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
