package hbm.fraudDetectionSystem.RuleEngine.Core.Core.Component;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceFraudList.SpringLogic.FraudListValue.FraudValueRepository;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTrans;
import hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrTrans.CurrTransService;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ApplicationContext;
import hbm.fraudDetectionSystem.GeneralComponent.Utility.ServiceHelper;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLPatternUtil;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.DSLResolver;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.MVELParser;
import hbm.fraudDetectionSystem.RuleEngine.Core.Util.TypeChecker;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters.AggregateCounter;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.AggregateCounters.AggregateCounterRepository;
import hbm.fraudDetectionSystem.RuleEngine.SpringLogic.Filtration.Filtration;
import hbm.fraudDetectionSystem.RuleEngine.Utils.LimitCounter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static hbm.fraudDetectionSystem.GeneralComponent.Constant.CurrTransConstant.convertCamelToSnake;

@Slf4j
public class LimitEngine extends DSLResolver {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final Map<String, String> transactionData;
    protected final AggregateCounterRepository aggregateCounterRepository;
    protected final FraudValueRepository valueRepository;
    protected final CurrTransService currTransService;
    protected final LimitCounter limitCounter;
    protected boolean evaluateStatus = false;
    protected StringBuilder filtrationJoinQuery = new StringBuilder();
    protected String filtrationQuery = "";

    public LimitEngine(Map<String, String> transactionData) {
        this.transactionData = transactionData;
        this.aggregateCounterRepository = ApplicationContext.getBean("aggregateCounterRepository", AggregateCounterRepository.class);
        this.valueRepository = ApplicationContext.getBean("fraudValueRepository", FraudValueRepository.class);
        this.currTransService = ApplicationContext.getBean("currTransServiceImpl", CurrTransService.class);
        this.limitCounter = ApplicationContext.getBean("limitCounter", LimitCounter.class);
    }

    public void run(String expression) throws Exception {
        LOGGER.info(
                String.format(
                        "Initial Expression: %s",
                        expression
                ),
                1
        );

        //This method will run the magic bro
        String resolvedCondition = resolveConditionFormula(expression, this.transactionData, false);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("input", this.transactionData);

        //This will evaluate the expression
        LOGGER.info("Start evaluate expression...");
        this.evaluateStatus = MVELParser.evaluateConditionFormula(resolvedCondition, dataModel);
        LOGGER.info(
                String.format(
                        "Evaluate Result: [%s]",
                        this.evaluateStatus
                )
        );
    }

