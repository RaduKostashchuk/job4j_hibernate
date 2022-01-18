package ru.job4j.hibernate.intergration;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderStore {
    private final BasicDataSource pool;

    public OrderStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Order save(Order order) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =
                     cn.prepareStatement("INSERT INTO orders (name, description, created) VALUES (?, ?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getName());
            ps.setString(2, order.getDescription());
            ps.setTimestamp(3, order.getCreated());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next()) {
                order.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public Collection<Order> findAll() {
        List<Order> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM orders")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            new Order(
                                    rs.getInt(1),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getTimestamp("created")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Order finById(int id) {
        Order result = null;
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = new Order(
                            rs.getInt(1),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("created"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void update(Order order) {
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement(
                "UPDATE orders SET name = ?, description = ?, created = ? where id = ?")) {
            ps.setString(1, order.getName());
            ps.setString(2, order.getDescription());
            ps.setTimestamp(3, order.getCreated());
            ps.setInt(4, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Collection<Order> findByName(String name) {
        List<Order> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM orders WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            new Order(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getTimestamp("created")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
