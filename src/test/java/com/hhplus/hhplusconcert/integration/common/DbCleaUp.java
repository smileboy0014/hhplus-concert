package com.hhplus.hhplusconcert.integration.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ActiveProfiles("test")
public class DbCleaUp {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void execute() {
        List<String> tableNames = getTableNames();
        entityManager.flush();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 모든 테이블의 데이터를 삭제합니다.
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // 모든 시퀀스를 재설정합니다.(PK 1부터 다시 시작)
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private List<String> getTableNames() {
        return entityManager.getMetamodel().getEntities().stream()
                .map(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    Table table = javaType.getAnnotation(Table.class);
                    if (table != null && !table.name().isEmpty()) {
                        return table.name();
                    } else {
                        return convertToSnakeCase(entityType.getName());
                    }
                })
                .collect(Collectors.toList());
    }

    private String convertToSnakeCase(String entityName) {
        return entityName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }


}
