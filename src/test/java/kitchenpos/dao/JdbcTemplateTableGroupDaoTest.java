package kitchenpos.dao;

import static kitchenpos.domain.DomainCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import kitchenpos.domain.TableGroup;

@JdbcTest
class JdbcTemplateTableGroupDaoTest {
    private JdbcTemplateTableGroupDao tableGroupDao;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = DataSourceBuilder.initializeDataSource();
        tableGroupDao = new JdbcTemplateTableGroupDao(dataSource);
    }

    @AfterEach
    void cleanUp() {
        dataSource = DataSourceBuilder.deleteDataSource();
    }

    @Test
    void save() {
        TableGroup tableGroup = createTableGroup(
            Arrays.asList(createOrderTable(true), createOrderTable(true)));
        TableGroup savedTableGroup = tableGroupDao.save(tableGroup);

        assertThat(savedTableGroup.getId()).isNotNull();
    }

    @Test
    void findById() {
        TableGroup tableGroup = createTableGroup(
            Arrays.asList(createOrderTable(true), createOrderTable(true)));
        TableGroup savedTableGroup = tableGroupDao.save(tableGroup);
        TableGroup expectedTableGroup = tableGroupDao.findById(savedTableGroup.getId()).get();

        assertAll(
            () -> assertThat(expectedTableGroup.getId()).isEqualTo(savedTableGroup.getId()),
            () -> assertThat(expectedTableGroup.getCreatedDate()).isEqualTo(savedTableGroup.getCreatedDate())
        );
    }

    @Test
    void findAll() {
        TableGroup tableGroup1 = createTableGroup(
            Arrays.asList(createOrderTable(true), createOrderTable(true)));
        TableGroup tableGroup2 = createTableGroup(
            Arrays.asList(createOrderTable(true), createOrderTable(true)));
        tableGroupDao.save(tableGroup1);
        tableGroupDao.save(tableGroup2);

        List<TableGroup> tableGroups = tableGroupDao.findAll();

        assertThat(tableGroups.size()).isEqualTo(2);
    }
}