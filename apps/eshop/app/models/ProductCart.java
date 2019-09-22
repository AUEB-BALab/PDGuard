package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This class defines the join table between the entities of Product and Cart.
 * The relationship between these entities is Many to Many but there is also an
 * extra field which is added to this relationship.
 *
 * Therefore, this relationship is constructed manually.
 *
 * @author Thodoris Sotiropoulos
 */
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {"product_product_key", "cart_cart_id"}))
@Entity
public class ProductCart extends Model {
    /** Id of entity. Auto generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** This entity is associated with one product. */
    @ManyToOne
    private Product product;

    /** This entity is associated with one cart. */
    @ManyToOne(cascade = CascadeType.ALL)
    private Cart cart;

    /** Quantity of product which have been added to the cart. */
    @Column
    private int quantity;

    /**
     * This field will be used to programatically make queries which return
     * object of this class.
     */
    private static Finder<Integer, ProductCart> finder = new Finder<>(
            ProductCart.class);

    /**
     * Creates a new product entry to a cart.
     *
     * @param product Product to be added to the cart.
     * @param cart Cart which contains the product.
     * @param quantity Quantity of products to be added to the cart.
     */
    public ProductCart(final Product product, final Cart cart,
            final int quantity) {
        this.product = product;
        this.cart = cart;
        this.quantity = quantity;
    }

    /** This method adds a new product entry to a customer's cart. */
    public void addProductEntry() {
        ProductCart productCart = ProductCart.finder.where().eq(
                "product.productKey", product.getProductKey())
                .eq("cart.cartId", cart.getCartId()).findUnique();
        if (productCart != null) {
            productCart.setQuantity(quantity + productCart.getQuantity());
            productCart.update();
        } else
            this.save();
    }

    /**
     * Getter of id field.
     *
     * @return Id of entity.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Getter of product field.
     *
     * @return Product to be added to the cart.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Getter of cart field.
     *
     * @return Cart which contains product.
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Getter of quantity field.
     *
     * @return Quantity of products to be added to the cart.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter of quantity field.
     *
     * @param quantity Quantity of products to be added to the cart.
     */
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    /**
     * Getter of finder field.
     *
     * @return This field will be used to programatically make queries which
     * returns object of this class.
     */
    public static Finder<Integer, ProductCart> getFinder() {
        return finder;
    }
}
