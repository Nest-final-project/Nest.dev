package caffeine.nest_dev.domain.certificate.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.certificate.dto.request.UpdateCertificateRequestDto;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import caffeine.nest_dev.domain.certificate.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;

    // 경력증명서 수정
    @Transactional
    public void updateCertificate(Long careerId, Long certificateId,
            UpdateCertificateRequestDto dto) {

        // 경력 id 에 해당하는 경력 증명서 1개 조회
        Certificate certificate = findByIdAndCareerId(certificateId, careerId);

        // 경력증명서 수정
        certificate.updateCertificate(dto);
    }

    // 경력증명서 삭제
    @Transactional
    public void deleteCertificate(Long careerId, Long certificateId) {

        // 경력 증명서 조회
        Certificate certificate = findByIdAndCareerId(certificateId, careerId);

        // 경력증명서 삭제
        certificateRepository.delete(certificate);
    }

    // 경력 id 와 경력 증명서 id 로 경력 증명서 조회
    public Certificate findByIdAndCareerId(Long certificateId, Long careerId) {

        return certificateRepository.findByIdAndCareerId(certificateId, careerId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CERTIFICATE));
    }
}
