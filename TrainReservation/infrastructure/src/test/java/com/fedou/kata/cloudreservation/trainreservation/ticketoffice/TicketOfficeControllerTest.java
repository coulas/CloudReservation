package com.fedou.kata.cloudreservation.trainreservation.ticketoffice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fedou.kata.cloudreservation.trainreservation.TrainReservationApplicationTests;
import com.fedou.kata.cloudreservation.trainreservation.traindata.SeatData;
import com.fedou.kata.cloudreservation.trainreservation.traindata.TrainData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.fedou.kata.cloudreservation.traindata.TrainDataBuilder.theAcceptanceTrain;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class TicketOfficeControllerTest extends TrainReservationApplicationTests {

    @WithMockUser
    @Test
    public void make_a_simple_reservation() throws Exception {
        String trainId = "express_2000";
        TrainData trainData = theAcceptanceTrain();
        String bookingId = "something";
        List<String> bookedSeats = asList("1A", "2A");

        givenTheTrainDataServerWillProvide(trainId, trainData);
        givenTheBookingReferenceServerWillProvide(bookingId);
        ReservationRequestDTO bookingRequest = new ReservationRequestDTO(trainId, 2);
        givenTheTrainDataServerWillBeCalledForAReservation();

        ReservationDTO actualReservation = whenMakeReservationFor(bookingRequest);

        ReservationDTO expectedReservation = new ReservationDTO(
                trainId,
                bookingId,
                bookedSeats);
        assertAll(
                () -> thenTheTrainDataServerReceives(expectedReservation),
                () -> assertThat(actualReservation)
                        .isEqualToComparingFieldByField(expectedReservation)
        );
    }

    @WithMockUser
    @Test
    public void no_seats_found() throws Exception {
        String trainId = "express_2000";
        TrainData trainData = new TrainData();
        trainData.seats = new SeatData[]{
                new SeatData("booked", "A", 1),
                new SeatData("booked", "A", 2),
                new SeatData("booked", "A", 3)
        };
        String bookingId = "something";
        List<String> bookedSeats = emptyList();

        givenTheTrainDataServerWillProvide(trainId, trainData);
        ReservationRequestDTO bookingRequest = new ReservationRequestDTO(trainId, 1);
        givenTheTrainDataServerWillBeCalledForAReservation();

        ReservationDTO actualReservation = whenMakeReservationFor(bookingRequest);

        ReservationDTO expectedReservation = new ReservationDTO(
                trainId,
                "",
                bookedSeats);
        assertAll(
                this::thenTheTrainDataServerDoNotReceivesReservation,
                () -> assertThat(actualReservation)
                        .isEqualToComparingFieldByField(expectedReservation)
        );
    }

    @BeforeEach
    public void resetMocks() {
        reset();
    }

    private void givenTheBookingReferenceServerWillProvide(String bookingId) {
        givenThat(get("/booking_reference").willReturn(ok(bookingId)));
    }

    private void givenTheTrainDataServerWillProvide(String trainId, TrainData trainData) throws JsonProcessingException {
        givenThat(get("/data_for_train/" + trainId)
                .willReturn(aResponse()
                        .withHeader("content-type", "application/json")
                        .withBody(getRequestObjectAsJson(trainData))));
    }

    private void givenTheTrainDataServerWillBeCalledForAReservation() {
        givenThat(post("/reserve").willReturn(ok()));
    }

    private ReservationDTO whenMakeReservationFor(ReservationRequestDTO bookingRequest) throws Exception {
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post("/reservation/makeReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getRequestObjectAsJson(bookingRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return getResponseContentAs(result, ReservationDTO.class);
    }

    private void thenTheTrainDataServerReceives(ReservationDTO reservation) throws JsonProcessingException {
        String body = "{ \"trainId\": [\"express_2000\"], " +
                "\"seats\": [[\"1A\", \"2A\"]], " +
                "\"bookingId\": [\"" + reservation.getBooking_reference() + "\"]}";
        verify(1, postRequestedFor(urlEqualTo("/reserve"))
                .withRequestBody(equalToJson(body)));
    }

    private void thenTheTrainDataServerDoNotReceivesReservation() {
        verify(0, postRequestedFor(urlEqualTo("/reserve")));
    }
}
