package ru.job4j.hibernate.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class HbmRun {
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
                    .buildMetadata()
                    .buildSessionFactory();
            create(sf);
            for (Object candidate : getAllCandidates(sf)) {
                System.out.println(candidate);
            }
            System.out.println(getCandidateById(sf, 3));
            System.out.println(getCandidateByName(sf, "Egor"));
            updateCandidateById(sf, 2);
            deleteCandidateById(sf, 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static void create(SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        Candidate one = Candidate.of("Alexandr", 5, 50000);
        Candidate two = Candidate.of("Egor", 0, 30000);
        Candidate three = Candidate.of("Ivan", 10, 100000);
        session.save(one);
        session.save(two);
        session.save(three);
        session.getTransaction().commit();
        session.close();
    }

    private static List<Candidate> getAllCandidates(SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        List candidates = session.createQuery("from Candidate").list();
        session.getTransaction().commit();
        session.close();
        return candidates;
    }

    private static Candidate getCandidateById(SessionFactory sf, int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Candidate candidate = (Candidate) session.createQuery("from Candidate where id = :id")
                .setParameter("id", id)
                .uniqueResult();
        session.getTransaction().commit();
        session.close();
        return candidate;
    }

    private static Candidate getCandidateByName(SessionFactory sf, String name) {
        Session session = sf.openSession();
        session.beginTransaction();
        Candidate candidate = (Candidate) session.createQuery("from Candidate where name = :name")
                .setParameter("name", name)
                .uniqueResult();
        session.getTransaction().commit();
        session.close();
        return candidate;
    }

    private static void updateCandidateById(SessionFactory sf, int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.createQuery("update Candidate c set c.experience = 1, c.salary = 35000 where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    private static void deleteCandidateById(SessionFactory sf, int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.createQuery("delete from Candidate c where c.id = :id")
                .setParameter("id", id)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}