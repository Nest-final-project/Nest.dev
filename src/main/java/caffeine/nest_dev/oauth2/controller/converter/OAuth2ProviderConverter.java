package caffeine.nest_dev.oauth2.controller.converter;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.user.enums.SocialType;
import org.springframework.core.convert.converter.Converter;

public class OAuth2ProviderConverter implements Converter<String, SocialType> {

    @Override
    public SocialType convert(String source) {
        try {
            return SocialType.valueOf(source.toUpperCase());
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INVALID_SOCIAL_TYPE);
        }
    }
}
