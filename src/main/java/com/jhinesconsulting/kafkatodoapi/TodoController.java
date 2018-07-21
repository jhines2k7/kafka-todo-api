package com.jhinesconsulting.kafkatodoapi;

import com.jhinesconsulting.TodoEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
public class TodoController {
    @Autowired
    private TodoEventRecordStore todoEventRecordStore;

    @Autowired
    private TodoEventRecordProducer todoEventRecordProducer;

    TodoEvent.Builder todoEventBuilder = TodoEvent.newBuilder();

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    AllTodosView allTodosView;

    @Autowired
    CompleteTodosView completeTodosView;

    @Autowired
    TodoEventReducer todoEventReducer;

    @PostMapping
    ResponseEntity<?> add(@RequestBody Todo todo) {
        String uuid = UUID.randomUUID().toString();
        String todoId = uuid.substring(0, Math.min(uuid.length(), 8));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        todo.setId(todoId);
        todo.setCreated(timestamp.getTime());

        todoEventBuilder.setEventId(UUID.randomUUID().toString());
        todoEventBuilder.setId(todoId);
        todoEventBuilder.setAction("CREATE");
        todoEventBuilder.setCreated(todo.getCreated());
        todoEventBuilder.setTitle(todo.getTitle());

        todoEventRecordProducer.send(
            new ProducerRecord<String, TodoEvent>(applicationProperties.getTopic(), todoEventBuilder.build()));

        allTodosView.getTodos().put(todoId, todo);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(todoId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    ResponseEntity<List<Todo>> getAll() {
        List<Todo> allTodos = new ArrayList<>(allTodosView.getTodos().values())
                .stream()
                .filter(t -> !t.isCleared())
                .collect(Collectors.toList());

        return ResponseEntity.ok(allTodos);
    }

    @GetMapping("/completed")
    ResponseEntity<List<Todo>> getCompleted() {
        List<Todo> completedTodos = new ArrayList<>(completeTodosView.getTodos().values())
                .stream()
                .filter(t -> !t.isCleared())
                .collect(Collectors.toList());

        return ResponseEntity.ok(completedTodos);
    }

    @PutMapping("/{todoId}")
    ResponseEntity<?> put(@PathVariable String todoId, @RequestBody Todo todo) {
        // store event in kafka
        todoEventBuilder.setEventId(UUID.randomUUID().toString());
        todoEventBuilder.setAction("UPDATE");
        todoEventBuilder.setId(todoId);
        todoEventBuilder.setTitle(todo.getTitle());
        todoEventBuilder.setCreated(new Timestamp(System.currentTimeMillis()).getTime());

        todoEventRecordProducer.send(
                new ProducerRecord<String, TodoEvent>(applicationProperties.getTopic(), todoEventBuilder.build()));

        return ResponseEntity.ok().build();
    }

    @PutMapping("/toggle-complete/{todoId}")
    ResponseEntity<?> toggle(@PathVariable String todoId) {
        List<TodoEvent> filteredTodoEventsById = todoEventRecordStore.getTodoEvents()
                .stream()
                .filter(t -> t.getId().equals(todoId))
                .collect(Collectors.toList());

        Todo todo = todoEventReducer.reduce(filteredTodoEventsById);

        todoEventBuilder.setEventId(UUID.randomUUID().toString());
        todoEventBuilder.setAction("TOGGLE");
        todoEventBuilder.setId(todoId);
        todoEventBuilder.setTitle("");
        todoEventBuilder.setActive(!todo.isActive());
        todoEventBuilder.setCreated(new Timestamp(System.currentTimeMillis()).getTime());

        todoEventRecordProducer.send(
                new ProducerRecord<String, TodoEvent>(applicationProperties.getTopic(), todoEventBuilder.build()));

        return ResponseEntity.ok().build();
    }

    @PutMapping("/clear-completed")
    ResponseEntity<?> clearCompleted() {
        for(Todo todo : allTodosView.getTodos().values()) {
            if(!todo.isActive()) {
                todoEventBuilder.setEventId(UUID.randomUUID().toString());
                todoEventBuilder.setAction("CLEAR");
                todoEventBuilder.setId(todo.getId());
                todoEventBuilder.setTitle("");
                todoEventBuilder.setCleared(true);
                todoEventBuilder.setCreated(new Timestamp(System.currentTimeMillis()).getTime());

                todoEventRecordProducer.send(
                        new ProducerRecord<String, TodoEvent>(applicationProperties.getTopic(), todoEventBuilder.build()));
            }
        }

        return ResponseEntity.ok().build();
    }
}
