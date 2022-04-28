package me.liting.restapiwithspring.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // WEB과 관련된 bean 등록 조금더 빠름
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;    // web서버를 띄우지 않음 단위테스트보다는 느림  Repository용 빈 등록을 안함

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;//mock 으로 생성

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
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
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);//eventRepository 의 save 호출되면 event return;

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))//json 문자열 로 변환


        )
                .andDo(print()) // request 확인할수있음
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
        ; // result : post request 201
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
