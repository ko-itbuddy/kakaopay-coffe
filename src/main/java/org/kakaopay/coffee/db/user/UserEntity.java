package org.kakaopay.coffee.db.user;


import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.kakaopay.coffee.api.user.PasswordConverter;
import org.kakaopay.coffee.api.user.request.UserSignUpServiceRequest;
import org.kakaopay.coffee.db.common.BaseEntity;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "TB_USER")
public class UserEntity extends BaseEntity implements Comparable<UserEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 30)
    private String name;

    @Column(length = 128)
    @Convert(converter = PasswordConverter.class)
    private String password;

    @Column
    @ColumnDefault("0")
    private Integer point = 0 ;

    @ToString.Exclude
    @OneToMany
    @JoinColumn(
        name = "userId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private SortedSet<UserPointHistoryEntity> userPointHistories = new TreeSet<>();

    @Builder
    private UserEntity(Long id, String phone, String name, String password, Integer point) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.point = point == null ? 0 : point;
    }

    public static UserEntity of(UserSignUpServiceRequest request) {
        return UserEntity.builder()
                         .phone(request.getPhone())
                         .name(request.getName())
                         .password(request.getPassword())
                         .build();
    }

    @Override
    public int compareTo(UserEntity o) {
        return 1;
    }
}
