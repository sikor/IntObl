package metal.storage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
}
