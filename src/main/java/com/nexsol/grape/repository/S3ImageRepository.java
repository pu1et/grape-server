package com.nexsol.grape.repository;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.nexsol.grape.domain.Image;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class S3ImageRepository implements ImageRepository{

    final String endPoint = "https://kr.object.ncloudstorage.com";
    final String regionName = "kr-standard";
    final String accessKey = "xFUyM3vJgRk7v8vUyPMo";
    final String secretKey = "UgA268i1uFeEF0cY3gwcbHdC8y36m9a4r5Fw0XYz";

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .build();

    String bucketName = "grape-bucket";
    String objectName = "doc-image";


    @Override
    public Image save(Image image){

        String folderName = image.getId()+"/";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);


        try{
            s3.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        }catch (AmazonS3Exception e){
            e.printStackTrace();
        }catch (SdkClientException e){
            e.printStackTrace();
        }

        // upload local file
        String objectName = "doc-image";
        String filePath = image.getName();
        try{

            byte[] bytes = image.getFile().getBytes();
            objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            s3.putObject(new PutObjectRequest(bucketName, filePath, byteArrayInputStream, objectMetadata));

            System.out.format("Object %s has been created.\n", objectName);
        }catch (AmazonS3Exception e){
            e.printStackTrace();
        }catch (SdkClientException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return image;
    }


/*
    @Override
    public Image save(Image image){

        String folderName = image.getId()+"/";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);


        try{
            s3.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        }catch (AmazonS3Exception e){
            e.printStackTrace();
        }catch (SdkClientException e){
            e.printStackTrace();
        }

        try{
//            String folderName = image.getId()+"/";
            File file = new File(image.getFile().getOriginalFilename());
            image.getFile().transferTo(file);

            long contentLength = file.length();
            long partSize = 10 * 1024 * 1024;

            InitiateMultipartUploadResult initiateMultipartUploadResult = s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, objectName));
            String uploadId = initiateMultipartUploadResult.getUploadId();
            System.out.println("uploadId = " + uploadId);
            List<PartETag> partETagList = new ArrayList<PartETag>();

            long fileOffset = 0;
            for(int i=1; fileOffset < contentLength; i++){
                partSize = Math.min(partSize, (contentLength - fileOffset));

                UploadPartRequest uploadPartRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(objectName)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFile(file)
                        .withFileOffset(fileOffset)
                        .withPartSize(partSize);

                UploadPartResult uploadPartResult = s3.uploadPart(uploadPartRequest);
                partETagList.add(uploadPartResult.getPartETag());

                fileOffset += partSize;
            }
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETagList);
            CompleteMultipartUploadResult comResponse = s3.completeMultipartUpload(compRequest);

//            CompleteMultipartUploadResult completeMultipartUploadResult = s3.completeMultipartUpload(new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETagList));

        }catch (IOException e){
            e.printStackTrace();
        }catch (AmazonS3Exception e){
            e.printStackTrace();
        }catch (SdkClientException e){
            e.printStackTrace();
        }

        return image;

    }



 */

    @Override
    public List<Image> findById(Long id) {
        List<Image> result = new ArrayList<Image>();
        try{
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withPrefix("/"+id)
                    .withMaxKeys(300);

            ObjectListing objectListing = s3.listObjects(listObjectsRequest);

            System.out.println("Object List: ["+id+"]:\n");
            while(true){
                for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
                    System.out.println("name = " + objectSummary.getKey());
                    Image image = new Image();
                    image.setId(id);
                    image.setName(objectSummary.getKey());
                    result.add(image);
                }

                if(objectListing.isTruncated()){
                    objectListing = s3.listNextBatchOfObjects(objectListing);
                }else break;
            }
        }catch (AmazonS3Exception e){
            System.err.println(e.getErrorMessage());
        }

        return result;
    }

    @Override
    public Optional<Image> getById(String objectName) {

        try{
            S3Object s3Object = s3.getObject(bucketName, objectName);
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

            byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
            MultipartFile multipartFile = new MockMultipartFile(objectName, bytes);

            Image image = new Image();
            image.setName(objectName);
            image.setFile(multipartFile);

            s3ObjectInputStream.close();

            return Optional.ofNullable(image);

        }catch (IOException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
