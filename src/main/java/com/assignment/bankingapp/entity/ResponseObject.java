package com.assignment.bankingapp.entity;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseObject {
    private String message;
    private int status;
    private Object data;
}
