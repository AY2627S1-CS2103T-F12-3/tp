package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MenuPanelTest {
    @Test
    void formatPrice_wholeAmount_formatsWithTwoDecimalPlaces() {
        assertEquals("$4.00", MenuPanel.formatPrice(400));
    }

    @Test
    void formatPrice_amountWithCents_preservesLeadingZero() {
        assertEquals("$4.05", MenuPanel.formatPrice(405));
    }

    @Test
    void formatPrice_largeAmount_formatsDollarsAndCents() {
        assertEquals("$123456.78", MenuPanel.formatPrice(12_345_678));
    }
}
