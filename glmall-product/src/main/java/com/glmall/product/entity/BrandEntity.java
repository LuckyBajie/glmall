package com.glmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.glmall.common.validator.annotations.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名不能为空")
    private String name;
    /**
     * 品牌logo地址
     */
    @NotBlank(message = "品牌logo地址不能为空")
    @URL(message = "品牌logo地址必须是一个url地址")
    private String logo;
    /**
     * 介绍
     */
    @NotBlank(message = "介绍不能为空")
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(message = "显示状态只能是：0-不显示；1-显示")
    @ListValue(value = {0,1}, message = "显示状态只能取值为0或1")
    private Integer showStatus;
    /**
     * 检索首字母
     * 在java中编写正则表达式的时候，前后的 "/" 是不需要的
     */
    @NotBlank(message = "检索首字母不能为空")
     @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母仅能是单个字母")
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序字段不能为空")
    @Min(0)
    private Integer sort;

}
