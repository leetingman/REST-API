package me.liting.restapiwithspring.events;

import com.fasterxml.jackson.core.JsonParser;
import me.liting.restapiwithspring.accounts.Account;
import me.liting.restapiwithspring.accounts.AccountRepository;
import me.liting.restapiwithspring.accounts.AccountRole;
import me.liting.restapiwithspring.accounts.AccountService;
import me.liting.restapiwithspring.common.BaseControllerTest;
import me.liting.restapiwithspring.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    @Before
    public void setUp(){
        this.accountRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        relaxedResponseFields(
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

    private String getBearerToken() throws Exception {
        return "Bearer"+getAccessToken();
    }

    private String getAccessToken() throws Exception {
        //Given
        String username = "sts@gmail.com";
        String password = "liting";
        Account liting = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(liting);

        String clientId="myApp";
        String clientSecret="pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        );
        var responseBody=perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser= new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }
    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception{
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);
        //When
        ResultActions perform = this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        );
        //Then
                perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @Test
    @TestDescription("control event")
    public void getEvent()throws Exception{
        //Given
        Event event=this.generateEvent(100);
        //When&Then
        this.mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                ;



    }

    @Test
    @TestDescription("dose not exist event 4040")
    public void getEvent404() throws Exception{
        //When&Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("Update Event")
    public void updateEvent() throws Exception{
        //Given
        Event event= this.generateEvent(200);
        EventDto eventDto=this.modelMapper.map(event,EventDto.class);
        String eventName = "Update Event";
        eventDto.setName(eventName);

        //When
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;
    }

    @Test
    @TestDescription("fail to Update Event with empty Data")
    public void updateEvent400_Empty() throws Exception{
        //Given
        Event event= this.generateEvent(200);
        EventDto eventDto=new EventDto();

        //When and Then
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("fail to Update Event with wrong Data")
    public void updateEvent400_Wrong() throws Exception{
        //Given
        Event event= this.generateEvent(200);
        EventDto eventDto=modelMapper.map(event,EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //When and Then
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("fail to Update Event with dosent exist event")
    public void updateEvent404() throws Exception{
        Event event= this.generateEvent(200);
        EventDto eventDto= this.modelMapper.map(event,EventDto.class);
        //When and Then
        this.mockMvc.perform(put("/api/events/100901")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private Event generateEvent(int index) {
        Event event= Event.builder()
                .name("event"+index)
                .description("test event")
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
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build()
                ;
        return this.eventRepository.save(event);
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
