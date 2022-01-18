package ru.job4j.hibernate.intergration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderStoreTest {
    private final BasicDataSource pool = new BasicDataSource();

    @Before
    public void setup() throws SQLException {
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:mem:tests;sql_syntax_pgs=true");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setMaxTotal(10);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("db/update_001.sql")))) {
                in.lines().forEach(line -> builder.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.getConnection().prepareStatement(builder.toString()).executeUpdate();
    }

    @After
    public void cleanup() throws SQLException {
        pool.getConnection().prepareStatement("DROP TABLE orders").executeUpdate();
    }

    @Test
    public void whenSaveOrderAndFindAllOneRowWithDescription() {
        OrderStore store = new OrderStore(pool);

        store.save(Order.of("name1", "description1"));

        List<Order> all = (List<Order>) store.findAll();

        assertThat(all.size(), is(1));
        assertThat(all.get(0).getDescription(), is("description1"));
        assertThat(all.get(0).getId(), is(1));
    }

    @Test
    public void whenUpdate() {
        OrderStore store = new OrderStore(pool);
        Order original = Order.of("original", "description #1");
        store.save(original);
        Order modified = new Order(original.getId(), "modified", "description #2", original.getCreated());
        store.update(modified);
        List<Order> all = (List<Order>) store.findAll();
        assertThat(all.size(), is(1));
        assertThat(all.get(0).getDescription(), is("description #2"));
    }

    @Test
    public void whenFindByNameThenReturnListOfSize2() {
        OrderStore store = new OrderStore(pool);
        Order order1 = Order.of("name", "description #1");
        Order order2 = Order.of("name", "description #2");
        store.save(order1);
        store.save(order2);
        List<Order> orders = (List<Order>) store.findByName("name");
        assertThat(orders.size(), is(2));
        assertThat(orders.get(0).getDescription(), is("description #1"));
        assertThat(orders.get(1).getDescription(), is("description #2"));
    }

    @Test
    public void whenFindById() {
        OrderStore store = new OrderStore(pool);
        Order order = Order.of("name", "description #1");
        store.save(order);
        Order result = store.finById(order.getId());
        assertThat(result.getDescription(), is("description #1"));
    }
}