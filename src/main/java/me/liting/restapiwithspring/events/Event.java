package me.liting.restapiwithspring.events;

import lombok.*;
import me.liting.restapiwithspring.accounts.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of="id")
@Entity
public class Event {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;//(optional)
    private int basePrice;//(optional)
    private int maxPrice;//(optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)//default Odiner 후에 데이터 순서 바뀌면 데이터 꼬이루수도 있음 String 선ho
    private EventStatus eventStatus= EventStatus.DRAFT;
    @ManyToOne
    private Account manager;

    public void update() {
        //Update free
        if(this.basePrice== 0 && this.maxPrice==0){
            this.free = true;
        }else{
            this.free = false;
        }
        //Update offline
        if(this.location == null ||this.location.isBlank()){
            this.offline= false;
        }
        else{
            this.offline = true;
        }

    }
}
