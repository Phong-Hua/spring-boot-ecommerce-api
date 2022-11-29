package com.example.demo.model.persistence;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item")
@EqualsAndHashCode
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	private @Getter @Setter Long id;
	
	@Column(nullable = false)
	@JsonProperty
	private @Getter @Setter String name;
	
	@Column(nullable = false)
	@JsonProperty
	private @Getter @Setter BigDecimal price;
	
	@Column(nullable = false)
	@JsonProperty
	private @Getter @Setter String description;

}
