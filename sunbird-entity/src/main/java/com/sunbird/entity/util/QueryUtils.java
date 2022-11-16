package com.sunbird.entity.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sunbird.entity.model.QueryDao;

/**
 * Class handles parameters and functionality related to database
 * 
 * @author nivetha
 *
 */
public class QueryUtils {

	public interface Table {
		String DATA_NODE = "data_node";
		String PARENT_NODE = "node_mapping_parent";
		String CHILD_NODE = "node_mapping_child";
		String BOOKMARK = "bookmarks";
	}

	public interface TableFields {
		String PARENT_ID = "parent_id";
		String CHILD_ID = "child_id";
		String PARENT_MAP_ID = "parent_map_id";
		String NODE_ID = "node_id";
		String CREATED_BY = "created_by";
		String USER_ID = "user_id";
	}

	public interface Clauses {
		String FROM = " FROM ";
		String AND = " AND ";
		String OR = " OR ";
		String WHERE = " WHERE ";
		String IN = " IN ";
		String LIKE = " LIKE ";
		String ON = " ON ";
		String INNER_JOIN = " INNER JOIN ";
	}

	public interface Parameters {
		String SPACE = " ";
		String COLON = ":";
		String VALUE = COLON + "value";
		String EQUAL_VALUE = " = " + VALUE;
		String IN_VALUE = " (" + VALUE + ") ";
		String LIKE_VALUE = " % " + VALUE + " % ";
	}

	public interface Queries {
		String DRAFT_FILTER = "((status = \"DRAFT\" and (created_by = ? || updated_by = ?)) OR status NOT LIKE \"DRAFT\" OR status IS NULL)";
	}

	/**
	 * Creates search query with requested field and conditions
	 * 
	 * @param table
	 *            String - table to search
	 * @param params
	 *            Map<String, Object> - map of values to append in query
	 * @param search
	 *            Map<String, Object> - fields to add in query conditions
	 * @param keywordSearch
	 *            Boolean
	 * @param operator
	 *            String - type of operation to use in query builder
	 * @return String - query to execute
	 */
	public static QueryDao queryBuilder(String table, Map<String, Object> search, Boolean keywordSearch,
			String operator) {
		StringBuilder queryBuilder = new StringBuilder(Clauses.FROM + table + Parameters.SPACE);
		Map<String, Object> params = new HashMap<>();
		if (search != null) {
			for (Map.Entry<String, Object> entry : search.entrySet()) {
				String columnName = tableColumn(entry.getKey());
				if (StringUtils.isNotEmpty(columnName)) {
					// append operator
					if (!(queryBuilder.toString().contains(Clauses.WHERE))) {
						queryBuilder.append(Clauses.WHERE);
					} else {
						queryBuilder.append(operator);
					}
					queryBuilder.append(columnName);
					// append values
					String value = StringUtils.EMPTY;
					if (entry.getValue() instanceof List) {
						queryBuilder.append(Clauses.IN);
						value = Parameters.IN_VALUE;
					} else if (keywordSearch != null && keywordSearch) {
						queryBuilder.append(Clauses.LIKE);
						value = Parameters.LIKE_VALUE;
					} else {
						value = Parameters.EQUAL_VALUE;
					}
					queryBuilder.append(value.replace(Parameters.VALUE, Parameters.COLON + entry.getKey()));
					params.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return new QueryDao(queryBuilder.toString(), params);
	}

	/**
	 * Returns the table field name
	 * 
	 * @param field
	 *            String
	 * @return String
	 */
	public static String tableColumn(String field) {

		switch (field) {
		case "id":
		case "type":
		case "name":
		case "description":
		case "status":
		case "source":
		case "child":
			return field;

		case Constants.Parameters.PARENT_ID:
			return TableFields.PARENT_ID;
		case "myRequest":
			return TableFields.CREATED_BY;

		default:
			return StringUtils.EMPTY;
		}

	}

}
