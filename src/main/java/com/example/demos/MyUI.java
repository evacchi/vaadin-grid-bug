package com.example.demos;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.grid.EditorClientRpc;
import com.vaadin.ui.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

@Theme("valo")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        ArrayList<Address> addrs = new ArrayList<>();
        addrs.add(new Address("foo"));

        final Grid grid2 = new Grid(new BeanItemContainer<>(Address.class, addrs));
        grid2.setEditorEnabled(true);

        final Button edit = new Button("edit"),
                     save = new Button("save");



        layout.addComponents(grid2, edit, save);

        edit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                System.out.println("selected row " + grid2.getSelectedRow());
                grid2.editItem(grid2.getSelectedRow());
            }
        });


        save.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                boolean success = true;
                try {
                    grid2.saveEditor();

                    try {
                        Method m = Grid.class.getDeclaredMethod("getEditorRpc");
                        m.setAccessible(true);

                        EditorClientRpc rpc = (EditorClientRpc) m.invoke(grid2);
                        rpc.confirmSave(success, null, null);

                    } catch (Exception ex) { throw new Error(ex); }



                    grid2.cancelEditor();
                    Object oldSel = grid2.getSelectedRow();

                    grid2.setCellStyleGenerator(grid2.getCellStyleGenerator());
                    Grid.SelectionModel oldSelModel = grid2.getSelectionModel();
                    grid2.setSelectionMode(Grid.SelectionMode.NONE);
                    grid2.setSelectionModel(oldSelModel);

                    grid2.select(oldSel);

                } catch (FieldGroup.CommitException ex) {
                    ex.printStackTrace();
                    success = false;
                }

            }
        });

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
