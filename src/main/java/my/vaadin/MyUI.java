package my.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import my.vaadin.app.Customer;
import my.vaadin.app.CustomerForm;
import my.vaadin.app.CustomerService;

import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    private CustomerService service = CustomerService.getInstance();

    Grid<Customer> grid = new Grid<>(Customer.class);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(addButton());
        mainLayout.addComponent(addTable());
        setContent(mainLayout);
    }

    public void updateList() {
        updateList(null);
    }

    public void updateList(String filterValue) {
        if(filterValue != null && !filterValue.isEmpty()) {
            grid.setItems(service.findAll(filterValue));
        } else {
            grid.setItems(service.findAll());
        }
    }

    private VerticalLayout addButton() {
        VerticalLayout buttonLayout = new VerticalLayout();

        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            buttonLayout.addComponent(new Label("Thanks " + name.getValue()
                    + ", it works!"));
        });

        buttonLayout.addComponents(name, button);

        return buttonLayout;
    }

    private VerticalLayout addTable() {
        VerticalLayout tableLayout = new VerticalLayout();
        TextField filterText = new TextField();

        grid.setColumns("firstName", "lastName", "email");
        updateList();

        filterText.setCaption("Filter ...");
        filterText.addValueChangeListener(e -> updateList(((TextField)e.getComponent()).getValue()));

        Button clearFilterTextBtn = new Button(VaadinIcons.CLOSE);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        Button addNewCustomerBtn  = new Button();
        addNewCustomerBtn.setDescription("Add new customer");
        addNewCustomerBtn.setCaption("New");

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        CustomerForm customerForm = new CustomerForm(this);
        addNewCustomerBtn.addClickListener(e -> {
            grid.asSingleSelect().clear();
            customerForm.setCustomer(new Customer());
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(grid, customerForm);
        horizontalLayout.setSizeFull();
        grid.setSizeFull();
        horizontalLayout.setExpandRatio(grid,1);

        //grid.addItemClickListener(e -> customerForm.setCustomer(e.getItem()));

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                customerForm.setVisible(false);
            } else {
                customerForm.setCustomer(event.getValue());
            }
        });

        tableLayout.addComponents(new HorizontalLayout(filtering,addNewCustomerBtn),horizontalLayout);

        return tableLayout;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
