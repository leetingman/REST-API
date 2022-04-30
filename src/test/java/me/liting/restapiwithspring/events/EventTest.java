package me.liting.restapiwithspring.events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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

    @ParameterizedTest
    @MethodSource("paramsForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event =Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build()
                ;
        //When
        event.update();
        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }


    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    public void testOffline(String location ,boolean isOffline){
        //Given
        Event event = Event.builder()
                .location(location)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isEqualTo(isOffline);

    }


    private static Stream<Arguments> paramsForTestFree(){
        return Stream.of(
                Arguments.of(0,0,true),
                Arguments.of(100,0,false),
                Arguments.of(0,100,false),
                Arguments.of(100,200,false)
        );
    }

    private static Stream<Arguments> paramsForTestOffline(){
        return Stream.of(
                Arguments.of("밤밭",true),
                Arguments.of(null,false),
                Arguments.of("  ",false)
        );
    }



//
//    private Object[] paramsForTestFree(){
//        return new Object[]{
//                new Object[]{0,0,true},
//                new Object[]{100,0,false},
//                new Object[]{0,100,false},
//                new Object[]{100,200,false}
//        };
//    }



}