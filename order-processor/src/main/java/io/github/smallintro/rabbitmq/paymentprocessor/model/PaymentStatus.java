package io.github.smallintro.rabbitmq.paymentprocessor.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PaymentStatus {
    @JsonProperty("payment_request_id")
    private String paymentRequestId;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("message")
    private String message;

    @JsonProperty("payment_info")
    private PaymentInfo paymentInfo;
}
