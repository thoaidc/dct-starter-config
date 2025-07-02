package com.dct.base.common;

import com.dct.model.constants.BaseDatetimeConstants;
import com.dct.model.dto.response.AuditingEntityDTO;
import com.dct.model.common.DateUtils;
import com.dct.base.entity.AbstractAuditingEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Common {

    private static final Logger log = LoggerFactory.getLogger(Common.class);
    private static final String ENTITY_NAME = "Common";

    public static void setAuditingInfo(AbstractAuditingEntity entity, AuditingEntityDTO auditingDTO) {
        auditingDTO.setCreatedByStr(entity.getCreatedBy());
        auditingDTO.setLastModifiedByStr(entity.getLastModifiedBy());

        try {
            String createdDate = DateUtils.ofInstant(entity.getCreatedDate())
                    .toString(BaseDatetimeConstants.Formatter.DD_MM_YYYY_HH_MM_SS_DASH);

            String lastModifiedDate = DateUtils.ofInstant(entity.getLastModifiedDate())
                    .toString(BaseDatetimeConstants.Formatter.DD_MM_YYYY_HH_MM_SS_DASH);

            auditingDTO.setCreatedDateStr(createdDate);
            auditingDTO.setLastModifiedDateStr(lastModifiedDate);
        } catch (Exception e) {
            log.error("[{}] - Could not set entity auditing info. {}", ENTITY_NAME, e.getMessage());
        }
    }
}
