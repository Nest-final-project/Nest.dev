package caffeine.nest_dev.domain.profile.entity;

import aj.org.objectweb.asm.commons.Remapper;
import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.keyword.entity.ProfileKeyword;
import caffeine.nest_dev.domain.user.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String introduction;

    @Column(nullable = true)
    private String accountNumber;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileKeyword> profileKeywords = new ArrayList<>();

    @Column(nullable = false)
    private boolean isDeleted;

    private String imageUrl;


    public void updateProfile(String title, String introduction, String imageUrl, Category category) {
        this.title = title;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
