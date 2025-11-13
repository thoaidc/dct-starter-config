package com.dct.config.common;

import com.dct.model.dto.request.BaseRequestDTO;
import com.dct.model.exception.BaseIllegalArgumentException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

@SuppressWarnings("unused")
public class SqlUtils {
    private static final Logger log = LoggerFactory.getLogger(SqlUtils.class);
    private static final String ENTITY_NAME = "com.dct.config.common.DataUtils";

    public static void appendDateCondition(StringBuilder sql,
                                           Map<String, Object> params,
                                           BaseRequestDTO request,
                                           String columnName) {
        Instant instantStart = request.getFromInstantSearch();
        Instant instantEnd = request.getToInstantSearch();

        if (Objects.nonNull(instantStart) && Objects.nonNull(instantEnd)) {
            sql.append(" AND ").append(columnName).append(" BETWEEN :fromDate AND :toDate");
            params.put("fromDate", instantStart);
            params.put("toDate", instantEnd);
        } else if (Objects.nonNull(instantStart)) {
            sql.append(" AND ").append(columnName).append(" >= :fromDate");
            params.put("fromDate", instantStart);
        } else if (Objects.nonNull(instantEnd)) {
            sql.append(" AND ").append(columnName).append(" <= :toDate");
            params.put("toDate", instantEnd);
        }
    }

    public static void appendSqlEqualCondition(StringBuilder sql,
                                               Map<String, Object> params,
                                               String columnName,
                                               Object value) {
        if (Objects.nonNull(value)) {
            sql.append(" AND ").append(columnName).append(" = :").append(columnName);
            params.put(columnName, value);
        }
    }

    public static void appendSqlGreaterThanCondition(StringBuilder sql,
                                                     Map<String, Object> params,
                                                     String columnName,
                                                     Object value) {
        if (Objects.nonNull(value)) {
            sql.append(" AND ").append(columnName).append(" > :").append(columnName);
            params.put(columnName, value);
        }
    }

    public static void appendSqlGreaterThanEqualCondition(StringBuilder sql,
                                                          Map<String, Object> params,
                                                          String columnName,
                                                          Object value) {
        if (Objects.nonNull(value)) {
            sql.append(" AND ").append(columnName).append(" >= :").append(columnName);
            params.put(columnName, value);
        }
    }

    public static void appendSqlLessThanCondition(StringBuilder sql,
                                                  Map<String, Object> params,
                                                  String columnName,
                                                  Object value) {
        if (Objects.nonNull(value)) {
            sql.append(" AND ").append(columnName).append(" < :").append(columnName);
            params.put(columnName, value);
        }
    }

    public static void appendSqlLessThanEqualCondition(StringBuilder sql,
                                                       Map<String, Object> params,
                                                       String columnName,
                                                       Object value) {
        if (Objects.nonNull(value)) {
            sql.append(" AND ").append(columnName).append(" =< :").append(columnName);
            params.put(columnName, value);
        }
    }

    public static void appendSqlBetweenCondition(StringBuilder sql,
                                                 Map<String, Object> params,
                                                 String columnName,
                                                 Object startValue,
                                                 Object endValue) {
        if (Objects.nonNull(startValue) && Objects.nonNull(endValue)) {
            sql.append(" AND ")
                .append(columnName)
                .append(" BETWEEN :")
                .append(columnName)
                .append("Start AND :")
                .append(columnName)
                .append("End");
            params.put(columnName + "Start", startValue);
            params.put(columnName + "End", endValue);
        } else if (Objects.nonNull(startValue)) {
            appendSqlGreaterThanCondition(sql, params, columnName, startValue);
        } else if (Objects.nonNull(endValue)) {
            appendSqlLessThanCondition(sql, params, columnName, endValue);
        }
    }

