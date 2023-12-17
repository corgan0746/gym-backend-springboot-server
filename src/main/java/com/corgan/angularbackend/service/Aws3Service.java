package com.corgan.angularbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
public class Aws3Service {

    @Value("${aws.s3.access.key}")
    private String accessKey;

    @Value("${aws.s3.secret.key}")
    private String secretKey;

    private S3Client getClient() {

        AwsCredentialsProvider credentials = new AwsCredentialsProvider() {
            @Override
            public AwsCredentials resolveCredentials() {
                return new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return accessKey;
                    }

                    @Override
                    public String secretAccessKey() {
                        return secretKey;
                    }
                };
            }
        };

        return S3Client.builder()
                .credentialsProvider(credentials)
                .region(Region.EU_WEST_2)
                .build();
    }

    public boolean uploadImage(byte[] bytes, String bucketName, String fileName, String description) {
        S3Client s3 = getClient();

        try {
            String theTags = "name="+fileName+"&description="+description;
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .tagging(theTags)
                    .build();

            PutObjectResponse objRes =  s3.putObject(putOb, RequestBody.fromBytes(bytes));

            return true;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    public void deleteImage(String bucketName, String fileName) {
        S3Client s3 = getClient();

        try {

            DeleteObjectRequest deleteObj = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3.deleteObject(deleteObj);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }


}
