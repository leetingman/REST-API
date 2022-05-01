package me.liting.restapiwithspring.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
// BeanSerializer
//public class EventResource extends RepresentationModel {
//    @JsonUnwrapped// unwrapped event
//    private Event event;
//
//    public EventResource(Event event) {
//        this.event = event;
//    }
//
//    public Event getEvent() {
//        return event;
//    }
//}


public class EventResource extends EntityModel<Event>{
    public EventResource(Event event, Link... links){
        super(event, Arrays.asList(links));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
//        add(new Link("https://localhost:8080/api/events/"+event.getId())); is not typesafe

    }
}