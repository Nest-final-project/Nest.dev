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
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.repository.ProfileRepository;
import caffeine.nest_dev.domain.s3.S3Service;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    // 경력 생성
    @Transactional
    public CareerResponseDto save(CareerRequestDto dto, Long profileId, List<MultipartFile> files) {

        // 경력증명서 파일은 3개까지 가능
        if (files != null && files.size() > 3) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_LIMIT_EXCEEDED);
        }

        // 경력증명서가 null 일 때 예외 발생
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BaseException(ErrorCode.CAREER_CERTIFICATE_EMPTY);
        }

        // 프로필 조회
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.PROFILE_NOT_FOUND));

        // 경력 Entity 생성 및 profile 과 연관관계 설정
        Career career = CareerRequestDto.toEntity(dto, profile);

        // s3에 업로드 및 URL을 엔티티에 저장
        List<Certificate> certificates = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        String fileUrl = s3Service.uploadFile(file);
                        return Certificate.builder()
                                .fileUrl(fileUrl)
                                .build();
                    } catch (IOException e) {
                        throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
                    }
                })
                .toList();

        // 경력과 경력증명서 연관관계 설정
        certificates.forEach(career::addCertificate); // 경력에 경력증명서 추가
        // 경력 저장
        Career saved = careerRepository.save(career);

        // 경력 증명서 리스트 dto 만들기
        List<CertificateResponseDto> responseDto = CertificateResponseDto.fromList(
                saved.getCertificates());

        return CareerResponseDto.of(saved, responseDto);
    }

    // 경력 상세 페이지 조회
    @Transactional(readOnly = true)
    public FindCareerResponseDto findCareer(Long profileId, Long careerId) {

        // 경력 조회
        Career career = findByIdAndProfileId(careerId, profileId);

        List<CertificateResponseDto> list = career.getCertificates().stream()
                .map(CertificateResponseDto::from).toList();

        return FindCareerResponseDto.of(career, list);
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
    public void updateCareer(Long careerId, UpdateCareerRequestDto dto) {

        // 경력 조회
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CAREER));

        // 경력 수정
        career.updateCareer(dto);
    }

    // 경력 삭제
    @Transactional
    public void deleteCareer(Long careerId) {

        // 경력 조회
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CAREER));

        // s3에서 이미지 삭제
        career.getCertificates().forEach(certificate ->
            s3Service.deleteFile(certificate.getFileUrl())
        );

        // 경력 삭제
        careerRepository.delete(career);
    }

    // 경력 조회 시 null 이면 예외 발생
    public Career findByIdAndProfileId(Long careerId, Long profileId) {
        return careerRepository.findByIdAndProfileId(careerId, profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CAREER));
    }

    @Transactional(readOnly = true)
    public PagingResponse<CareersResponseDto> getCareers(Pageable pageable,
            UserDetailsImpl userDetails) {

        List<Profile> profiles = profileRepository.findByUserIdAndIsDeletedFalse(
                userDetails.getId());

        if (profiles.isEmpty()) {
            return PagingResponse.from(Page.empty());
        }

        // 프로필 id 추출
        List<Long> profileIds = profiles.stream().map(Profile::getId).toList();

        // 각 프로필에 해당되는 경력 목록 조회
        Page<Career> all = careerRepository.findAllByProfileIdsIn(profileIds, pageable);

        Page<CareersResponseDto> map = all.map(CareersResponseDto::of);

        return PagingResponse.from(map);
    }
}
