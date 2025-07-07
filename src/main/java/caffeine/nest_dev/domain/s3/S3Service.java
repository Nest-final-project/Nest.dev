package caffeine.nest_dev.domain.s3;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.base-path}")
    private String basePath;


    /**
     * S3에 파일을 업로드하고, 업로드된 파일의 URL을 반환합니다.
     *
     * @param file 업로드할 MultipartFile
     * @return 업로드된 파일의 S3 URL
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 기본 경로 사용을 위해 folder 파라미터에 null을 전달합니다.
        return uploadFile(file, null);
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {

        String originalFilename = file.getOriginalFilename();

        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf("."); // 1. 파일명에서 마지막 '.'의 위치를 찾습니다.
        // 예: "my_image.jpg" -> 10
        // 예: "document.v1.pdf" -> 12
        // 예: "no_extension_file" -> -1

        if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
            // 2. 만약 마지막 '.'이 존재하고 (lastDotIndex != -1)
            //    그리고 '.'이 파일명의 마지막 문자가 아니라면 (lastDotIndex < originalFilename.length() - 1)
            //    (즉, "." 으로만 끝나는 파일이나 확장자가 없는 파일을 제외)
            fileExtension = originalFilename.substring(
                    lastDotIndex); // 3. 그 위치부터 끝까지 잘라내어 확장자를 얻습니다.
            // 예: "my_image.jpg" -> ".jpg"
        }

        // UUID + 원본 파일 확장자
        String uploadPrefix;
        if (folder != null && !folder.trim().isEmpty()) {
            uploadPrefix = folder.endsWith("/") ? folder : folder + "/";
        } else {
            uploadPrefix = basePath.endsWith("/") ? basePath : basePath + "/";
        }

        String s3FileName = uploadPrefix + UUID.randomUUID().toString() + fileExtension;

        try {
            // PutObjectRequest 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3FileName)
                    .contentType(file.getContentType()) // 파일 타입 설정 (없으면 다운로드될 수 있음)
                    .contentLength(file.getSize())
                    .build();

            // 파일 업로드
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 생성 (퍼블릭 접근이 설정되어 있다면)
            // AWS S3 객체 URL 형식: https://xn--jx2bx8c95n7jj.s3.xn--oy2bi6x.amazonaws.com/%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region,
                    s3FileName);

        } catch (S3Exception e) {
            System.err.println("S3 업로드 오류: " + e.awsErrorDetails().errorMessage());
            throw new IOException("S3 파일 업로드 중 오류가 발생했습니다.", e);
        } catch (IOException e) {
            System.err.println("파일 읽기/쓰기 오류: " + e.getMessage());
            throw new IOException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 파일 삭제 메서드
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String s3Key;
        try {
            // URL에서 S3 키 추출 (예: "https://bucket.s3.region.amazonaws.com/path/to/file.jpg" -> "path/to/file.jpg")
            // .com/ 이후의 문자열을 키로 간주합니다.
            s3Key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("유효하지 않은 S3 URL 형식: " + fileUrl);
            throw new IllegalArgumentException("유효하지 않은 S3 URL 형식입니다.", e);
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("S3 파일 삭제 완료: " + s3Key);
        } catch (S3Exception e) {
            System.err.println("S3 삭제 오류: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(
                    "S3 파일 삭제 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}
