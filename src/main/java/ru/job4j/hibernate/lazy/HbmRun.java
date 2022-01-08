package ru.job4j.hibernate.lazy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class HbmRun {
    public static void main(String[] args) {
        List<Brand> brands = new ArrayList<>();
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/lazy");
        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure()
                        .applySettings(cfg.getProperties())
                        .build();
        try {
            SessionFactory sf =
                    new MetadataSources(registry)
                            .addAnnotatedClass(Brand.class)
                            .addAnnotatedClass(Model.class)
                            .buildMetadata()
                            .buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();
            brands = session.createQuery("select distinct b from Brand b join fetch b.models").list();

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        for (Brand brand : brands) {
            for (Model model : brand.getModels()) {
                System.out.println(model);
            }
        }
    }
}
