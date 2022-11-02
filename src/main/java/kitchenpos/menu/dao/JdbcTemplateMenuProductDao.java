package kitchenpos.menu.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import kitchenpos.menu.domain.MenuProduct;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateMenuProductDao implements MenuProductDao {

    private static final String TABLE_NAME = "menu_product";
    private static final String KEY_COLUMN_NAME = "seq";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateMenuProductDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_COLUMN_NAME);
    }

    @Override
    public Long save(MenuProduct entity) {
        SqlParameterSource parameters = new BeanPropertySqlParameterSource(entity);
        Number key = jdbcInsert.executeAndReturnKey(parameters);
        return key.longValue();
    }

    @Override
    public Optional<MenuProduct> findById(Long id) {
        try {
            return Optional.of(select(id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MenuProduct> findAll() {
        String sql = "SELECT seq, menu_id, product_id, quantity, price FROM menu_product";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    @Override
    public List<MenuProduct> findAllByMenuId(Long menuId) {
        String sql = "SELECT seq, menu_id, product_id, quantity, price FROM menu_product WHERE menu_id = (:menuId)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("menuId", menuId);
        return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private MenuProduct select(Long id) {
        String sql = "SELECT seq, menu_id, product_id, quantity, price FROM menu_product WHERE seq = (:seq)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("seq", id);
        return jdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNumber) -> toEntity(resultSet));
    }

    private MenuProduct toEntity(ResultSet resultSet) throws SQLException {
        return new MenuProduct(
                resultSet.getLong(KEY_COLUMN_NAME),
                resultSet.getLong("menu_id"),
                resultSet.getLong("product_id"),
                resultSet.getLong("quantity"),
                BigDecimal.valueOf(resultSet.getLong("price"))
        );
    }
}