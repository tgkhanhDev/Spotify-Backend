package media_service.service;

import com.mpatric.mp3agic.Mp3File;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileDetailResponse;
import media_service.dto.fileDto.response.FileUploadResponse;
import media_service.exception.AuthenException;
import media_service.exception.ErrorCode;
import media_service.exception.MusicException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileServiceImpl implements FileService {

    @NotNull
    @Value("${dev-music-file-path}")
    protected String musicFilePath;

    @NotNull
    @Value("${dev-image-file-path}")
    protected String imageFilePath;

    @NotNull
    @Value("${url-music-deploy}")
    protected String musicDeployPath;

    @NotNull
    @Value("${url-image-deploy}")
    protected String imageDeployPath;

    protected String rootFilePath = System.getProperty("user.home");
    //File:name: UUID Token_LocalDateTime
    @Override
    public FileUploadResponse uploadFileAudio(FileUploadRequest request, Jwt jwtToken) {

        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID


        //Define file name for save
        String fileName = userIdClaims+"_"+LocalDateTime.now();

        // Define upload directory
        String filePath = rootFilePath + musicFilePath;
        String songName = request.getFile().getOriginalFilename();

        //check origin
        if (request.getFile().isEmpty() || !request.getFile().getOriginalFilename().endsWith(".mp3") || !checkValidOriginOfAudioFile(request.getFile())) {
            throw new MusicException(ErrorCode.INVALID_MUSIC_FILE);
        }

        try {
            //songName
            if(request.getName() != null && !request.getName().trim().isEmpty()) {
                    songName = request.getName();
            }

            //Add .ext for fileName
            try {
                String ext = request.getFile().getOriginalFilename().split("\\.")[1];
                fileName = fileName + "." + ext;
            } catch (Exception e){
                throw new MusicException(ErrorCode.INVALID_IMAGE_FILE);
            }

            String tmpPath = "/tmp/" + fileName;
            File tempFile = new File(tmpPath);
            request.getFile().transferTo(tempFile);

            // Process the temp file
            Mp3File mp3File = new Mp3File(tempFile);
            long durationInSeconds = mp3File.getLengthInSeconds();

            // Then save the file to its final destination
            String entirePath = filePath + fileName;
            File savePath = new File(entirePath);
            Files.copy(tempFile.toPath(), savePath.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return FileUploadResponse.builder()
                    .url(musicDeployPath + fileName)
                    .fileName(fileName)
                    .name(songName)
                    .duration(durationInSeconds)
                    .build();

        } catch (Exception e) {
            log.error("File Upload error: ", e);
            throw new MusicException(ErrorCode.ERROR_WHEN_UPLOAD);
        }

    }

    @Override
    public FileUploadResponse uploadFileImage(FileUploadRequest request, Jwt jwtToken) {

        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID

        //Define file name for save
        String fileName = userIdClaims +"_"+LocalDateTime.now();


        // Define upload directory
        String filePath = rootFilePath + imageFilePath;
        String imageName = request.getFile().getOriginalFilename();

        System.out.println("1: "+request.toString());
        //check origin
        try {
            BufferedImage image = ImageIO.read(request.getFile().getInputStream());
            if (image == null) {
                throw new MusicException(ErrorCode.INVALID_IMAGE_FILE);
            }
        }catch (Exception e){
            log.error("Error when extract image: "+ e.getMessage());
            throw new MusicException(ErrorCode.INVALID_IMAGE_FILE);
        }


        try {
            //imageName
            if(request.getName() != null && !request.getName().trim().isEmpty()) {
                imageName = request.getName();
            }

            //Add .ext for fileName
            try {
                String ext = request.getFile().getOriginalFilename().split("\\.")[1];
                fileName = fileName + "." + ext;
            } catch (Exception e){
                throw new MusicException(ErrorCode.INVALID_IMAGE_FILE);
            }

            String entirePath = filePath + fileName;
            System.out.println("FilePathImage: " + entirePath);
            request.getFile().transferTo(new File(entirePath));

            System.out.println("fileImage: " + FileUploadResponse.builder()
                    .url(imageDeployPath + fileName)
                    .fileName(fileName)
                    .name(imageName)
                    .build().toString());

            return FileUploadResponse.builder()
                    .url(imageDeployPath + fileName)
                    .fileName(fileName)
                    .name(imageName)
                    .build();

        } catch (Exception e) {
            log.error("File Upload error: ", e);
            throw new MusicException(ErrorCode.ERROR_WHEN_UPLOAD);
        }

    }

    private boolean checkValidOriginOfAudioFile(MultipartFile file) {
        Tika tika = new Tika();
        try {
            String detectedType = tika.detect(file.getInputStream());
//            System.out.println("detectedType: " + detectedType);
            boolean check = detectedType.equals("audio/mpeg") || detectedType.equals("audio/mp3");
            return check;
        } catch (Exception e) {
            throw new MusicException(ErrorCode.INVALID_MUSIC_FILE);
        }
    }

}
