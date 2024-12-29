package com.ryan_frederick.painting.painting;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
public class PaintingUploaderService {
    String bucketName = "paintingappimagestorage";
    Logger logger = LogManager.getLogger(PaintingUploaderService.class);
    S3Client s3Client;
    S3Presigner presigner;
    public PaintingUploaderService() throws IOException {
        Dotenv dotenv = Dotenv.load();
        String accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
        String secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        assert accessKey != null;
        System.setProperty("AWS_ACCESS_KEY_ID", accessKey);
        assert secretKey != null;
        System.setProperty("AWS_SECRET_ACCESS_KEY", secretKey);

        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);


        Region region = Region.US_EAST_2;

        StaticCredentialsProvider creds = StaticCredentialsProvider.create(awsBasicCredentials);

        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(creds)
                .build();

        presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(creds)
                .build();

    }
    public String uploadImage(String dataUrl) throws IOException {
        // convert data url to jpg file
        String base64Data = dataUrl.split(",")[1];
        byte[] bytes = Base64.getDecoder().decode(base64Data);

        Path tempFile = Files.createTempFile("tempImage", ".jpg");
        Files.write(tempFile, bytes);

        // generate unique random image name using uuid
        String imageName = UUID.randomUUID().toString();

        // upload image to s3
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageName)
                        .build(),
                RequestBody.fromFile(tempFile)
        );

        return imageName;
    }

    public void deleteImage(String imageName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build()
        );
    }

    public String createImageUrl(String imageName) {
        logger.info("attempting to generate url for image name: " + imageName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(60))
                .build();


        return presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }
}
