package DataImport;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FileFooter {
    private LocalDate _footerDate;
    private BigDecimal _marketPrice;

    public FileFooter(LocalDate date, BigDecimal price) {
        _footerDate = date;
        _marketPrice = price;
    }

    public LocalDate getFooterDate() { return _footerDate; }
    public BigDecimal getMarketPrice() { return _marketPrice; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileFooter) {
            FileFooter castedObj = (FileFooter) obj;
            return _footerDate.equals(castedObj.getFooterDate()) && _marketPrice.equals(castedObj.getMarketPrice());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Date: " + _footerDate.toString() + ", Price: " + _marketPrice.toString();
    }
}
