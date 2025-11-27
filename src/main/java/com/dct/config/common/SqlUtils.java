package com.dct.config.common;

import com.dct.model.dto.request.BaseRequestDTO;
import com.dct.model.exception.BaseIllegalArgumentException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public class SqlUtils {
    private static final Logger log = LoggerFactory.getLogger(SqlUtils.class);
    private static final String ENTITY_NAME = "com.dct.config.common.DataUtils";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String START = "Start";
    public static final String END = "End";
    public static final String PERCENT = "%";
    public static final String LIKE = " LIKE :";
    public static final String LIKE_SUFFIX = "Like";
    public static final String LIST = "List";
    public static final String WHERE = "WHERE ";
    public static final String WHERE_DEFAULT = "WHERE 1=1";
    public static final String WHERE_AND = "WHERE 1=1 AND";
    public static final String OR = " OR ";
    public static final String AND = " AND ";
    public static final String AND_PARAM = " AND :";
    public static final String BETWEEN = " BETWEEN :";
    public static final String EQUALS = " = :";
    public static final String NOT_EQUALS = " <> :";
    public static final String GREATER_THAN = " > :";
    public static final String GREATER_THAN_OR_EQUAL = " >= :";
    public static final String LESS_THAN = " < :";
    public static final String LESS_THAN_OR_EQUAL = " <= :";
    public static final String IN = " IN (:";
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String DESC = " DESC";

    public static <T extends BaseRequestDTO> void addDateTimeCondition(
        StringBuilder sql,
        Map<String, Object> params,
        T request,
        String column
    ) {
        addBetweenCondition(sql, params, column, request.getFromInstantSearch(), request.getToInstantSearch());
    }

    public static void addEqualCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, EQUALS, column, value);
    }

    public static void addNotEqualCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, NOT_EQUALS, column, value);
    }

    public static void addGreaterThanCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, GREATER_THAN, column, value);
    }

    public static void addGreaterThanOrEqualCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, GREATER_THAN_OR_EQUAL, column, value);
    }

    public static void addLessThanCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, LESS_THAN, column, value);
    }

    public static void addLessThanOrEqualCondition(StringBuilder sql, Map<String, Object> params, String column, Object value) {
        addSqlSingleCondition(sql, params, LESS_THAN_OR_EQUAL, column, value);
    }

    private static void addSqlSingleCondition(
        StringBuilder sql,
        Map<String, Object> params,
        String condition,
        String column,
        Object value
    ) {
        if (Objects.nonNull(value)) {
            sql.append(AND).append(column).append(condition).append(column);
            params.put(column, value);
        }
    }

    public static void addBetweenCondition(
        StringBuilder sql,
        Map<String, Object> params,
        String column,
        Object startValue,
        Object endValue
    ) {
        if (Objects.nonNull(startValue) && Objects.nonNull(endValue)) {
            sql.append(AND).append(column)
                .append(BETWEEN).append(column).append(START)
                .append(AND_PARAM).append(column).append(END);
            params.put(column + START, startValue);
            params.put(column + END, endValue);
        } else if (Objects.nonNull(startValue)) {
            addGreaterThanCondition(sql, params, column, startValue);
        } else if (Objects.nonNull(endValue)) {
            addLessThanCondition(sql, params, column, endValue);
        }
    }

    public static void addInCondition(StringBuilder sql, Map<String, Object> params, String column, Collection<?> values) {
        if (Objects.nonNull(values) && !values.isEmpty()) {
            sql.append(AND).append(column).append(IN).append(column).append(LIST).append(CLOSE_PAREN);
            params.put(column + LIST, values);
        }
    }

    public static void addLikeCondition(StringBuilder sql, Map<String, Object> params, String value, String... columns) {
        if (!StringUtils.hasText(value) || Objects.isNull(columns) || columns.length == 0) {
            return;
        }

        String likeValue = value.startsWith(PERCENT) ? value : PERCENT + value + PERCENT;
        boolean multipleColumns = columns.length > 1;
        sql.append(AND);

        if (multipleColumns) {
            sql.append(OPEN_PAREN);
        }

        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            String columnName = column + LIKE_SUFFIX;
            sql.append(column).append(LIKE).append(columnName);

            if (i < columns.length - 1) {
                sql.append(OR);
            }

            params.put(columnName, likeValue);
        }

        if (multipleColumns) {
            sql.append(CLOSE_PAREN);
        }
    }

    public static void setOrderByAscending(StringBuilder sql, String column) {
        sql.append(ORDER_BY).append(column);
    }

    public static void setOrderByDecreasing(StringBuilder sql, String column) {
        sql.append(ORDER_BY).append(column).append(DESC);
    }

    public static void setPageable(Query query, Pageable pageable) {
        if (Objects.isNull(query) || Objects.isNull(pageable))
            return;

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int firstResult; // offset

        if (pageable.getOffset() >= 0L) {
            firstResult = (int) pageable.getOffset();
        } else {
            firstResult = pageNumber * pageSize;
        }

        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);
    }

    private static void setParams(Query query, Map<String, Object> params) {
        if (Objects.isNull(query) || Objects.isNull(params) || params.isEmpty())
            return;

        params.replaceAll((key, value) -> Objects.nonNull(value) ? value : EMPTY);
        params.forEach(query::setParameter);
    }

    private static String replaceWhere(@NotNull StringBuilder query) {
        String toReplace = WHERE_AND;
        int index = query.indexOf(toReplace);

        if (index > 0) {
            query.replace(index, index + toReplace.length(), WHERE);
        } else {
            toReplace = WHERE_DEFAULT;
            index = query.indexOf(toReplace);

            if (index > 0) {
                query.replace(index, index + toReplace.length(), SPACE);
            }
        }

        return query.toString();
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
                this.querySql = replaceWhere(new StringBuilder(querySql));
                return this;
            }

            throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must not be null or empty!");
        }

        public QueryBuilder countQuerySql(String countQuerySql) {
            if (StringUtils.hasText(countQuerySql)) {
                this.countQuerySql = replaceWhere(new StringBuilder(countQuerySql));
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

        public <T> Page<T> getResultsWithPaging(String mappingName) {
            return new PageImpl<>(getResults(mappingName), this.pageable, count());
        }

        public <T> List<T> getResults(String mappingName) {
            if (!StringUtils.hasText(querySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must be set before execution");
            }

            Query query = entityManager.createNativeQuery(querySql, mappingName);
            setPageable(query, pageable);
            setParams(query, params);

            //noinspection unchecked
            return (List<T>) query.getResultList();
        }

        public <T> Optional<T> getSingleResult(String mappingName) {
            if (!StringUtils.hasText(querySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Query SQL must be set before execution");
            }

            Query query = entityManager.createNativeQuery(querySql, mappingName);
            setParams(query, params);

            try {
                //noinspection unchecked
                return Optional.of((T) query.getSingleResult());
            } catch (NoResultException e) {
                log.debug("[QUERY_RESULT_SET_ERROR] - No result found for query: {}", querySql);
            }

            return Optional.empty();
        }

        public long count() {
            if (!StringUtils.hasText(countQuerySql)) {
                throw new BaseIllegalArgumentException(ENTITY_NAME, "Count query SQL must be set before execution");
            }

            Query countQuery = entityManager.createNativeQuery(countQuerySql);
            setParams(countQuery, params);

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
