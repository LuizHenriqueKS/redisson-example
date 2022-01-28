package com.zul.redisson.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserOrder {

    private int id;
    private Category category;

}
