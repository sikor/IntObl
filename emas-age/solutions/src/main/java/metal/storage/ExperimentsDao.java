package metal.storage;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.Properties;

public class ExperimentsDao {

    private final SessionFactory sessionFactory;

    private static SessionFactory createSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure();

        Properties properties = configuration.getProperties();

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public ExperimentsDao() {
        sessionFactory = createSessionFactory();
    }

    public void save(ExperimentResult experimentResult) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.save(experimentResult);

        transaction.commit();
    }

    public int generateNewExperimentNo() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        SQLQuery sqlQuery = session.createSQLQuery(
                "select distinct experimentNo from ExperimentResult order by experimentNo desc limit 1");
        Object o = sqlQuery.list().get(0);
        transaction.commit();
        return (Integer) o + 1;
    }
}
