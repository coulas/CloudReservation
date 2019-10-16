package com.fedou.kata.cloudreservation.traindata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;

@RestController
public class TrainDataServer {
    private HashMap<String, TrainDataDTO> trainDatasById;

    public TrainDataServer() {
        trainDatasById = new HashMap<>();
        addAcceptanceTrain();
    }

    private void addAcceptanceTrain() {
        TrainDataDTO trainData = new TrainDataDTO();
        ArrayList<SeatDataDTO> seatDatas = new ArrayList<>();

        for (String coach : new String[]{"A", "B"}) {
            for (int seatNumber : rangeClosed(1, 10).toArray()) {
                seatDatas.add(
                        new SeatDataDTO(
                                null,
                                coach,
                                seatNumber));
            }
        }

        trainData.seats = seatDatas.toArray(new SeatDataDTO[seatDatas.size()]);
        trainDatasById.put("express_2000", trainData);
    }

    @RequestMapping("data_for_train/{trainId}")
    public TrainDataDTO getTrainDataById(@PathVariable String trainId) {
        TrainDataDTO trainData = trainDatasById.get(trainId);
        // TODO: use logger
        return trainData;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/reserve", consumes = "application/json")
    public void reserve(@RequestBody Map<String, Object> arguments) throws IOException {
        String trainId = ((List<String>) arguments.get("trainId")).get(0);
        TrainDataDTO trainData = trainDatasById.get(trainId);
        List<String> seats = ((List<ArrayList<String>>) arguments.get("seats")).stream().flatMap(Collection::stream).collect(Collectors.toList());
        System.err.println(String.join(" - ", seats));
        // String[] seats = seatsArguments.substring(1, seatsArguments.length() - 1).split(",");
        for (String seat : seats) {
            String[] tokens = seat.split("");
            SeatDataDTO bookingSeat = new SeatDataDTO(
                    (String) arguments.get("bookingReference"),
                    tokens[1],
                    Integer.parseInt(tokens[0]));
            for (int i = 0; i < trainData.seats.length; i++) {
                SeatDataDTO trainSeat = trainData.seats[i];
                if (trainSeat.coach.equalsIgnoreCase(bookingSeat.coach)
                        && trainSeat.seatNumber == bookingSeat.seatNumber) {
                    trainData.seats[i] = bookingSeat;
                    System.err.println("seat found " + bookingSeat);
                }
            }
        }
    }
}
