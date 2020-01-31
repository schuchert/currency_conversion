package org.shoe.http_scrapping;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CurrencyConverterTest {
    @Test
    void foo() throws Exception {
        CurrencyConverter mock = Mockito.mock(CurrencyConverter.class);
        when(mock.readConversionsImpl(anyString(), anyString())).thenReturn(new StringBuffer(""));

        CurrencyConverter original = CurrencyConverter.reset(mock);

        CurrencyConverter.instance().convertFromToImpl("USD", "USD");

        CurrencyConverter.reset(original);
    }
}
