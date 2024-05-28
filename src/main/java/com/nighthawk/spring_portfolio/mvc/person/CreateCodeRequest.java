package com.nighthawk.spring_portfolio.mvc.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCodeRequest {
    private String email;
    private String className;
}
