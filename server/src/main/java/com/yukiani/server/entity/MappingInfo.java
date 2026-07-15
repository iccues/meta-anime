package com.yukiani.server.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 外部平台提供的动画基础信息快照。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MappingInfo {
    @Embedded
    AnimeTitles title;
    String coverImage;
    LocalDate startDate;
}
