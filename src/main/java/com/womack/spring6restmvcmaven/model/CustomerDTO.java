package com.womack.spring6restmvcmaven.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private Integer version;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
