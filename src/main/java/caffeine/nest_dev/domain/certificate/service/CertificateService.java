package caffeine.nest_dev.domain.certificate.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import caffeine.nest_dev.domain.certificate.repository.CertificateRepository;
import caffeine.nest_dev.domain.s3.S3Service;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CareerRepository careerRepository;
    private final CertificateRepository certificateRepository;
    private final S3Service s3Service;

    // 경력증명서 수정
    @Transactional
    public void updateCertificate(Long careerId, List<MultipartFile> files) {

        // 경력 증명서 개수 유효성 검사(3개까지 허용)
        if (files != null && files.size() > 3) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_LIMIT_EXCEEDED);
        }

        // 새 경력증명서가 없는 경우
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_EMPTY);
        }

        // 경력 조회
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CAREER));

        // 경력에 해당되는 경력증명서 가져오기
        List<Certificate> list = certificateRepository.findByCareerId(careerId);

        // S3에서 파일 삭제
        if (!list.isEmpty()) {
            for (Certificate certificate : list) {
                try {
                    s3Service.deleteFile(certificate.getFileUrl());
                } catch (Exception e) {
                    log.error(
                            "S3 파일 삭제 실패: " + certificate.getFileUrl() + ", 오류: " + e.getMessage());
                }
            }

            // 경력 증명서 삭제
            certificateRepository.deleteAll(list);
        }

        // 경력에서 증명서 부분 삭제
        career.getCertificates().clear();

        // 경력에 새로운 경력증명서 추가 및 S3 에 업로드
        List<Certificate> certificates = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        String fileUrl = s3Service.uploadFile(file);
                        return Certificate.builder()
                                .fileUrl(fileUrl)
                                .build();
                    } catch (IOException e) {
                        // S3Service에서 발생한 IO 예외 (예: 파일 읽기/쓰기 오류, S3 통신 오류)
                        throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
                    } catch (IllegalArgumentException e) {
                        // S3Service에서 발생한 파일 유효성 검사 예외
                        throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
                    }
                })
                .toList();

        // 경력과 연관관계 설정(경력에 경력증명서 저장)
        certificates.forEach(career::addCertificate);

        // 새로운 경력증명서 생성
        careerRepository.save(career);
    }
}
