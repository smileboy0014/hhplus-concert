package com.hhplus.hhplusconcert.domain.common.config;

import com.hhplus.hhplusconcert.domain.concert.Concert;
import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.Place;
import com.hhplus.hhplusconcert.domain.concert.Seat;
import com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus;
import com.hhplus.hhplusconcert.support.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hhplus.hhplusconcert.domain.concert.Seat.TicketClass;

@Configuration
@RequiredArgsConstructor
public class DataBatchLoader {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    ApplicationRunner init() {
        return args -> insertMockData();
    }

    public void insertMockData() {
        long totalSize = 1000000;
        int batchSize = 10000;

        List<Place> places = new ArrayList<>();
        List<Concert> concerts = new ArrayList<>();
        List<ConcertDate> concertDates = new ArrayList<>();
        List<Seat> seats = new ArrayList<>();

        Random random = new Random();
        int dateRange = 365;
        int bound = 0;
        long start = 10;
        for (long i = 1; i <= totalSize; i++) {

            bound++;
            Place place = Place.builder().name("Place " + i).totalSeat(50000 + random.nextInt(50001)).build();
            places.add(place);

            Concert concert = Concert.builder().name("Concert" + i).build();
            concerts.add(concert);
            int randomDays = random.nextInt(2 * dateRange + 1) - dateRange;

            ConcertDate concertDate;
            if (bound <= 25000) {
                concertDate = ConcertDate.builder()
                        .concertId(start)
                        .placeId((long) random.nextInt(1000))
                        .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now().plusDays(randomDays)))
                        .build();
                concertDates.add(concertDate);
            } else {
                concertDate = ConcertDate.builder()
                        .concertId(i)
                        .placeId(i)
                        .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.now().plusDays(randomDays)))
                        .build();
                concertDates.add(concertDate);

                bound = 0;
                start += 100;
            }

            if (i % batchSize == 0) {
                batchInsertPlaces(places);
                batchInsertConcerts(concerts);
                batchInsertConcertDates(concertDates);

                places.clear();
                concerts.clear();
                concertDates.clear();
            }
        }


        totalSize = 10000000;
        batchSize = 100000;

        for (long i = 1; i <= totalSize; i++) {

            Seat seat = Seat.builder()
                    .price(BigDecimal.valueOf(100000 + random.nextDouble() * 10000))
                    .seatNumber((int) i)
                    .ticketClass(TicketClass.values()[random.nextInt(TicketClass.values().length)])
                    .concertDateId(i % 1000)
                    .version(1)
                    .status(random.nextBoolean() ? SeatStatus.AVAILABLE : SeatStatus.UNAVAILABLE)
                    .build();
            seats.add(seat);

            if (seats.size() == batchSize) {
                batchInsertSeats(seats);
                seats.clear();
            }
        }

        if (!seats.isEmpty()) {
            batchInsertSeats(seats);
        }

    }


    private void batchInsertPlaces(List<Place> places) {
        String sql = "INSERT INTO place (name, total_seat, created_at, updated_at) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Place place = places.get(i);
                ps.setString(1, place.getName());
                ps.setInt(2, place.getTotalSeat());
                ps.setObject(3, LocalDateTime.now());
                ps.setObject(4, LocalDateTime.now());
            }

            @Override
            public int getBatchSize() {
                return places.size();
            }
        });
    }

    private void batchInsertConcerts(List<Concert> concerts) {
        String sql = "INSERT INTO concert (name, created_at, updated_at) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Concert concert = concerts.get(i);
                ps.setString(1, concert.getName());
                ps.setObject(2, LocalDateTime.now());
                ps.setObject(3, LocalDateTime.now());
            }

            @Override
            public int getBatchSize() {
                return concerts.size();
            }
        });
    }

    private void batchInsertConcertDates(List<ConcertDate> concertDates) {
        String sql = "INSERT INTO concert_date (concert_id, place_id, concert_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ConcertDate concertDate = concertDates.get(i);
                ps.setLong(1, concertDate.getConcertId());
                ps.setLong(2, concertDate.getPlaceId());
                ps.setString(3, concertDate.getConcertDate());
                ps.setObject(4, LocalDateTime.now());
                ps.setObject(5, LocalDateTime.now());
            }

            @Override
            public int getBatchSize() {
                return concertDates.size();
            }
        });
    }

    private void batchInsertSeats(List<Seat> seats) {
        String sql = "INSERT INTO seat (concert_date_id, seat_number, price, status, version, ticket_class, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seat seat = seats.get(i);
                ps.setLong(1, seat.getConcertDateId());
                ps.setInt(2, seat.getSeatNumber());
                ps.setBigDecimal(3, seat.getPrice());
                ps.setString(4, seat.getStatus().name());
                ps.setInt(5, seat.getVersion());
                ps.setString(6, seat.getTicketClass().name());
                ps.setObject(7, LocalDateTime.now());
                ps.setObject(8, LocalDateTime.now());
            }

            @Override
            public int getBatchSize() {
                return seats.size();
            }
        });
    }
}
