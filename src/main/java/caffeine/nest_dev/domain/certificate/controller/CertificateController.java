package caffeine.nest_dev.domain.certificate.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.certificate.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Certificate", description = "경력증명서 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/careers/{careerId}/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    // 경력증명서 수정
    @Operation(summary = "경력증명서 수정", description = "특정 경력의 증명서 파일을 수정합니다")
    @ApiResponse(responseCode = "200", description = "경력증명서 수정 성공")
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<Void>> updateCertificate(
            @Parameter(description = "경력 ID") @PathVariable Long careerId,
            @Parameter(description = "경력증명서 파일 목록") @RequestPart(value = "files") List<MultipartFile> files
    ) {

        certificateService.updateCertificate(careerId, files);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CERTIFICATE_UPDATED));
    }
}
