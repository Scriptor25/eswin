package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.impl.builtin.ListComponent;
import io.scriptor.eswin.impl.builtin.RouterContext;
import io.scriptor.eswin.impl.builtin.TextFieldComponent;
import io.scriptor.eswin.impl.model.DatabaseRef;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Component(
        value = "database-select",
        layout = "layout/database.select.xml"
)
public class DatabaseSelectComponent extends ComponentBase {

    private final RouterContext ctxRouter;
    private final SourceContext ctxSource;

    public DatabaseSelectComponent(final @NotNull ComponentInfo info) {
        super(info);

        ctxRouter = getProvider().use(RouterContext.class);
        ctxSource = getProvider().use(SourceContext.class);
    }

    @SuppressWarnings("unchecked")
    private ListComponent<String> list() {
        return getChild("list", ListComponent.class);
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        final var list = list();
        final var databases = ctxSource.getDatabases()
                                       .map(DatabaseRef::getName)
                                       .toArray(String[]::new);
        list.setListData(databases);
    }

    public void select() throws SQLException {
        final var list  = list();
        final var value = list.getSelectedValue();

        if (value.isEmpty())
            return;

        ctxSource.selectDatabase(value.get());
        ctxRouter.setActive("schema-select");
    }

    public void create() throws SQLException {
        final var name = getChild("name", TextFieldComponent.class);

        ctxSource.createDatabase(name.getText());
        ctxRouter.setActive("schema-select");
    }
}
