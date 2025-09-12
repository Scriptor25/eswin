package io.scriptor.eswin.impl;

import io.scriptor.eswin.component.Component;
import io.scriptor.eswin.component.ComponentBase;
import io.scriptor.eswin.component.ComponentInfo;
import io.scriptor.eswin.impl.builtin.ListComponent;
import io.scriptor.eswin.impl.builtin.RouterContext;
import io.scriptor.eswin.impl.builtin.TextFieldComponent;
import io.scriptor.eswin.impl.model.ServerRef;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Component(
        value = "server-select",
        layout = "layout/server.select.xml"
)
public class ServerSelectComponent extends ComponentBase {

    private final RouterContext ctxRouter;
    private final SourceContext ctxSource;

    public ServerSelectComponent(final @NotNull ComponentInfo info) {
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
        final var servers = ctxSource.getServers()
                                     .map(ServerRef::label)
                                     .toArray(String[]::new);
        list.setListData(servers);
    }

    public void select() throws SQLException {
        final var list  = list();
        final var index = list.getSelectedIndex();

        if (index < 0)
            return;

        ctxSource.selectServer(index);
        ctxRouter.setActive("database-select");
    }

    public void create() throws SQLException {
        final var label    = getChild("label", TextFieldComponent.class);
        final var url      = getChild("url", TextFieldComponent.class);
        final var username = getChild("username", TextFieldComponent.class);
        final var password = getChild("password", TextFieldComponent.class);

        ctxSource.createServer(label.getText(), url.getText(), username.getText(), password.getText());
        ctxRouter.setActive("database-select");
    }
}
