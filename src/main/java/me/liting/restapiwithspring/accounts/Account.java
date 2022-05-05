package me.liting.restapiwithspring.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;
    @ElementCollection(fetch = FetchType.EAGER) //다중 enum default fetchtype laze
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
