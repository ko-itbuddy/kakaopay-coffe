package org.kakaopay.coffee.api.user;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
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
import org.kakaopay.coffee.api.common.BaseEntity;
import org.kakaopay.coffee.api.user.request.UserSignUpServiceRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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


    @ToString.Exclude
    @OneToMany
    @JoinColumn(
        name = "userId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private SortedSet<UserPointHistoryEntity> userPointHistories = new TreeSet<>();

    @Builder
    private UserEntity(String phone, String name, String password,
        SortedSet<UserPointHistoryEntity> userPointHistories) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.userPointHistories = userPointHistories;
    }

    public static UserEntity of(UserSignUpServiceRequest request) {
        return UserEntity.builder()
                         .phone(request.getPhone())
                         .name(request.getName())
                         .password(request.getPassword())
                         .build();
    }

    public Integer getPointSum() {
        return this.getUserPointHistories()
                   .stream()
                   .map(UserPointHistoryEntity::getPoint)
                   .reduce(0, Integer::sum);
    }

    @Override
    public int compareTo(UserEntity o) {
        return 1;
    }
}
