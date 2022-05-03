package me.liting.restapiwithspring.events;

import me.liting.restapiwithspring.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final EventValidator eventValidator;


    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){

        if(errors.hasErrors()){
            //After Binding if has errors return badRequest
            //extract method option+command+M
            return badRequest(errors);
        }

        eventValidator.validate(eventDto,errors);


        if(errors.hasErrors()){
            //After Binding if has errors return badRequest
            return badRequest(errors);
        }
        //errors 는 json 으로 변환 불가
        //event object 는 javabean 스펙을 따름

        //eventDto의 값을 event 의 값으로 변경해야 eventRepository 사용가능
        //ModelMapper 라이브러리 사용하여 해결
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .build(); 이 과정을 생략하는 것
        Event event = modelMapper.map(eventDto,Event.class);//eventDto trans 4 event.class
        event.update();
        Event newEvent=this.eventRepository.save(event);

//        ControllerLinkBuilder now is WebMvcLinkBuilder
//        add link
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();//p,nullpointE
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
//        eventResource.add(selfLinkBuilder.withSelfRel()); EventResource 에서 직접 넣어줌
        eventResource.add(selfLinkBuilder.withRel("update-event"));//rel href
        eventResource.add(Link.of("/docs/index.html#resource-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }


    /**
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedModel=assembler.toModel(page,entity -> new EventResource(entity));
        //add link to profile
        pagedModel.add(Link.of("/docs/index.html#resource-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModel);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }
}
