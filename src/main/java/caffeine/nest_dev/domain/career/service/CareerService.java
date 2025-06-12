package caffeine.nest_dev.domain.career.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.career.dto.request.CareerRequestDto;
import caffeine.nest_dev.domain.career.dto.request.UpdateCareerRequestDto;
import caffeine.nest_dev.domain.career.dto.response.CareerResponseDto;
import caffeine.nest_dev.domain.career.dto.response.CareersResponseDto;
import caffeine.nest_dev.domain.career.dto.response.CertificateResponseDto;
import caffeine.nest_dev.domain.career.dto.response.FindCareerResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import caffeine.nest_dev.domain.certificate.repository.CertificateRepository;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final ProfileRepository profileRepository;
    private final CertificateRepository certificateRepository;

    // 경력 생성
    @Transactional
    public CareerResponseDto save(CareerRequestDto dto, Long profileId) {
        // 경력 증명서가 3개 초과 되면 에러 발생
        if (dto.getCertificates().size() > 3) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_LIMIT_EXCEEDED);
        }

        // 경력증명서가 null 일 때 예외 발생
        if (dto.getCertificates().isEmpty()) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_EMPTY);
        }

        // 프로필 조회
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.PROFILE_NOT_FOUND));

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

    // 경력 상세 페이지 조회
    @Transactional(readOnly = true)
    public FindCareerResponseDto findCareer(Long profileId, Long careerId) {

        // 경력 조회
        Career career = findByIdAndProfileId(careerId, profileId);

        return FindCareerResponseDto.of(career);
    }

    // 경력 목록 조회
    @Transactional(readOnly = true)
    public PagingResponse<CareersResponseDto> findCareers(Long profileId, Pageable pageable) {

        // 경력 페이지 조회
        Page<Career> careers = careerRepository.findAllByProfileId(profileId, pageable);

        // 리스트로 변환
        Page<CareersResponseDto> dtoPage = careers.map(CareersResponseDto::of);

        // 공통 페이징 DTO 로 변환해서 반환
        return PagingResponse.from(dtoPage);
    }

    // 경력 수정
    @Transactional
    public void updateCareer(Long profileId, Long careerId, UpdateCareerRequestDto dto) {

        // 경력 조회
        Career career = findByIdAndProfileId(careerId, profileId);

        // 경력 수정
        career.updateCareer(dto);
    }

    // 경력 삭제
    @Transactional
    public void deleteCareer(Long profileId, Long careerId) {

        // 경력 조회
        Career career = findByIdAndProfileId(careerId, profileId);

        // 경력 삭제
        careerRepository.delete(career);
    }

    // 경력 조회 시 null 이면 예외발생
    public Career findByIdAndProfileId(Long careerId, Long profileId) {
        return careerRepository.findByIdAndProfileId(careerId, profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CAREER));
    }
}
