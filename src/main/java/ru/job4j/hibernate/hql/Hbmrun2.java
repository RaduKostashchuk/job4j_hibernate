package ru.job4j.hibernate.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Hbmrun2 {
    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/hql");
        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure()
                        .applySettings(cfg.getProperties())
                        .build();
        try {
            SessionFactory sf = new MetadataSources(registry)
                    .addAnnotatedClass(Candidate.class)
                    .addAnnotatedClass(Base.class)
                    .addAnnotatedClass(Vacancy.class)
                    .buildMetadata()
                    .buildSessionFactory();
            createAll(sf);
            System.out.println(getCandidateById(sf, 4));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static void createAll(SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Vacancy vac1 = Vacancy.of("builder");
        Vacancy vac2 = Vacancy.of("driver");
        Base base = Base.of("Base");
        base.addVacancy(vac1);
        base.addVacancy(vac2);
        Candidate candidate = Candidate.of("Alexandr", 5, 50000);
        candidate.setBase(base);
        session.save(vac1);
        session.save(vac2);
        session.save(base);
        session.save(candidate);
        session.getTransaction().commit();
        session.close();
    }

    private static Candidate getCandidateById(SessionFactory sf, int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Candidate candidate = (Candidate) session.createQuery(
                "select distinct c from Candidate c join fetch c.base b join fetch b.vacancies where c.id = :id")
                .setParameter("id", id)
                .uniqueResult();
        session.getTransaction().commit();
        session.close();
        return candidate;
    }
}
