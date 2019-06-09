package my.vaadin.app;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import my.vaadin.MyUI;

public class CustomerForm extends CustomerFormDesign {
    private CustomerService service = CustomerService.getInstance();
    private Customer customer;
    private MyUI myUI;
    //The Binder will bind a Vaadin instance with fields to the specified class (Customer.class in this case)
    private Binder<Customer> binder = new Binder<>(Customer.class);

    public CustomerForm(MyUI myUI) {
        this.myUI = myUI;
        status.setItems(CustomerStatus.values());
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        //the bindInstanceFields takes an object with fields and binds it to the Binder class
        //Binder class is Customer.class
        //Object with fields is the
        binder.bindInstanceFields(this);

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());

        setCustomer(new Customer());
        setVisible(false);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        binder.setBean(customer);

        // Show delete button for only customers already in the database
        delete.setVisible(customer.isPersisted());
        setVisible(true);
        firstName.selectAll();
    }

    private void delete() {
        service.delete(customer);
        myUI.updateList();
        setVisible(false);
    }

    private void save() {
        service.save(binder.getBean());
        myUI.updateList();
        setVisible(false);
    }
}
