package ru.investportfolio.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "deals")
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "ticker")
    private String ticker;

    //Price of single share
    @Column(name = "acquisition_price")
    private BigDecimal acquisitionPrice;

    @Column(name = "quantity")
    private Long quantity;

    //Value of all shares in deal = acquisitionPrice * quantity
    @Column(name = "acquisition_value")
    private BigDecimal acquisitionValue;

    @Enumerated(EnumType.STRING)
    private DealType dealType;

    @Column(name = "market_commission")
    private BigDecimal marketCommission;

    @Column(name = "broker_commission")
    private BigDecimal brokerCommission;

    @Column(name = "other_commission")
    private BigDecimal otherCommission;

    //Value of all shares in deal including all commissions
    @Column(name = "total_value")
    private BigDecimal totalAcquisitionValue;

    @Column(name = "deal_date")
    private LocalDate date;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

}
