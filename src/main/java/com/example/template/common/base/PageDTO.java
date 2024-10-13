package com.example.template.common.base;

import lombok.Data;

/**
 * @author hzh
 * @data 2024/10/13 16:20
 */
@Data
public class PageDTO {
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 排序方式
     */
    private Boolean orderByAsc;

    /**
     * 获取默认分页参数
     *
     * @return 分页参数
     */
    public static PageDTO getDefaultPageDTO() {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPageNum(CommonConstants.PAGE_NUM);
        pageDTO.setPageSize(CommonConstants.PAGE_SIZE);
        pageDTO.setOrderByAsc(true);
        return pageDTO;
    }
}
