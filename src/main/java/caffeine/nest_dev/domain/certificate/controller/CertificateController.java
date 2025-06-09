package caffeine.nest_dev.domain.certificate.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.certificate.dto.request.UpdateCertificateRequestDto;
import caffeine.nest_dev.domain.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/careers/{careerId}/certificates/{certificateId}")
public class CertificateController {

    private final CertificateService certificateService;

    // 경력증명서 수정
    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> updateCertificate(
            @PathVariable Long careerId,
            @PathVariable Long certificateId,
            @RequestBody UpdateCertificateRequestDto dto
    ) {

        certificateService.updateCertificate(careerId, certificateId, dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CERTIFICATE_UPDATED));
    }

    // 경력증명서 삭제
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteCertificate(
            @PathVariable Long careerId,
            @PathVariable Long certificateId
    ) {

        certificateService.deleteCertificate(careerId, certificateId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CERTIFICATE_DELETED));
    }
}