    @Override
    protected Object resolveValue(String counterId, String historyAttribute, String transField, Map<String, String> inputData, boolean ignoreType) throws Exception {
        LOGGER.info(
                String.format(
                        "Find counter with id: [%s]",
                        counterId
                )
        );

        AggregateCounter aCounter = aggregateCounterRepository.findById(Long.parseLong(counterId))
                .orElseThrow(() -> new Exception(
                        String.format(
                                "Counter with id: [%s] isn't found",
                                counterId
                        )
                ));


        LOGGER.info(
                String.format(
                        "Fetched Counter detail: \n\tCounter id: [%s] \n\tAggregating Entity: [%s] \n\tAttribute: [%s] \n\tMetric Type: [%s] \n\tIncrementation Mode: [%s]",
                        aCounter.getId(), aCounter.getAggregatingEntity(), aCounter.getAttribute(), aCounter.getMetricType(), aCounter.getIncrementationMode()
                )
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String currentTime = inputData.get("sysdate");
        LocalDateTime startDate = LocalDateTime.parse(currentTime, formatter);
        LocalDateTime endDate = LocalDateTime.parse(currentTime, formatter);

        LOGGER.info(
                String.format(
                        "Aggregate current time: [%s]",
                        currentTime
                )
        );
        LOGGER.info("Checking the counter is having filtration...");

        if (isCounterHaveFiltration(aCounter)) {
            LOGGER.info("Counter have filtration will process it");

            //This method will validate the filtration condition
            boolean isFiltrationTriggered = this.isFiltrationTriggered(aCounter);

            if (isFiltrationTriggered) {
                LOGGER.info("Filtration is triggered");
                return this.checkCounter(aCounter, currentTime, startDate, endDate, inputData);
            } else {
                return -1;
            }
        } else {
            return this.checkCounter(aCounter, currentTime, startDate, endDate, inputData);
        }
    }

    protected boolean isCycleAreLength(AggregateCounter aCounter) {
        return aCounter.getTimeStartCycle() != 0 && aCounter.getTimeEndCycle() != 0;
    }

    protected boolean isCounterHaveFiltration(AggregateCounter counters) {
        return counters.getFiltrations().size() > 0;
    }

    protected long checkCounter(AggregateCounter aCounter, String currentTime, LocalDateTime startDate, LocalDateTime endDate, Map<String, String> inputData) {
        if (this.isCycleAreLength(aCounter)) {
            LOGGER.info("Aggregate is using type: [CYCLE LENGTH]");
            return this.evaluateCounterCycleLength(aCounter, currentTime, startDate, endDate, inputData);
        }

        LOGGER.info("Aggregate is using type: [CYCLE FIXED]");
        return this.evaluateCounterCycleFixed(aCounter, startDate, endDate, inputData);
    }

    protected long evaluateCounterCycleLength(AggregateCounter aCounter, String currentTime, LocalDateTime startDate, LocalDateTime endDate, Map<String, String> inputData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        String startTime = aCounter.getTimeStartCycle() > aCounter.getTimeEndCycle() ?
                this.checkTimeCycle(startDate, aCounter.getTimeStartCycle(), 1).format(formatter) :
                this.checkTimeCycle(startDate, aCounter.getTimeStartCycle(), 0).format(formatter);
        String endTime = this.checkTimeCycle(endDate, aCounter.getTimeEndCycle(), 0).format(formatter);

        LOGGER.info(
                String.format(
                        "Prepared time cycle: \n\tStart time: [%s] \n\tEnd time: [%s]",
                        startTime, endTime
                )
        );

        LocalDateTime v1 = LocalDateTime.parse(startTime, formatter);
        LocalDateTime v2 = LocalDateTime.parse(endTime, formatter);
        LocalDateTime currentTimestamp = LocalDateTime.parse(currentTime, formatter);

        boolean isOutsideRange = !(currentTimestamp.isAfter(v1) && currentTimestamp.isBefore(v2));

        if (isOutsideRange) {
            LOGGER.info("Current time is out of range, skip check...");
            return -1;
        }

        return evaluateCounter(aCounter, startTime, endTime, inputData);
    }

    protected long evaluateCounterCycleFixed(AggregateCounter aCounter, LocalDateTime startDate, LocalDateTime endDate, Map<String, String> inputData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return evaluateCounter(
                aCounter,
                startDate.minus(Duration.ofMillis(aCounter.getTimeStartCycle())).format(formatter),
                endDate.plus(Duration.ofMillis(aCounter.getTimeEndCycle())).format(formatter),
                inputData
        );
    }

    protected long evaluateCounter(AggregateCounter counters, String startDate, String endDate, Map<String, String> inputData) {
        switch (counters.getMetricType()) {
            case 1:
                return this.currTransService.limitCountMatched(counters.getAggregatingEntity(), counters.getAttribute(), startDate, endDate, inputData, this.filtrationJoinQuery.toString(), this.filtrationQuery.toString()) + 1;

            case 2:
                return this.currTransService.limitCountDifferent(counters.getAggregatingEntity(), counters.getAttribute(), startDate, endDate, inputData, this.filtrationJoinQuery.toString(), this.filtrationQuery.toString());

            case 3:
                return this.currTransService.limitCountAmountMatched(counters.getAggregatingEntity(), counters.getAttribute(), startDate, endDate, inputData, this.filtrationJoinQuery.toString(), this.filtrationQuery.toString()) + Integer.parseInt(this.transactionData.get("amount"));

            case 4:
                return this.currTransService.limitCountAmountDifferent(counters.getAggregatingEntity(), counters.getAttribute(), startDate, endDate, inputData, this.filtrationJoinQuery.toString(), this.filtrationQuery.toString()) + Integer.parseInt(this.transactionData.get("amount"));

            default:
                throw new RuntimeException(
                        String.format(
                                "Counter Metric Type: [%s] is unknown",
                                counters.getMetricType()
                        )
                );
        }
    }

    protected LocalDateTime checkTimeCycle(LocalDateTime initialTime, long time, int type) {
        Duration duration = Duration.ofMillis(time);

        int days = (int) duration.toDays();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        if (days > 0)
            initialTime = initialTime.withDayOfMonth(days);
        else if (type == 1)
            initialTime = initialTime.minus(Duration.ofMillis(86400000));

        if (type == 1) {
            initialTime = initialTime.minus(Duration.ofMillis(hours * 3600000L));
            initialTime = initialTime.minus(Duration.ofMillis(minutes * 60000L));
            initialTime = initialTime.minus(Duration.ofMillis(seconds * 1000L));
        } else {
            initialTime = initialTime.withHour(hours);
            initialTime = initialTime.withMinute(minutes);
            initialTime = initialTime.withSecond(seconds);
        }

        return initialTime;
    }

    protected boolean isFiltrationTriggered(AggregateCounter counters) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("input", this.transactionData);
        dataModel.put("tyChe", new TypeChecker());

        if (counters.getIsFormulaEnabled()) {
            LOGGER.info(
                    String.format(
                            "Counter have formula, will proceed with formula : [%s]",
                            counters.getFormula()
                    )
            );
            String filtrationFormula = this.resolveFiltration(counters.getFormula(), counters.getFiltrations(), dataModel);

            LOGGER.info(
                    "Start evaluate formula..."
            );
            boolean finalStatus = MVELParser.evaluateSFormula(filtrationFormula);
            LOGGER.info(
                    String.format(
                            "Final evaluate result: [%s]",
                            finalStatus
                    )
            );
            return finalStatus;
        } else {
            LOGGER.info("Counter not have formula, will proceed with DEFAULT formula");
            String filtrationFormula = this.resolveFiltration(this.buildFiltrationFormula(counters.getFiltrations()), counters.getFiltrations(), dataModel);

            LOGGER.info(
                    "Start evaluate formula..."
            );
            boolean finalStatus = MVELParser.evaluateSFormula(filtrationFormula);
            LOGGER.info(
                    String.format(
                            "Final evaluate result: [%s]",
                            finalStatus
                    )
            );

            return finalStatus;
        }
    }

