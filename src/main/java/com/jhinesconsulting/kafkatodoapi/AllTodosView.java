package com.jhinesconsulting.kafkatodoapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter @Setter
public class AllTodosView {
    private HashMap<String, Todo> todos = new HashMap<>();
}
