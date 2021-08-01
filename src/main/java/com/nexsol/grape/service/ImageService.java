package com.nexsol.grape.service;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.nexsol.grape.controller.ApplyLoanForm;
import com.nexsol.grape.domain.Image;
import com.nexsol.grape.util.ObjectStorageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ObjectStorageUtil objectStorageUtil;

    /**
     * 이미지 업로드
     * @param loanId
     * @param loanForm
     */
    @Transactional
    public void uploadImages(Long loanId, ApplyLoanForm loanForm){

        List<MultipartFile> images = loanForm.getImages();

        String folderKey = loanId + "/" + loanForm.getId() + "/";
        objectStorageUtil.createFolder(folderKey);

        for(int i=0;i<images.size();i++){
            try {
                MultipartFile tmpFile = images.get(i);
                String tmpExt = FilenameUtils.getExtension(tmpFile.getOriginalFilename());
                String imageFileKey = folderKey + (i + 1) + "." + tmpExt;

                Image image = new Image(loanForm.getId(), loanId, tmpFile, imageFileKey);
                objectStorageUtil.upload(image);

            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 대출마다 신청한 총 회원 수(폴더 수)
     * @param loanId
     * @return
     */
    @Transactional
    public Integer getFolderListSize(Long loanId){
        List<String> folders = objectStorageUtil.getObjectFolders(loanId);
        return folders.size();
    }

    /**
     * 사용자가 신청한 대출에 제출한 총 이미지 파일 수 - 폴더 제외
     * @param userId, loanId
     * @return
     */
    @Transactional
    public Integer getImageListSize(Long loanId, Long userId){
        List<String> objectKeys = objectStorageUtil.getObjectKeys(loanId, userId);

        // 폴더 제외
        return objectKeys.size() == 0 ? 0 : objectKeys.size() - 1;
    }

    @Transactional
    public List<String> getImageNameList(Long loanId, Long userId) {
        List<String> objectKeys = objectStorageUtil.getObjectKeys(loanId, userId);
        objectKeys.remove(0);
        return objectKeys;
    }

    /**
     * 저장소에서 이미지를 꺼내 파일로 변환하여 반환 - 폴더 제외
     * @param userId, loanId
     * @return
     */
    @Transactional
    public List<ByteArrayResource> getImageFiles(Long loanId, Long userId){
        List<String> objectKeys = objectStorageUtil.getObjectKeys(loanId, userId);
        // 폴더 제외
        objectKeys.remove(0);
        List<ByteArrayResource> files = objectStorageUtil.downloadFiles(objectKeys);
        return files;
    }

    /**
     * 사용자가 업로드한 모든 이미지 삭제 - 폴더 포함
     * @param userId
     * @param loanId
     */
    @Transactional
    public void deleteImageFiles(Long loanId, Long userId){
        List<String> objectKeys = objectStorageUtil.getObjectKeys(loanId, userId);
        objectStorageUtil.deleteImages(objectKeys);
    }
}
