package caffeine.nest_dev.domain.keyword.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "keywords")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileKeyword> profileKeywords = new ArrayList<>();

    public void update(String dtoName) {
        this.name = dtoName;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
