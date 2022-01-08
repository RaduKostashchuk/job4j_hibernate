package ru.job4j.hibernate.manytomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HbmRun {
    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/manytomany");
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .applySettings(cfg.getProperties())
                .build();
        try {
            SessionFactory sf = new MetadataSources(registry)
                    .addAnnotatedClass(Author.class)
                    .addAnnotatedClass(Book.class)
                    .buildMetadata()
                    .buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();
            Book book1 = Book.of("Book of author one");
            Book book2 = Book.of("Book of author two");
            Book book3 = Book.of("Book of authors one and two");

            Author author1 = Author.of("Author one");
            Author author2 = Author.of("Author two");

            author1.addBook(book1);
            author1.addBook(book3);
            author2.addBook(book2);
            author2.addBook(book3);

            session.persist(author1);
            session.persist(author2);

            Author author = session.get(Author.class, 29);
            session.remove(author);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
