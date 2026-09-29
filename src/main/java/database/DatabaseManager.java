package database;

import database.mappers.ConsumerLoanCaseMapper;
import database.models.ConsumerLoanCase;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static constants.Constants.MYBATIS_CONFIG;

public class DatabaseManager {
    private static final SqlSessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SqlSessionFactory buildSessionFactory() {
        try (InputStream config = Resources.getResourceAsStream(MYBATIS_CONFIG)) {
            return new SqlSessionFactoryBuilder().build(config);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + MYBATIS_CONFIG, e);
        }
    }

    public static List<ConsumerLoanCase> getActiveConsumerLoanCases() {
        try (SqlSession session = SESSION_FACTORY.openSession()) {
            return session.getMapper(ConsumerLoanCaseMapper.class).findActiveCases();
        }
    }
}
