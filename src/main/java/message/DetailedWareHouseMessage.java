package message;

import com.google.common.base.Objects;

/**
 */
public class DetailedWareHouseMessage extends WareHouseMessage {

	protected final String name;
	protected final String description;


	public DetailedWareHouseMessage(int itemId, String name, String description) {
		super(itemId);
		this.name = name;
		this.description = description;
	}

    @Override
    public String toString() {
        return "DetailedWareHouseMessage{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
