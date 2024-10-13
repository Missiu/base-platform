package com.example.template.util;

import com.example.template.common.base.CommonConstants;
import com.example.template.common.base.PageDTO;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义分页工具类
 *
 * @author hzh
 */
@Slf4j
public class PageUtils {

    /**
     * 默认分页查询
     */
    public static void buildPage() {
        PageDTO pageDTO = PageDTO.getDefaultPageDTO();
        buildPageWithOrder(pageDTO);
    }

    /**
     * 自定义分页查询，带 pageNum 和 pageSize 参数
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     */
    public static void buildPage(int pageNum, int pageSize) {
        PageDTO pageDTO = PageDTO.getDefaultPageDTO();
        // 调整分页大小
        pageDTO.setPageNum(pageNum);
        pageDTO.setPageSize(adjustPageSize(pageSize));
        buildPageWithOrder(pageDTO);
    }

    /**
     * 自定义分页查询，使用传入的 pageDTO
     *
     * @param pageDTO 自定义分页参数
     */
    public static void buildPage(PageDTO pageDTO) {
        if (ObjectUtils.isNotEmpty(pageDTO)) {
            pageDTO.setPageSize(adjustPageSize(pageDTO.getPageSize()));
            buildPageWithOrder(pageDTO);
        } else {
            buildPage();
        }
    }

    /**
     * 执行分页和排序逻辑
     *
     * @param pageDTO 分页参数
     */
    private static void buildPageWithOrder(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

        // 设置排序
        if (StringUtils.isNotBlank(pageDTO.getOrderField())) {
            String orderDirection = Boolean.TRUE.equals(pageDTO.getOrderByAsc())
                    ? CommonConstants.ORDER_BY_ASC
                    : CommonConstants.ORDER_BY_DESC;
            PageHelper.orderBy(pageDTO.getOrderField() + " " + orderDirection);
        } else {
            String defaultOrderDirection = Boolean.TRUE.equals(pageDTO.getOrderByAsc())
                    ? CommonConstants.ORDER_BY_ASC
                    : CommonConstants.ORDER_BY_DESC;
            PageHelper.orderBy(defaultOrderDirection);
        }
    }

    /**
     * 调整分页大小方法，处理 pageSize 合法性
     *
     * @param pageSize 页大小
     * @return 调整后的页大小
     */
    private static int adjustPageSize(int pageSize) {
        if (pageSize < 1) {
            // 使用默认分页大小
            log.warn("PageSize is less than 1, using default page size: {}", PageDTO.getDefaultPageDTO().getPageSize());
            return PageDTO.getDefaultPageDTO().getPageSize();
        } else if (pageSize > CommonConstants.MAX_PAGE_SIZE) {
            // 限制最大分页大小
            log.warn("PageSize is greater than {}, using max page size: {}", CommonConstants.MAX_PAGE_SIZE, CommonConstants.MAX_PAGE_SIZE);
            return CommonConstants.MAX_PAGE_SIZE.intValue();
        }
        return pageSize;
    }
}
