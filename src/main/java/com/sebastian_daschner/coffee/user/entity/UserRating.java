package com.sebastian_daschner.coffee.user.entity;

import com.sebastian_daschner.coffee.beans.entity.CoffeeBean;
import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity("RATED")
public class UserRating {

    @Id
    @GeneratedValue
    Long id;

    @StartNode
    public User user;

    @EndNode
    public CoffeeBean bean;

    @Property
    public int rating;

    private UserRating() {
    }

    public UserRating(User user, CoffeeBean bean) {
        this.user = user;
        this.bean = bean;
    }

    @Override
    public String toString() {
        return "UserRating{" +
               "id=" + id +
               ", rating=" + rating +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRating that = (UserRating) o;
        return rating == that.rating && Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(bean, that.bean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, bean, rating);
    }

}
