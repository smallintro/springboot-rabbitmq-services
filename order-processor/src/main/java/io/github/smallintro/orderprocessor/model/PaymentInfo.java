package io.github.smallintro.orderprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PaymentInfo {
    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("payment_amount")
    private float paymentAmount;

    @JsonProperty("payment_priority")
    private String paymentPriority;
}
