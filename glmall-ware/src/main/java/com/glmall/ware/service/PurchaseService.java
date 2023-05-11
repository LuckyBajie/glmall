package com.glmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.ware.entity.PurchaseEntity;
import com.glmall.ware.vo.MergeVo;
import com.glmall.ware.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 22:36:12
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceiveList(Map<String, Object> params);

    void mergePurchase(MergeVo vo);

    void receivePurchase(List<Long> ids);

    void done(PurchaseFinishVo finishVo);
}

