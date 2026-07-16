package com.yukiani.server.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * 动画在原文、罗马字、英文和中文语种下的标题集合。
 */
@Data
@Embeddable
public class AnimeTitles {
    String titleNative;
    String titleRomaji;
    String titleEn;
    String titleCn;

    /**
     * 使用另一组标题补齐当前为空的标题，不覆盖已有值。
     */
    public void merge(AnimeTitles b) {
        titleNative = titleNative != null ? titleNative : b.titleNative;
        titleRomaji = titleRomaji != null ? titleRomaji : b.titleRomaji;
        titleEn     = titleEn     != null ? titleEn     : b.titleEn;
        titleCn     = titleCn     != null ? titleCn     : b.titleCn;
    }
}
