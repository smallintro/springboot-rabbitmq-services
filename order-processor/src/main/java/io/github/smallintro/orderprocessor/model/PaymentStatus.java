package io.github.smallintro.orderprocessor.model;


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
    @JsonProperty("paymentRequestId")
    private String payment_request_id;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("message")
    private String message;

    @JsonProperty("payment_info")
    private PaymentInfo paymentInfo;
}
