package me.liting.restapiwithspring.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.liting.restapiwithspring.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest // WEB과 관련된 bean 등록 조금더 빠름

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;    // web서버를 띄우지 않음 단위테스트보다는 느림  Repository용 빈 등록을 안함

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
//    EventRepository eventRepository;//mock 으로 생성
// api 입력값 이외에 에러발생 BadRequest로 응당 vs 받기로 한 값 이외는 무
    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스t")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 12 ,22,14 ,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 12 ,27,14 ,21))
                .beginEventDateTime(LocalDateTime.of(2021, 12 ,28,14 ,21))
                .endEventDateTime(LocalDateTime.of(2021, 12 ,29,14 ,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("밤밭1818")
                .build();
//        Mockito.when(eventRepository.save(event)).thenReturn(event);
        //eventRepository 의 save 호출될때 event object를 받은경우에
        // event return; EventController 에서 만든 객체는 새로만든 객체임 그래서 목킹적용 x

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))//json 문자열 로 변환

        )
                .andDo(print()) // request 확인할수있음
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))//test
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))//test
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ; // result : post request 201
    }
    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 발생하는 이벤트 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 12 ,22,14 ,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 12 ,27,14 ,21))
                .beginEventDateTime(LocalDateTime.of(2021, 12 ,28,14 ,21))
                .endEventDateTime(LocalDateTime.of(2021, 12 ,29,14 ,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("밤밭1818")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();
//        Mockito.when(eventRepository.save(event)).thenReturn(event);
        //eventRepository 의 save 호출될때 event object를 받은경우에
        // event return; EventController 에서 만든 객체는 새로만든 객체임 그래서 목킹적용 x

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))//json 문자열 로 변환

        )
                .andDo(print()) // request 확인할수있음
                .andExpect(status().isBadRequest())//bad Request  deserialization json  -> object ,unknown properties
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {

        EventDto eventDto= EventDto.builder().build();//input value dose n otexist   -> @Vaild BindingResult

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andExpect(status().isBadRequest())
        ;
    }

    /**
     * 입력 값이 잘못된 경우에 에러가 발생하는 테스트
     * @throws Exception
     */
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_input() throws Exception {
        //anotaion 으로 검증하기 어렵d
        EventDto eventDto= EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 12 ,23,14 ,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 12 ,21,14 ,21))
                .beginEventDateTime(LocalDateTime.of(2021, 12 ,24,14 ,21))
                .endEventDateTime(LocalDateTime.of(2021, 12 ,23,14 ,21))
                .basePrice(10000)//base >max 일
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("밤밭1818")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }

}

/*
    test result:
    Status = 201
    Error message = null
          Headers = [Location:"http://localhost/api/events/%257Bid%257D"]
     Content type = null
             Body =
    Forwarded URL = null
   Redirected URL = http://localhost/api/events/%257Bid%257D
          Cookies = []
*/