    protected String resolveFiltration(String formula, List<Filtration> filtrations, Map<String, Object> dataModel) {
        LOGGER.info(
                String.format(
                        "Initial formula: [%s]",
                        formula
                )
        );

        this.filtrationQuery = " and (" + formula.replaceAll("\\|\\|", "or").replaceAll("&&", "and") + ")";

        for (Filtration filtration : filtrations) {
            LOGGER.info(
                    String.format(
                            "Start processing formula [%s]",
                            filtration.getConditionId()
                    )
            );
            LOGGER.info(
                    String.format(
                            "Detail filtration: \n\tCondition Id: [%s] \n\tAttribute: [%s] \n\tOperator: [%s] \n\tOperator Detail: [%s] \n\tValue: [%s] \n\tMin Range: [%s] \n\tMax Range: [%s]",
                            filtration.getConditionId(), filtration.getAttribute(), filtration.getOperator(), filtration.getOperatorDetails(), filtration.getValue(), filtration.getMinRange(), filtration.getMaxRange()
                    )
            );

            this.isOperDetailsList(filtration, dataModel);

            LOGGER.info("Start generate filtration expression");

            String finalExpression = this.generateFiltrationExpression(filtration);

            LOGGER.info(
                    String.format(
                            "Generated expression: [%s]",
                            finalExpression
                    )
            );
            LOGGER.info("Start evaluate expression...");
            boolean evaluateStatus = MVELParser.evaluateConditionFormula(finalExpression, dataModel);
            LOGGER.info(
                    String.format(
                            "Finish processing formula [%s], result: [%s]",
                            filtration.getConditionId(), evaluateStatus
                    )
            );

            formula = DSLPatternUtil.assignEvaluatedStatus(formula, filtration.getConditionId(), evaluateStatus);
        }

        LOGGER.info(
                String.format(
                        "Final formula: [%s]",
                        formula
                )
        );

        return formula;
    }

    protected String buildFiltrationFormula(List<Filtration> filtrations) {
        StringBuilder tempFormula = new StringBuilder();
        for (int i = 0; i < filtrations.size(); i++) {
            tempFormula.append(filtrations.get(i).getConditionId());

            if (i != filtrations.size() - 1) {
                tempFormula.append(" && ");
            }
        }

        return tempFormula.toString();
    }

    protected String generateFiltrationExpression(Filtration filtration) {
        if (filtration.getOperator() == null) {
            return this.mapperFiltrationExpression(new StringBuilder(), filtration, 1);
        } else {
            return this.mapperFiltrationExpression(new StringBuilder(), filtration, 2);
        }
    }

