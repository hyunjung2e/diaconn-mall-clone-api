package com.diaconn_mall.website.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequestDto {
    private Long id;
    private Long userId;
    private Long productId;
    private int count;
}

