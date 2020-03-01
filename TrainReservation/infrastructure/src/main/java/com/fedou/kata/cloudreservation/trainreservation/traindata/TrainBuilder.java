package com.fedou.kata.cloudreservation.trainreservation.traindata;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

public class TrainBuilder {
    private Map<String, List<Integer>> freeSeatsCoachesBuilder = new HashMap<>();
    private Map<String, Integer> totalSeatsCoachesBuilder = new HashMap<>();
    private String trainId;

    public TrainBuilder(String trainId) {
        this.trainId = trainId;
    }

    public TrainBuilder with(TrainData trainData) {
        stream(trainData.seats).forEach(this::with);
        return this;
    }

    public TrainBuilder with(SeatData seat) {
        if (seat.bookingReference == null) {
            addFreeSeat(seat.coach, seat.seatNumber);
        }
        incrementTotalSeat(seat.coach);
        return this;
    }

    private void incrementTotalSeat(String coach) {
        Integer totalSeats = 1 + totalSeatsCoachesBuilder.getOrDefault(coach, 0);
        totalSeatsCoachesBuilder.put(coach, totalSeats);
    }

    private void addFreeSeat(String coach, int seatNumber) {
        List<Integer> seats = freeSeatsCoachesBuilder.computeIfAbsent(coach, key -> new LinkedList<>());
        seats.add(seatNumber);
    }

    public Train build() {
        ArrayList<Coach> coaches = new ArrayList<>();

        this.totalSeatsCoachesBuilder.forEach(
                (key, value) -> coaches.add(
                        new Coach(key,
                                value,
                                freeSeatsCoachesBuilder.getOrDefault(key, emptyList())
                        )
                )
        );
        return new Train(trainId, coaches);
    }
}
