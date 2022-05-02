package me.liting.restapiwithspring.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.liting.restapiwithspring.common.RestDocsConfiguration;
import me.liting.restapiwithspring.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest // WEB과 관련된 bean 등록 조금더 빠름

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs//RestDocs
@Import(RestDocsConfiguration.class)//RestDocsConfiguration설정파일
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;    // web서버를 띄우지 않음 단위테스트보다는 느림  Repository용 빈 등록을 안함

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
//    EventRepository eventRepository;//mock 으로 생성
// api 입력값 이외에 에러발생 BadRequest로 응당 vs 받기로 한 값 이외는 무
    @Test
    @TestDescription("Trying to create new event with correct data.")
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
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"
                        ,links(
                                //link 정보 문서
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to update an existing event")
                ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of events"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of events"),
                                fieldWithPath("beginEventDateTime").description("date time of close of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Loction header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                //relaxed 가급적 쓰지말것
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of events"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of events"),
                                fieldWithPath("beginEventDateTime").description("date time of close of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")

                        )


        ))//name:create-event+ snippet
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
