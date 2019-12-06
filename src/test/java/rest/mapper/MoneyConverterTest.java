package rest.mapper;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyConverterTest {

    @Test
    void testConvertToMoney() {
        Money amount = MoneyConverter.convertToMoney(1000);

        assertEquals(BigDecimal.valueOf(1000, 2), amount.getAmount());
        assertEquals(CurrencyUnit.EUR, amount.getCurrencyUnit());
    }

    @Test
    void testConvertToString() {
        Money amount = Money.of(CurrencyUnit.EUR, new BigDecimal("2.5"));

        assertEquals("EUR2.50", MoneyConverter.convertToString(amount));
    }
}