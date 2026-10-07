package database.mappers;

import database.models.ConsumerLoanCase;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConsumerLoanCaseMapper {
    @Select("SELECT id, description, amount, months, expected_result FROM consumer_loan_cases WHERE active = TRUE ORDER BY id")
    List<ConsumerLoanCase> findActiveCases();
}
