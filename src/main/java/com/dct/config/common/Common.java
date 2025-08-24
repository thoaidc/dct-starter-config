package com.dct.config.common;

import com.dct.config.entity.AbstractAuditingEntity;
import com.dct.model.constants.BaseDatetimeConstants;
import com.dct.model.dto.response.AuditingEntityDTO;
import com.dct.model.common.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Common {

    private static final Logger log = LoggerFactory.getLogger(Common.class);

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
            log.error("[SET_AUDITING_INFO_ERROR] - Could not set entity auditing info. {}", e.getMessage());
        }
    }
}
