package com.glmall.product.vo;

import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo{
    /**
     * 所属分类名
     */
    private String catelogName;
    /**
     * 所属分组名
     */
    private String groupName;

    /**
     * 分类路径
     */
    private Long[] catelogPath;
}
