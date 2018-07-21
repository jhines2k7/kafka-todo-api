package com.jhinesconsulting.kafkatodoapi;

import com.jhinesconsulting.TodoEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoEventReducer {
    public Todo reduce(List<TodoEvent> todos) {
        Todo todo = new Todo();

        for(TodoEvent todoEvent : todos) {
            if(todoEvent.getAction().equals("UPDATE")) {
                todo.setTitle(todoEvent.getTitle());
            } else if(todoEvent.getAction().equals("CREATE")) {
                todo.setId(todoEvent.getId());
                todo.setCreated(todoEvent.getCreated());
                todo.setTitle(todoEvent.getTitle());
                todo.setActive(todoEvent.getActive());
            } else if(todoEvent.getAction().equals("TOGGLE")) {
                todo.setActive(todoEvent.getActive());
            } else if(todoEvent.getAction().equals("CLEAR")) {
                todo.setCleared(true);
            }
        }

        return todo;
    }
}
