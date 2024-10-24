package org.slavbx.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CompletionDate {
    private Long id;
    private LocalDate date;
    private Habit habit;
}
