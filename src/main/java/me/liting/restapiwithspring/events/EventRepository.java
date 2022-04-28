package me.liting.restapiwithspring.events;

import org.springframework.data.jpa.repository.JpaRepository;

//JPA Repository
public interface EventRepository extends JpaRepository<Event,Integer> {//Entity


}
