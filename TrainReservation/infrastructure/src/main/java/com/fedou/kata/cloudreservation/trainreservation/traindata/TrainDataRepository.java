package com.fedou.kata.cloudreservation.trainreservation.traindata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
@Primary
public class TrainDataRepository implements TrainDataService {
    private RestTemplate restTemplate;

    @Value("${trainDataHost}")
    private String trainDataHost;

    @Value("${trainDataEndPoint}")
    private String trainDataEndPoint;

    @Value("${trainDataBookingEndPoint}")
    private String trainDataBookingEndPoint;

    @Autowired
    public TrainDataRepository(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    private TrainData getTrainData(String trainId) {
        return restTemplate.getForObject(trainDataHost + "/" + trainDataEndPoint + "/" + trainId, TrainData.class);
    }

    @Override
    public void reserve(String trainId, String bookingReference, List<String> seats) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("trainId", trainId);
        map.add("seats", seats);
        map.add("bookingId", bookingReference);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        restTemplate.postForLocation(trainDataHost + "/" + trainDataBookingEndPoint, request);
    }

    @Override
    public Train getTrainById(String trainId) {
        TrainData trainData = getTrainData(trainId);
        return new TrainBuilder(trainId).with(trainData).build();
    }
}
