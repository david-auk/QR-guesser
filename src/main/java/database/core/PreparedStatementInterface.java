package database.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PreparedStatementInterface<T, K> {
    PreparedStatement prepareAddStatement(T entity) throws SQLException;
    PreparedStatement prepareUpdateStatement(T entity) throws SQLException;
    T buildFromTableWildcardQuery(ResultSet resultSet) throws SQLException;
    K getPrimaryKey(T entity);
}