    protected String mapperFiltrationExpression(StringBuilder result, Filtration filtration, int type) {
        StringBuilder tempQuery = new StringBuilder();

        switch (type) {
            //Null Operator
            case 1:
                switch (filtration.getOperatorDetails()) {
                    case "DIVISIBLE":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" % ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)")
                                    .append(" = 0");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId())
                                    .append(".utrnno and cat_").append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) % ")
                                    .append(filtration.getValue())
                                    .append(" = 0");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" % ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")")
                                .append(" == 0").toString();

                    case "EQUALS":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" = '").append(filtration.getValue()).append("'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value = '")
                                    .append(filtration.getValue()).append("'");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("input.").append(filtration.getAttribute())
                                .append(" == ").append("'").append(filtration.getValue()).append("'").toString();

                    case "GREATER":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" > ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) > cast(")
                                    .append(filtration.getValue()).append(" as bigint)");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" > ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")").toString();

                    case "LESS":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" < ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) < cast(")
                                    .append(filtration.getValue()).append(" as bigint)");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" < ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")").toString();

                    case "LIKE_START":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" like '").append(filtration.getValue()).append("%'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value like '")
                                    .append(filtration.getValue()).append("%'");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("input.").append(filtration.getAttribute())
                                .append(".matches('").append(filtration.getValue())
                                .append(".*')").toString();

                    case "LIKE_END":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" like '%").append(filtration.getValue()).append("'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value like '%")
                                    .append(filtration.getValue()).append("'");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("input.").append(filtration.getAttribute())
                                .append(".matches('.*").append(filtration.getValue())
                                .append("')").toString();

                    case "LIKE_BOTH":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" like '%").append(filtration.getValue()).append("%'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value like '%")
                                    .append(filtration.getValue()).append("%'");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("input.").append(filtration.getAttribute())
                                .append(".matches('.*").append(filtration.getValue())
                                .append(".*')").toString();

                    case "LIST":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n exists(select 1 from t_fraud_list_value tflv join t_fraud_list tfl on tflv.list_id = tfl.list_id where tfl.list_name = '")
                                    .append(filtration.getValue()).append("' and tflv.value = ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute())).append(")");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n exists(select 1 from t_fraud_list_value tflv join t_fraud_list tfl on tflv.list_id = tfl.list_id where tfl.list_name = '")
                                    .append(filtration.getValue()).append("' and tflv.value = cat_")
                                    .append(filtration.getConditionId()).append(".value");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("fraudList").append(" contains ")
                                .append("input.").append(filtration.getAttribute())
                                .toString();

                    case "RANGE":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n (cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" >= ")
                                    .append("cast(").append(filtration.getMinRange()).append(" as bigint)")
                                    .append(" and ")
                                    .append("cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" <= ")
                                    .append("cast(").append(filtration.getMaxRange()).append(" as bigint))");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) >= ")
                                    .append(filtration.getMinRange()).append(" and cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) <= ")
                                    .append(filtration.getMaxRange());
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" >= ")
                                .append("tyChe.cvt(").append(filtration.getMinRange()).append(")")
                                .append(" && tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" <= ")
                                .append("tyChe.cvt(").append(filtration.getMaxRange()).append(")").toString();

                    default:
                        throw new RuntimeException(
                                String.format(
                                        "Filtration operator detail: [%s] is unknown",
                                        filtration.getOperatorDetails()
                                )
                        );
                }

                //Not Operator
            case 2:
                switch (filtration.getOperatorDetails()) {
                    case "DIVISIBLE":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" % ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)")
                                    .append(" != 0");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) % ")
                                    .append(filtration.getValue())
                                    .append(" != 0)");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" % ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")")
                                .append(" != 0").toString();

                    case "EQUALS":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" != '").append(filtration.getValue()).append("'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value != '")
                                    .append(filtration.getValue()).append("')");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("input.").append(filtration.getAttribute())
                                .append(" != ").append("'").append(filtration.getValue()).append("'").toString();

                    case "GREATER":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" < ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) < cast(")
                                    .append(filtration.getValue()).append(" as bigint))");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" < ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")").toString();

                    case "LESS":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" > ")
                                    .append("cast(").append(filtration.getValue()).append(" as bigint)");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) > cast(")
                                    .append(filtration.getValue()).append(" as bigint))");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" > ")
                                .append("tyChe.cvt(").append(filtration.getValue()).append(")").toString();

                    case "LIKE_START":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" not like '").append(filtration.getValue()).append("%'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value not like '")
                                    .append(filtration.getValue()).append("%')");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("!(input.").append(filtration.getAttribute())
                                .append(".matches('").append(filtration.getValue())
                                .append(".*'))").toString();

                    case "LIKE_END":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" not like '%").append(filtration.getValue()).append("'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value not like '%")
                                    .append(filtration.getValue()).append("')");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("!(input.").append(filtration.getAttribute())
                                .append(".matches('.*").append(filtration.getValue())
                                .append("'))").toString();

                    case "LIKE_BOTH":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute()))
                                    .append(" not like '%").append(filtration.getValue()).append("%'");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cat_")
                                    .append(filtration.getConditionId()).append(".value not like '%")
                                    .append(filtration.getValue()).append("%')");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("!(input.").append(filtration.getAttribute())
                                .append(".matches('.*").append(filtration.getValue())
                                .append(".*'))").toString();

                    case "LIST":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n exists(select 1 from t_fraud_list_value tflv join t_fraud_list tfl on tflv.list_id = tfl.list_id where tfl.list_name = '")
                                    .append(filtration.getValue()).append("' and tflv.value != ct.")
                                    .append(convertCamelToSnake(filtration.getAttribute())).append(")");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n exists(select 1 from t_fraud_list_value tflv join t_fraud_list tfl on tflv.list_id = tfl.list_id where tfl.list_name = '")
                                    .append(filtration.getValue()).append("' and tflv.value != cat_")
                                    .append(filtration.getConditionId()).append(".value)");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("!(fraudList").append(" contains ")
                                .append("input.").append(filtration.getAttribute()).append(")")
                                .toString();

                    case "RANGE":
                        if (ServiceHelper.doesVariableExist(filtration.getAttribute(), CurrTrans.class)) {
                            tempQuery
                                    .append("\n (cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" <= ")
                                    .append("cast(").append(filtration.getMinRange()).append(" as bigint)")
                                    .append(" and ")
                                    .append("cast(ct.").append(convertCamelToSnake(filtration.getAttribute())).append(" as bigint)")
                                    .append(" >= ")
                                    .append("cast(").append(filtration.getMaxRange()).append(" as bigint))");
                        } else {
                            this.filtrationJoinQuery
                                    .append("join curr_addt_trans cat_")
                                    .append(filtration.getConditionId())
                                    .append(" on ct.utrnno = cat_").append(filtration.getConditionId()).append(".utrnno and cat_")
                                    .append(filtration.getConditionId()).append(".attr = '")
                                    .append(filtration.getAttribute()).append("'\n");

                            tempQuery
                                    .append("\n cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) <= ")
                                    .append(filtration.getMinRange()).append(" and cast(cat_")
                                    .append(filtration.getConditionId()).append(".value as bigint) >= ")
                                    .append(filtration.getMaxRange()).append(")");
                        }

                        this.filtrationQuery = this.filtrationQuery.replace(filtration.getConditionId(), tempQuery.toString());

                        return result
                                .append("tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" <= ")
                                .append("tyChe.cvt(").append(filtration.getMinRange()).append(")")
                                .append(" && tyChe.cvt(input.").append(filtration.getAttribute()).append(")")
                                .append(" >= ")
                                .append("tyChe.cvt(").append(filtration.getMaxRange()).append(")").toString();

                    default:
                        throw new RuntimeException(
                                String.format(
                                        "Filtration operator detail: [%s] is unknown",
                                        filtration.getOperatorDetails()
                                )
                        );
                }

            default:
                throw new RuntimeException(
                        String.format(
                                "type: [%s] is unknown",
                                filtration.getOperatorDetails()
                        )
                );
        }
    }

    protected void isOperDetailsList(Filtration filtration, Map<String, Object> dataModel) {
        if (Objects.equals(filtration.getOperatorDetails(), "LIST")) {
            List<String> fraudValues = this.valueRepository.findFraudValueByListName(filtration.getValue());
            LOGGER.info(
                    String.format(
                            "Found fraud value with total data: [%s]",
                            fraudValues.size()
                    )
            );

            dataModel.put("fraudList", fraudValues);
        }
    }

    public boolean getEvaluateStatus() {
        return evaluateStatus;
    }
}
