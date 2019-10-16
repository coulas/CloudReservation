package com.fedou.kata.cloudreservation.traindata;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainDataServerTest extends TrainDataApplicationTests {

    @Test
    void getTrainDataById() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/data_for_train/express_2000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TrainDataDTO trainData = getResultAsType(mvcResult, TrainDataDTO.class);
        TrainDataDTO trainDataExpected = parseTo(
                "{\"seats\":[{\"seatNumber\":1,\"coach\":\"A\",\"name\":\"1A\"},{\"seatNumber\":2,\"coach\":\"A\",\"name\":\"2A\"},{\"seatNumber\":3,\"coach\":\"A\",\"name\":\"3A\"},{\"seatNumber\":4,\"coach\":\"A\",\"name\":\"4A\"},{\"seatNumber\":5,\"coach\":\"A\",\"name\":\"5A\"},{\"seatNumber\":6,\"coach\":\"A\",\"name\":\"6A\"},{\"seatNumber\":7,\"coach\":\"A\",\"name\":\"7A\"},{\"seatNumber\":8,\"coach\":\"A\",\"name\":\"8A\"},{\"seatNumber\":9,\"coach\":\"A\",\"name\":\"9A\"},{\"seatNumber\":10,\"coach\":\"A\",\"name\":\"10A\"},{\"seatNumber\":1,\"coach\":\"B\",\"name\":\"1B\"},{\"seatNumber\":2,\"coach\":\"B\",\"name\":\"2B\"},{\"seatNumber\":3,\"coach\":\"B\",\"name\":\"3B\"},{\"seatNumber\":4,\"coach\":\"B\",\"name\":\"4B\"},{\"seatNumber\":5,\"coach\":\"B\",\"name\":\"5B\"},{\"seatNumber\":6,\"coach\":\"B\",\"name\":\"6B\"},{\"seatNumber\":7,\"coach\":\"B\",\"name\":\"7B\"},{\"seatNumber\":8,\"coach\":\"B\",\"name\":\"8B\"},{\"seatNumber\":9,\"coach\":\"B\",\"name\":\"9B\"},{\"seatNumber\":10,\"coach\":\"B\",\"name\":\"10B\"}]}",
                TrainDataDTO.class
        );
        assertThat(trainData)
                .isEqualToComparingFieldByField(trainDataExpected);
    }

    @Test
    void seatData_is_equal_by_value_ignoring_booking() {
        SeatDataDTO noBooking = new SeatDataDTO(null, "A", 1);
        SeatDataDTO nonExisintgBooking = new SeatDataDTO("", "A", 1);
        SeatDataDTO withBooking = new SeatDataDTO("booked", "A", 1);

        assertThat(noBooking).isEqualTo(withBooking).isEqualTo(nonExisintgBooking);
        assertThat(noBooking.bookingReference).isNotEqualTo(nonExisintgBooking.bookingReference);
    }

    @Test
    void reserve() throws Exception {
        mvc.perform(
                post("/reserve")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("trainId", "express_2000")
                        .param("seats", "5A", "6A")
                        .param("bookingReference", "anotherThing"))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult mvcResult = mvc.perform(get("/data_for_train/express_2000"))
                .andExpect(status().isOk())
                .andReturn();
        TrainDataDTO trainData = getResultAsType(mvcResult, TrainDataDTO.class);
        assertThat(trainData.seats).extracting("bookingReference", "coach", "seatNumber").contains(
                new Tuple(null, "A", 4),
                new Tuple("anotherThing", "A", 5),
                new Tuple("anotherThing", "A", 6),
                new Tuple(null, "A", 7),
                new Tuple(null, "B", 5)
                );
    }
}