    public static void appendSqlInCondition(StringBuilder sql,
                                            Map<String, Object> params,
                                            String columnName,
                                            Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            sql.append(" AND ").append(columnName).append(" IN (:").append(columnName).append("List)");
            params.put(columnName + "List", values);
        }
    }

    public static void appendSqlLikeCondition(StringBuilder sql,
                                              Map<String, Object> params,
                                              String columnName,
                                              String value) {
        if (StringUtils.hasText(value)) {
            sql.append(" AND ").append(columnName).append(" LIKE :").append(columnName);

            if (value.startsWith("%")) {
                params.put(columnName, value);
            } else {
                params.put(columnName, "%" + value + "%");
            }
        }
    }

    public static void setPageable(Query query, Pageable pageable) {
        if (query == null || pageable == null)
            return;

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int firstResult = pageNumber * pageSize; // offset
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);
    }

    public static void setParams(Query query, Map<String, Object> params) {
        if (query == null || params == null || params.isEmpty())
            return;

        params.replaceAll((key, value) -> value != null ? value : "");
        params.forEach(query::setParameter);
    }

    public static void replaceWhere(@NotNull StringBuilder query) {
        String toReplace = "WHERE 1=1 AND";
        int index = query.indexOf("WHERE 1=1 AND");

        if (index > 0) {
            query.replace(index, index + toReplace.length(), "WHERE ");
        } else {
            toReplace = "WHERE 1=1";
            index = query.indexOf(toReplace);

            if (index > 0) {
                query.replace(index, index + toReplace.length(), " ");
            }
        }
    }

    public static QueryBuilder queryBuilder(EntityManager entityManager) {
        return new QueryBuilder(entityManager);
    }

    public static class QueryBuilder {
        private final EntityManager entityManager;
        private String querySql;
        private String countQuerySql;
        private Pageable pageable;
        private Map<String, Object> params = new HashMap<>();

        public QueryBuilder(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        public QueryBuilder querySql(String querySql) {
            if (StringUtils.hasText(querySql)) {
                this.querySql = querySql;
                return this;
            }

            throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must not be null or empty!");
        }

        public QueryBuilder countQuerySql(String countQuerySql) {
            if (StringUtils.hasText(countQuerySql)) {
                this.countQuerySql = countQuerySql;
                return this;
            }

            throw new BaseIllegalArgumentException(ENTITY_NAME, "Count query SQL must not be null or empty!");
        }

        public QueryBuilder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public QueryBuilder params(Map<String, Object> params) {
            if (Objects.nonNull(params)) {
                this.params = params;
            }

            return this;
        }

        public <T> List<T> getResultsWithDTO(String mappingName) {
            if (!StringUtils.hasText(querySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must be set before execution");
            }

            Query query = entityManager.createNativeQuery(querySql, mappingName);
            params.forEach(query::setParameter);

            if (Objects.nonNull(pageable) && pageable.getOffset() >= 0L && pageable.getPageSize() > 0) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
            }

            //noinspection unchecked
            return (List<T>) query.getResultList();
        }

        public <T> Optional<T> getSingleResultWithDTO(String mappingName) {
            if (!StringUtils.hasText(querySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must be set before execution");
            }

            Query query = entityManager.createNativeQuery(querySql, mappingName);
            params.forEach(query::setParameter);

            try {
                //noinspection unchecked
                return Optional.of((T) query.getSingleResult());
            } catch (NoResultException e) {
                log.debug("[QUERY_RESULT_SET_ERROR] - No result found for query: {}", querySql);
            }

            return Optional.empty();
        }

        public long countTotalRecords() {
            if (!StringUtils.hasText(countQuerySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Count query SQL must be set before execution");
            }

            Query countQuery = entityManager.createNativeQuery(countQuerySql);
            params.forEach(countQuery::setParameter);

            try {
                Object totalRecords = countQuery.getSingleResult();
                return Objects.nonNull(totalRecords) ? ((Number) totalRecords).longValue() : 0;
            } catch (Exception e) {
                log.error("[QUERY_COUNT_ERROR] - Could not execute count query: {}", e.getMessage());
            }

            return 0L;
        }
    }
}
