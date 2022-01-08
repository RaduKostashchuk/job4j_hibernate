package ru.job4j.hibernate.onetomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HbmRun {
    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/autos");
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
            Model model1 = Model.of("Vesta");
            Model model2 = Model.of("Granta");
            Model model3 = Model.of("Kalina");
            Model model4 = Model.of("Largus");
            Model model5 = Model.of("Priora");

            session.save(model1);
            session.save(model2);
            session.save(model3);
            session.save(model4);
            session.save(model5);

            Brand brand = Brand.of("VAZ");
            brand.addModel(model1);
            brand.addModel(model2);
            brand.addModel(model3);
            brand.addModel(model4);
            brand.addModel(model5);

            session.save(brand);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
