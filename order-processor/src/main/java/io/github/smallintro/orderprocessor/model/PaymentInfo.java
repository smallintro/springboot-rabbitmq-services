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
public class PaymentInfo {
    private String paymentId;
    private String paymentType;
    private int paymentAmount;
    private String paymentPriority;
}
