package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.AttributeSet;
import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ContextProvider;
import io.scriptor.eswin.impl.builtin.ListComponent;
import io.scriptor.eswin.impl.builtin.RouterContext;
import io.scriptor.eswin.impl.builtin.TextFieldComponent;
import io.scriptor.eswin.impl.db.SchemaRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

@Component(
        value = "schema-select",
        layout = "layout/schema.select.xml"
)
public class SchemaSelectComponent extends ComponentBase {

    private final RouterContext ctxRouter;
    private final SourceContext ctxSource;

    public SchemaSelectComponent(
            final @NotNull ContextProvider provider,
            final @Nullable ComponentBase parent,
            final @NotNull AttributeSet attributes,
            final @NotNull String text
    ) {
        super(provider, parent, attributes, text);

        ctxRouter = provider.use(RouterContext.class);
        ctxSource = provider.use(SourceContext.class);
    }

    @SuppressWarnings("unchecked")
    private ListComponent<String> list() {
        return getChild("list", ListComponent.class);
    }

    @Override
    protected void onAttached() {
        final var list = list();
        final var schemas = ctxSource.schemas()
                                     .map(SchemaRef::name)
                                     .toArray(String[]::new);
        list.setListData(schemas);
    }

    public void select() throws SQLException {
        final var list  = list();
        final var value = list.getSelectedValue();

        if (value.isEmpty())
            return;

        if (ctxSource.selectSchema(value.get()))
            ctxRouter.setActive("node-editor");
    }

    public void create() throws SQLException {
        final var name = getChild("name", TextFieldComponent.class);

        if (ctxSource.createSchema(name.getText()))
            ctxRouter.setActive("node-editor");
    }
}
