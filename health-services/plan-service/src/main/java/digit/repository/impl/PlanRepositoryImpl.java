package digit.repository.impl;

import digit.kafka.Producer;
import digit.repository.PlanRepository;
import digit.repository.querybuilder.PlanQueryBuilder;
import digit.repository.rowmapper.PlanRowMapper;
import digit.web.models.Plan;
import digit.web.models.PlanRequest;
import digit.web.models.PlanSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private Producer producer;

    private PlanQueryBuilder planQueryBuilder;

    private PlanRowMapper planRowMapper;

    private JdbcTemplate jdbcTemplate;

    public PlanRepositoryImpl(Producer producer, PlanQueryBuilder planQueryBuilder, PlanRowMapper planRowMapper,
                              JdbcTemplate jdbcTemplate) {
        this.producer = producer;
        this.planQueryBuilder = planQueryBuilder;
        this.planRowMapper = planRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(PlanRequest planRequest) {
        producer.push("save-plan", planRequest);
    }

    @Override
    public List<Plan> search(PlanSearchCriteria planSearchCriteria) {
        List<String> planIds = queryDatabaseForPlanIds(planSearchCriteria);
        log.info("Plan ids: " + planIds);

        List<Plan> plans = searchPlanByIds(planIds);

        return plans;
    }

    @Override
    public void update(PlanRequest planRequest) {
        producer.push("update-plan", planRequest);
    }

    private List<String> queryDatabaseForPlanIds(PlanSearchCriteria planSearchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = planQueryBuilder.getPlanSearchQuery(planSearchCriteria, preparedStmtList);
        log.info("Plan search query: " + query);
        return jdbcTemplate.query(query, new SingleColumnRowMapper<>(String.class), preparedStmtList.toArray());
    }

    private List<Plan> searchPlanByIds(List<String> planIds) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = planQueryBuilder.getPlanQuery(planIds, preparedStmtList);
        log.info("Plan query: " + query);
        return jdbcTemplate.query(query, planRowMapper, preparedStmtList.toArray());
    }

}
