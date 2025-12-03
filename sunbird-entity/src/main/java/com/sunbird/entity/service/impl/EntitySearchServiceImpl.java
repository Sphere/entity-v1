package com.sunbird.entity.service.impl;

import com.sunbird.entity.mapper.response.EntityDocumentMapper;
import com.sunbird.entity.model.DTO.EntityResponseDTO;
import com.sunbird.entity.model.DTO.EntitySearchRequestDTO;
import com.sunbird.entity.model.es.EntityDocument;
import com.sunbird.entity.repository.elasticsearch.EntityDocumentESRepository;
import com.sunbird.entity.service.EntitySearchService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntitySearchServiceImpl implements EntitySearchService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private EntityDocumentESRepository entityDocumentESRepository;

    @Autowired
    private EntityDocumentMapper entityDocumentMapper;

    /**
     * Search with multiple filters - Main search method
     */
    @Override
    public List<EntityDocument> searchWithFilters(
            String searchText,
            String type,
            String status,
            String level) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Full-text search on name and description
        if (searchText != null && !searchText.isEmpty()) {
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(searchText)
                            .field("name", 2.0f)  // boost name by 2
                            .field("description")
//                            .fuzziness(Fuzziness.AUTO)
            );
        }

        // Exact match filters - use .keyword for text fields
        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("status.keyword", status));
        }
        if (level != null && !level.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("level.keyword", level));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Search with multiple filters - PAGINATED version
     */
    public Page<EntityDocument> searchWithFiltersPaginated(
            String searchText,
            String type,
            String status,
            String level,
            String code,
            String language,
            int page,
            int size) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Full-text search on name and description
        if (searchText != null && !searchText.isEmpty()) {
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(searchText)
                            .field("name", 2.0f)  // boost name by 2
                            .field("description")
//                            .fuzziness(Fuzziness.AUTO)
            );
        }

        // Exact match filters - use .keyword for text fields
        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("status.keyword", status));
        }
        if (level != null && !level.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("level.keyword", level));
        }
        if (code != null && !code.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("code.keyword", code));
        }
        if (language != null && !language.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("language.keyword", language));
        }

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return elasticsearchRestTemplate.queryForPage(searchQuery, EntityDocument.class);
    }

    /** TODO parameter validation
     * @param searchText
     * @param type
     * @param status
     * @param level
     * @return
     */
    @Override
    public List<EntityResponseDTO> findEntityWithGenericAttributeValue(EntitySearchRequestDTO entitySearchRequestDTO) {
        List<EntityResponseDTO> entityResponseDTOList = Collections.emptyList();

        try {
            //TODO: parameter validation required
//            List<EntityDocument> entityDocumentList = searchWithFilters(
//                    entitySearchRequestDTO.getSearchText(),
//                    entitySearchRequestDTO.getType(),
//                    entitySearchRequestDTO.getStatus(),
//                    entitySearchRequestDTO.getLevel());

            Page<EntityDocument> entityDocumentPage = searchWithFiltersPaginated(
                    entitySearchRequestDTO.getSearchText(),
                    entitySearchRequestDTO.getType(),
                    entitySearchRequestDTO.getStatus(),
                    entitySearchRequestDTO.getLevel(),
                    entitySearchRequestDTO.getCode(),
                    entitySearchRequestDTO.getLanguage(),
                    entitySearchRequestDTO.getPage(),
                    entitySearchRequestDTO.getSize());

            List<EntityDocument> entityDocumentList = entityDocumentPage.getContent();

            if (entityDocumentList != null && !entityDocumentList.isEmpty()) {
                entityResponseDTOList = entityDocumentList.stream()
                        .map(entityDocument -> entityDocumentMapper.toEntityResponse(entityDocument))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return entityResponseDTOList;
    }

    /**
     * Simple name search
     */
    @Override
    public List<EntityDocument> searchByName(String searchText) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", searchText)
                        .fuzziness(Fuzziness.AUTO))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Search in additionalProperties (flattened field)
     */
    public List<EntityDocument> searchByAdditionalProperty(String key, String value) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("additionalProperties." + key, value))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Search by code
     */
    public EntityDocument searchByCode(String code) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("code.keyword", code))
                .build();

        List<EntityDocument> results = elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Paginated search with sorting
     */
    public Page<EntityDocument> searchPaginated(
            String searchText,
            int page,
            int size,
            String sortBy,
            String sortOrder) {

        Pageable pageable = PageRequest.of(page, size);

        SortOrder order = "asc".equalsIgnoreCase(sortOrder) ? SortOrder.ASC : SortOrder.DESC;

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(searchText, "name", "description"))
                .withPageable(pageable)
                .withSort(SortBuilders.fieldSort(sortBy).order(order))
                .build();

        return elasticsearchRestTemplate.queryForPage(searchQuery, EntityDocument.class);
    }

    /**
     * Search with aggregations
     */
    public Map<String, Object> searchWithAggregations(String searchText) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        searchText != null && !searchText.isEmpty()
                                ? QueryBuilders.multiMatchQuery(searchText, "name", "description")
                                : QueryBuilders.matchAllQuery()
                )
                .addAggregation(AggregationBuilders.terms("by_type").field("type.keyword").size(50))
                .addAggregation(AggregationBuilders.terms("by_status").field("status.keyword").size(50))
                .addAggregation(AggregationBuilders.terms("by_level").field("level.keyword").size(50))
                .build();

        AggregatedPage<EntityDocument> result =
                (AggregatedPage<EntityDocument>) elasticsearchRestTemplate.queryForPage(searchQuery, EntityDocument.class);

        Map<String, Object> response = new HashMap<>();
        response.put("documents", result.getContent());
        response.put("aggregations", result.getAggregations());
        response.put("totalHits", result.getTotalElements());

        return response;
    }

    /**
     * Match by type and status
     */
    public List<EntityDocument> findByTypeAndStatus(String type, String status) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("status.keyword", status));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Wildcard search
     */
    public List<EntityDocument> wildcardSearch(String fieldName, String pattern) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.wildcardQuery(fieldName, "*" + pattern + "*"))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Prefix search
     */
    public List<EntityDocument> prefixSearch(String fieldName, String prefix) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.prefixQuery(fieldName, prefix))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Range query for dates
     */
    public List<EntityDocument> findByDateRange(Date startDate, Date endDate) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("createdDate")
                        .gte(startDate.getTime())
                        .lte(endDate.getTime()))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Find by multiple IDs
     */
    public List<EntityDocument> findByIds(List<Integer> ids) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.idsQuery().addIds(
                        ids.stream().map(String::valueOf).toArray(String[]::new)
                ))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Complex search with multiple conditions
     */
    public List<EntityDocument> complexSearch(
            String searchText,
            List<String> types,
            List<String> statuses,
            Date startDate,
            Date endDate,
            String sortBy,
            String sortOrder) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Text search
        if (searchText != null && !searchText.isEmpty()) {
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(searchText)
                            .field("name", 2.0f)
                            .field("description")
                            .field("code")
                            .fuzziness(Fuzziness.AUTO)
            );
        }

        // Multiple types (OR condition)
        if (types != null && !types.isEmpty()) {
            BoolQueryBuilder typeQuery = QueryBuilders.boolQuery();
            for (String type : types) {
                typeQuery.should(QueryBuilders.termQuery("type.keyword", type));
            }
            boolQuery.filter(typeQuery);
        }

        // Multiple statuses (OR condition)
        if (statuses != null && !statuses.isEmpty()) {
            BoolQueryBuilder statusQuery = QueryBuilders.boolQuery();
            for (String status : statuses) {
                statusQuery.should(QueryBuilders.termQuery("status.keyword", status));
            }
            boolQuery.filter(statusQuery);
        }

        // Date range
        if (startDate != null || endDate != null) {
            org.elasticsearch.index.query.RangeQueryBuilder rangeQuery =
                    QueryBuilders.rangeQuery("createdDate");
            if (startDate != null) {
                rangeQuery.gte(startDate.getTime());
            }
            if (endDate != null) {
                rangeQuery.lte(endDate.getTime());
            }
            boolQuery.filter(rangeQuery);
        }

        // Build query with sorting
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery);

        if (sortBy != null && !sortBy.isEmpty()) {
            SortOrder order = "asc".equalsIgnoreCase(sortOrder) ? SortOrder.ASC : SortOrder.DESC;
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(order));
        }

        SearchQuery searchQuery = queryBuilder.build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Get all documents (with optional limit)
     */
    public List<EntityDocument> findAll(int limit) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(0, limit))
                .build();

        return elasticsearchRestTemplate.queryForList(searchQuery, EntityDocument.class);
    }

    /**
     * Count documents matching criteria
     */
    public long count(String type, String status) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type.keyword", type));
        }
        if (status != null && !status.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("status.keyword", status));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();

        return elasticsearchRestTemplate.count(searchQuery, EntityDocument.class);
    }

    /**
     * Check if document exists by code
     */
    public boolean existsByCode(String code) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("code.keyword", code))
                .build();

        return elasticsearchRestTemplate.count(searchQuery, EntityDocument.class) > 0;
    }
}