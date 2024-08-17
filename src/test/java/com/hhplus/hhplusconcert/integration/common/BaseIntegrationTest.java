package com.hhplus.hhplusconcert.integration.common;


import com.hhplus.hhplusconcert.infrastructure.redis.RedisRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
        ports = { 9092 }
)
public class BaseIntegrationTest {

    @Autowired
    private DbCleaUp dbCleaUp;

    @Autowired
    private TestDataHandler testDataHandler;

    @Autowired
    private RedisRepository redisRepository;

    @LocalServerPort
    protected int port;

    protected static final String LOCAL_HOST = "http://localhost:";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        // 초기 콘서트 정보 세팅
        testDataHandler.settingConcertInfo();
        testDataHandler.reserveSeats();
    }

    @AfterEach
    void tearDown() {
        // 데이터 초기화
        dbCleaUp.execute();
        redisRepository.clearCurrentDatabase();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured
                .given().log().all()
                .when().get(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> get(String path, Map<String, ?> parameters) {
        return RestAssured
                .given().log().all()
                .queryParams(parameters)
                .when().get(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> post(String path) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all().extract();
    }

    public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured
                .given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(String path) {
        return RestAssured
                .given().log().all()
                .when().put(path)
                .then().log().all().extract();
    }

    public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
        return RestAssured
                .given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> patch(String path) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch(path)
                .then().log().all().extract();
    }

    public static <T> ExtractableResponse<Response> patch(String path, T requestBody) {
        return RestAssured
                .given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured
                .given().log().all()
                .when().delete(path)
                .then().log().all().extract();
    }

    public static <T> ExtractableResponse<Response> delete(String path, Map<String, ?> parameters) {
        return RestAssured
                .given().log().all()
                .queryParams(parameters)
                .when().delete(path)
                .then().log().all().extract();
    }

}
