package caffeine.nest_dev.domain.certificate.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import caffeine.nest_dev.domain.certificate.repository.CertificateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CareerRepository careerRepository;
    private final CertificateRepository certificateRepository;

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

        // 경력 증명서 삭제
        certificateRepository.deleteAll(list);

        // 경력에서 증명서 부분 삭제
        career.getCertificates().clear();

        // 경력에 새로운 경력증명서 추가
        files.stream()
                .map(url -> Certificate.builder()
                        .fileUrl(url.getOriginalFilename())
                        .build())
                        .forEach(career::addCertificate);

        // 새로운 경력증명서 생성
        careerRepository.save(career);
    }
}
