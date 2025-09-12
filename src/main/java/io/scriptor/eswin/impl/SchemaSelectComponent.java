package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.impl.builtin.ListComponent;
import io.scriptor.eswin.impl.builtin.RouterContext;
import io.scriptor.eswin.impl.builtin.TextFieldComponent;
import io.scriptor.eswin.impl.model.SchemaRef;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Component(
        value = "schema-select",
        layout = "layout/schema.select.xml"
)
public class SchemaSelectComponent extends ComponentBase {

    private final RouterContext ctxRouter;
    private final SourceContext ctxSource;

    public SchemaSelectComponent(final @NotNull ComponentInfo info) {
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
        final var schemas = ctxSource.getSchemas()
                                     .map(SchemaRef::getName)
                                     .sorted()
                                     .toArray(String[]::new);
        list.setListData(schemas);
    }

    public void select() throws SQLException {
        final var list  = list();
        final var value = list.getSelectedValue();

        if (value.isEmpty())
            return;

        ctxSource.selectSchema(value.get());
        ctxRouter.setActive("editor");
    }

    public void create() throws SQLException {
        final var name = getChild("name", TextFieldComponent.class);

        ctxSource.createSchema(name.getText());
        ctxRouter.setActive("editor");
    }
}
