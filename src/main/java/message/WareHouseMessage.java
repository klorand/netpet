package message;

import java.io.Serializable;

/**
 */
public class WareHouseMessage implements Serializable {
    protected final int itemId;

    public WareHouseMessage(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "WareHouseMessage{" +
                "itemId=" + itemId +
                '}';
    }

    public int getItemId() {
        return itemId;
    }
}
