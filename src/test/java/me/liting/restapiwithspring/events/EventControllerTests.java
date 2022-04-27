package me.liting.restapiwithspring.events;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest // WEB과 관련된 bean 등록 조금더 빠름
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;    // web서버를 띄우지 않음 단위테스트보다는 느림

    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)

        )
                .andExpect(status().isCreated()); // result : post request 201
    }


}
