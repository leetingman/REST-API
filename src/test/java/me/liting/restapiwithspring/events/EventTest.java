package me.liting.restapiwithspring.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {
    @Test
    public void builder(){
        Event event=Event.builder()
                .name("Spring REST ApI")
                .description("REST ApI dev with Spring")
                .build();
        assertThat(event).isNotNull();
    }
    @Test
    public void javaBean(){
        //command+option+v
        //Given
        String name = "Event";
        String description = "Spring";
        //When
        Event event=new Event();//생성된 Builder class는 public 이아
        event.setName(name);
        event.setDescription(description);
        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }


}