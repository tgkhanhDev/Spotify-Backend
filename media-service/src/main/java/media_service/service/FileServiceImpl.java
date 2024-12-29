package media_service.service;

import com.mpatric.mp3agic.Mp3File;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileDetailResponse;
import media_service.dto.fileDto.response.FileUploadResponse;
import media_service.exception.ErrorCode;
import media_service.exception.MusicException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileServiceImpl implements FileService {

    @NotNull
    @Value("${music-file-path}")
    protected String musicFilePath;

    protected String rootFilePath =  System.getProperty("user.home");

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {

            FileDetailResponse fileDetail = getAudioDetail(request.getFile());

            if(request.getName() != null && !request.getName().trim().isEmpty()){
                fileDetail.setName(request.getName());
            }

            System.out.println("final File: " + fileDetail.toString());

            System.out.println("filePath: " + rootFilePath + musicFilePath);


        return FileUploadResponse.builder()
                .name(rootFilePath + musicFilePath)
                .build();
    }

    private boolean checkValidOriginOfAudioFile(MultipartFile file) {
        Tika tika = new Tika();
        try {
            String detectedType = tika.detect(file.getInputStream());
            boolean check = detectedType.equals("audio/mpeg") || detectedType.equals("audio/mp3");
            return check;
        } catch (Exception e) {
            throw new MusicException(ErrorCode.INVALID_MUSIC_FILE);
        }
    }
    private FileDetailResponse getAudioDetail(MultipartFile file) {

        if ( file.isEmpty() || !file.getOriginalFilename().endsWith(".mp3") || !checkValidOriginOfAudioFile(file) ) {
            throw new MusicException(ErrorCode.INVALID_MUSIC_FILE);
        }

        try {

            // Save the file to a temporary location
            java.io.File tempFile = java.io.File.createTempFile("uploaded", ".mp3");
            file.transferTo(tempFile);

            // Read MP3 file using Mp3agic
            Mp3File mp3File = new Mp3File(tempFile);

//            if (mp3File.hasId3v1Tag() || mp3File.hasId3v2Tag()) {
            long durationInSeconds = mp3File.getLengthInSeconds();

            return FileDetailResponse.builder()
                    .name(file.getOriginalFilename())
                    .duration(durationInSeconds)
                    .uploadTime(LocalDateTime.now())
                    .build();


        } catch (Exception e) {
            e.printStackTrace();
            throw new MusicException(ErrorCode.ERROR_WHEN_UPLOAD);
        }

    }


}
