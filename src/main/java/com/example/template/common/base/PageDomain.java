package com.example.template.common.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hzh
 * @data 2024/10/13 16:20
 */
@Data
public class PageDomain implements Serializable {
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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 获取默认分页参数
     *
     * @return 分页参数
     */
    public static PageDomain getDefaultPageDTO() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(CommonConstants.PAGE_NUM);
        pageDomain.setPageSize(CommonConstants.PAGE_SIZE);
        pageDomain.setOrderByAsc(true);
        return pageDomain;
    }


}
