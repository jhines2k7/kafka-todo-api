package com.jhinesconsulting.kafkatodoapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
public class Todo {
    @JsonIgnore
    private String id;

    @JsonIgnore
    private Long created;

    @JsonIgnore
    private boolean cleared;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean active;
}
