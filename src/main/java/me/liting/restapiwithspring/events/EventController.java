package me.liting.restapiwithspring.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value="/api/events",produces = MediaTypes.HAL_JSON_VALUE)//모든 파일들은 HAL JSON 으로 응답을 보냄
public class EventController {
//    @Autowired
//    EventRepository eventRepository;

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;


    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){

        if(errors.hasErrors()){
            //After Binding if has errors return badRequest
            return ResponseEntity.badRequest().build();
        }

        //eventDto의 값을 event 의 값으로 변경해야 eventRepository 사용가능
        //ModelMapper 라이브러리 사용하여 해결
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .build(); 이 과정을 생략하는 것
        Event event = modelMapper.map(eventDto,Event.class);//eventDto trans 4 event.class

        Event newEvent=this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();//p,nullpointE
        return ResponseEntity.created(createdUri).body(event);
    }
}
