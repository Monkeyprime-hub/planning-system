package com.akvelon.planningsystem.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    Long id;
    String name;
    String description;
    LocalDate createdDate;
    Status status;
    Integer priority;


    public static Task makeDefault(String name, String description,
                                   LocalDate createdDate, Status status, Integer priority) {
        return builder()
                .name(name)
                .description(description)
                .createdDate(LocalDate.now())
                .status(Status.TODO)
                .priority(1)
                .build();

    }

}
