package com.mvinuesa.spring.boot.webflux.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * User Event class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEvent {

    private User user;
    private Date when;
    private String type;

}
