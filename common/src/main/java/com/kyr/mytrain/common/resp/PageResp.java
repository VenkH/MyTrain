package com.kyr.mytrain.common.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResp<T> implements Serializable {
    /**
     * 总条数
     */
    private Long total;

    /**
     * 当前页
     */
    private List<T> list;
}
