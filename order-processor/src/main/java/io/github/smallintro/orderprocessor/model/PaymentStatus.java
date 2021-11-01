package io.github.smallintro.orderprocessor.model;


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
    private String paymentRequestId;
    private String paymentStatus;
    private String message;
    private PaymentInfo paymentInfo;
}
