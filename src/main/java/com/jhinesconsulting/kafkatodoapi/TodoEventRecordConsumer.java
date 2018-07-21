package com.jhinesconsulting.kafkatodoapi;

import com.jhinesconsulting.TodoEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class TodoEventRecordConsumer {
    private KafkaConsumer kafkaConsumer;
    private TodoEventRecordStore todoEventRecordStore;

    @Autowired
    TodoEventReducer todoEventReducer;

    @Autowired
    AllTodosView allTodosView;

    @Autowired
    CompleteTodosView completeTodosView;

    public TodoEventRecordConsumer(KafkaConsumer kafkaConsumer, TodoEventRecordStore todoEventRecordStore) {
        this.kafkaConsumer = kafkaConsumer;
        this.todoEventRecordStore = todoEventRecordStore;
    }

    public void poll() {
        while (true) {
            System.out.println("Polling");

            ConsumerRecords<String, TodoEvent> records = kafkaConsumer.poll(1000);

            for (ConsumerRecord<String, TodoEvent> record : records) {
                TodoEvent todoEvent = record.value();
                System.out.println(record.partition() + "@" + record.offset() + " " + todoEvent);

                todoEventRecordStore.getTodoEvents().add(todoEvent);

                if(todoEvent.getAction().equals("CREATE")) {
                    Todo todo = new Todo();
                    todo.setId(todoEvent.getId());
                    todo.setCreated(todoEvent.getCreated());
                    todo.setTitle(todoEvent.getTitle());
                    todo.setActive(todoEvent.getActive());

                    allTodosView.getTodos().put(todoEvent.getId(), todo);
                }

                if(todoEvent.getAction().equals("UPDATE") || todoEvent.getAction().equals("TOGGLE")) {
                    // filter todos by id
                    List<TodoEvent> filteredTodoEventsById = todoEventRecordStore.getTodoEvents()
                            .stream()
                            .filter(t -> t.getId().equals(todoEvent.getId()))
                            .collect(Collectors.toList());

                    // reduce updated todo
                    Todo updatedTodo = todoEventReducer.reduce(filteredTodoEventsById);

                    // update todo in all todos view hashmap
                    allTodosView.getTodos().put(updatedTodo.getId(), updatedTodo);

                    // update todo in complete todos view hashmap
                    if(!updatedTodo.isActive()) {
                        completeTodosView.getTodos().put(updatedTodo.getId(), updatedTodo);
                    } else if(updatedTodo.isActive()) {
                        completeTodosView.getTodos().remove(updatedTodo.getId());
                    }
                }
            }

            kafkaConsumer.commitSync();
        }
    }
}
