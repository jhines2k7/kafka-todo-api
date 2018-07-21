package com.jhinesconsulting.kafkatodoapi;

import com.jhinesconsulting.TodoEvent;

import java.util.ArrayList;
import java.util.List;

public class TodoEventRecordStore {
    private List<TodoEvent> todoEvents = new ArrayList<>();

    public List<TodoEvent> getTodoEvents() {
        return this.todoEvents;
    }
}
