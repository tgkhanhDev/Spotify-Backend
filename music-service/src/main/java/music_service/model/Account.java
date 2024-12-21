package music_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {

    @Id
    @UuidGenerator
    UUID id;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "gender", nullable = false)
    boolean gender;

    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @Column(name = "nickname", nullable = false)
    String nickName;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "avatar", nullable = false)
    String avatar;

    @Column(name = "issubcribe", nullable = false)
    boolean isSubcribe;
}
