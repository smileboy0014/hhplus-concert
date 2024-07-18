package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertAppender {

    private final ConcertRepository concertRepository;

    public Place appendPlace(Place place) {
        return concertRepository.addPlace(place);
    }

    public Concert appendConcert(Concert concert) {
        return concertRepository.addConcert(concert);
    }

    public List<Seat> appendSeats(List<Seat> seats) {
        return concertRepository.addSeats(seats);
    }

    public Reservation appendReservation(Reservation reservation) {
        return concertRepository.reserve(reservation);
    }

    public List<ConcertDate> appendConcertDates(List<ConcertDate> concertDates) {
        return concertRepository.addConcertDates(concertDates);
    }

    public void deleteAll() {
        concertRepository.deleteAll();
    }
}
