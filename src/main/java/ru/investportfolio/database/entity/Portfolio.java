package ru.investportfolio.database.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<Position> positions = new ArrayList<>();

    @Column(name = "cash")
    private BigDecimal cash = BigDecimal.ZERO;

    @Transient
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Transient
    private BigDecimal positionsValue = BigDecimal.ZERO;

    @Transient
    private BigDecimal profitLoss = BigDecimal.ZERO;

    @Transient
    private Double profitLossPercentage = (double) 0;

    public Portfolio() {
    }

    public Portfolio(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
