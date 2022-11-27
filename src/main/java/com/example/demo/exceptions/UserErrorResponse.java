package com.example.demo.exceptions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserErrorResponse {

	private @Getter @Setter long timestamp;
	private @Getter @Setter int status;
	private @Getter @Setter String message;
}
