package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ModifyCartRequest {
	
	@JsonProperty
	private @Getter @Setter String username;
	
	@JsonProperty
	private @Getter @Setter long itemId;
	
	@JsonProperty
	private @Getter @Setter int quantity;

	public ModifyCartRequest(String username, long itemId, int quantity) {
		this.username = username;
		this.itemId = itemId;
		this.quantity = quantity;
	}
}
