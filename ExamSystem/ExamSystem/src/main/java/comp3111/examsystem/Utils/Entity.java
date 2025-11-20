package comp3111.examsystem.Utils;


/**
 * Represents a generic entity with an ID.
 * This class implements Serializable and Comparable interfaces.
 */
public class Entity implements java.io.Serializable, Comparable<Entity> {
    protected Long id = 0L;


    /**
     * Gets the ID of the entity.
     *
     * @return the ID of the entity
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the ID of the entity.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Default constructor for creating an Entity object.
     */
    public Entity() {
        super();
    }


    /**
     * Constructor for creating an Entity object with a specified ID.
     *
     * @param id the ID of the entity
     */
    public Entity(Long id) {
        super();
        this.id = id;
    }


    /**
     * Compares this entity with another entity based on their IDs.
     *
     * @param o the entity to compare with
     * @return a negative integer, zero, or a positive integer as this entity's ID
     *         is less than, equal to, or greater than the specified entity's ID
     */
    @Override
    public int compareTo(Entity o) {
        return Long.compare(this.id, o.id);
    }
}
