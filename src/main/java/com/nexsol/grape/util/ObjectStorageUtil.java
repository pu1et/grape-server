package com.nexsol.grape.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.nexsol.grape.domain.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ObjectStorageUtil {

    @Value("${cloud.ncp.objectStorage.bucketName}")
    public String bucketName;

    private final AmazonS3 s3;

    public ObjectStorageUtil(){
        String endPoint = "https://kr.object.ncloudstorage.com";
        String regionName = "kr-standard";
        String accessKey = "xFUyM3vJgRk7v8vUyPMo";
        String secretKey = "UgA268i1uFeEF0cY3gwcbHdC8y36m9a4r5Fw0XYz";

        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    /**
     * 이미지 저장 - objectName : 파일 이름(고유함)
     * @param image
     * @return
     */
    public Image upload(Image image){

        // 오브젝트 경로
        String objectName = image.getName();
        try{

            byte[] bytes = image.getFile().getBytes();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            s3.putObject(new PutObjectRequest(bucketName, objectName, byteArrayInputStream, objectMetadata));

            log.info("Object {} has been created.\n", objectName);
        }catch (AmazonS3Exception e){
            e.printStackTrace();
        }catch (SdkClientException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return image;
    }

    public void createFolder(String folderKey){

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderKey, new ByteArrayInputStream(new byte[0]), objectMetadata);

        try{
            s3.putObject(putObjectRequest);
            log.info("Folder {} has been created.\n", folderKey);
        } catch (SdkClientException e){
            e.printStackTrace();
        }
    }

    /**
     * 대출 신청자의 첨부파일 이름 리스트 (object 이름)
     * @param userId
     * @param loanId
     * @return
     */
    public List<String> getObjectKeys(Long loanId, Long userId) {

        String folderName = loanId + "/" + userId + "/";
        List<String> objectList = new ArrayList<>();

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withDelimiter("/")
                .withPrefix(folderName)
                .withMaxKeys(100);

        ObjectListing objectListing = s3.listObjects(listObjectsRequest);

        log.info("[" + folderName + "] File List : ");
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
            log.info("     name= " + objectSummary.getKey() + ", size= " + objectSummary.getSize() + ", owner= " + objectSummary.getOwner().getId());
            objectList.add(objectSummary.getKey());
        }

        return objectList;
    }

    /**
     * 대출 신청자 리스트
     * @param loanId
     * @return
     */
    public List<String> getObjectFolders(Long loanId) {

        String folderName = loanId + "/";
        List<String> folderList = new ArrayList<>();

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucketName)
                .withDelimiter("/")
                .withPrefix(folderName)
                .withMaxKeys(100);

        ObjectListing objectListing = s3.listObjects(listObjectsRequest);

        log.info("[" + folderName + "] Folder List : ");
        for(String commonPrefixes : objectListing.getCommonPrefixes()){
            log.info("     name= " + commonPrefixes);
            folderList.add(commonPrefixes);
        }
        return folderList;
    }

    /**
     * 이미지 다운로드
     * @param objectList
     * @return
     */
    public List<ByteArrayResource> downloadFiles(List<String> objectList) {

        List<ByteArrayResource> images = new ArrayList<>();
        try{
            S3ObjectInputStream s3ObjectInputStream;

            for(String objectName : objectList) {

                S3Object s3Object = s3.getObject(bucketName, objectName);
                s3ObjectInputStream = s3Object.getObjectContent();

                byte[] bytes = s3ObjectInputStream.readAllBytes();
                images.add(new ByteArrayResource(bytes));

                log.info("Object {} has been downloaded.", objectName);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return images;
    }

    private File objectToFile(String objectName, S3ObjectInputStream s3ObjectInputStream){

        File file = new File(objectName);
        try{
            FileUtils.copyInputStreamToFile(s3ObjectInputStream, file);
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 이미지 1개 삭제
     * @param objectKey
     */
    public void deleteImage(String objectKey){

        try {
            s3.deleteObject(bucketName, objectKey);
            log.info("Object {} has been deleted.\n", objectKey);
        } catch (SdkClientException e){
            e.printStackTrace();
        }
    }

    /**
     * 이미지 리스트 삭제
     * @param objectKeyList
     */
    public void deleteImages(List<String> objectKeyList){

        try {
            for (int i = 0; i < objectKeyList.size(); i++) {
                String objectKey = objectKeyList.get(i);
                s3.deleteObject(bucketName, objectKey);
                log.info("Object {} has been deleted.\n", objectKey);
            }
        } catch (SdkClientException e){
            e.printStackTrace();
        }
    }

}

