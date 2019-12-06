package rest.mapper;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import java.math.BigDecimal;

public class MoneyConverter {

    public static final MoneyFormatter FORMATTER = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendAmount(MoneyAmountStyle.LOCALIZED_GROUPING)
            .toFormatter();

    public static Money convertToMoney(long amountInCents) {
        return Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(amountInCents, 2));
    }

    public static String convertToString(Money amount) {
        return FORMATTER.print(amount);
    }
}
