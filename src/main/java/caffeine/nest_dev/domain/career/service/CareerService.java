package caffeine.nest_dev.domain.career.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.career.dto.request.CareerRequestDto;
import caffeine.nest_dev.domain.career.dto.response.CareerResponseDto;
import caffeine.nest_dev.domain.career.dto.response.CertificateResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.entity.Certificate;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.career.repository.CertificateRepository;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final ProfileRepository profileRepository;
    private final CertificateRepository certificateRepository;

    public CareerResponseDto save(CareerRequestDto dto, Long profileId) {

        // 프로필 조회
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));

        // 경력 Entity 생성 및 profile 과 연관관계 설정
        Career career = dto.toEntity(dto, profile);

        // 경력과 경력증명서 연관관계 설정
        List<Certificate> certificateList = dto.getCertificates().stream()
                .map(url -> Certificate.builder()
                        .fileUrl(url)
                        .career(career)
                        .build())
                .toList();

        // 경력에 경력증명서 추가
        career.getCertificates().addAll(certificateList);

        // 경력 저장
        Career saved = careerRepository.save(career);

        // 경력 증명서 리스트 dto 만들기
        List<Certificate> list = certificateRepository.findByCareer(saved);
        List<CertificateResponseDto> responseDto = CertificateResponseDto.fromList(list);

        return CareerResponseDto.of(saved, responseDto);
    }
}
