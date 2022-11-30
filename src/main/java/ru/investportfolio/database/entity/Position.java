package ru.investportfolio.database.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "acquisition_price")
    private BigDecimal acquisitionPrice = BigDecimal.ZERO;

    @Column(name = "acquisition_value")
    private BigDecimal acquisitionValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_type")
    private PositionType positionType;

    @Column(name = "quantity")
    private Long quantity = 0L;

    @Column(name = "total_value")
    private BigDecimal totalAcquisitionValue = BigDecimal.ZERO;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<Deal> deals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Transient
    private BigDecimal currentPrice;

    @Transient
    private BigDecimal liquidationValue;

    @Transient
    private BigDecimal profitLoss;

    @Transient
    private Double profitLossPercentage;

    public Position() {
    }

    public Position(Portfolio portfolio){
        this.portfolio = portfolio;
    }

    public Position(Portfolio portfolio, String securityName, String ticker){
        this.portfolio = portfolio;
        this.securityName = securityName;
        this.ticker = ticker;
    }

    public Position(String securityName,
                    String ticker,
                    BigDecimal acquisitionPrice,
                    PositionType positionType,
                    Long quantity,
                    List<Deal> deals,
                    Portfolio portfolio) {
        this.securityName = securityName;
        this.ticker = ticker;
        this.acquisitionPrice = acquisitionPrice;
        this.positionType = positionType;
        this.quantity = quantity;
        this.deals = deals;
        this.portfolio = portfolio;
    }

}
