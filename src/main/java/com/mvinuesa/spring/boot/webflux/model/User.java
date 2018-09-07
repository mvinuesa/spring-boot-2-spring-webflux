package com.mvinuesa.spring.boot.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * User class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private String id;

    private String name;

}
