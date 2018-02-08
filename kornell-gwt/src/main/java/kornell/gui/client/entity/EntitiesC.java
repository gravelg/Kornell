package kornell.gui.client.entity;

import com.google.gwt.core.shared.GWT;

import kornell.core.entity.EntityFactory;

public class EntitiesC extends kornell.core.entity.Entities {
    private static final EntitiesC instance = new EntitiesC();
    private static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

    @Override
    protected EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public static EntitiesC get() {
        return instance;
    }

}
