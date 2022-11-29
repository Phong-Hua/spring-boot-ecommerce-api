package com.example.demo.exceptions;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class APIErrorResponseImpl implements APIErrorResponse {

    private @Getter @Setter String message;
    private @Getter @Setter int status;
    private @Getter @Setter long timestamp;
}
