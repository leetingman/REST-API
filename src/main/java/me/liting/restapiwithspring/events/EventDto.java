package me.liting.restapiwithspring.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
//입력받는 Dto
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {
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
}

//Id 나 free값 안받고 받기로 한 값만 받을수있음
