package com.sebastian_daschner.coffee.beans.control;

import com.sebastian_daschner.coffee.beans.entity.UserRating;

import javax.json.bind.adapter.JsonbAdapter;

public class UserRatingTypeAdapter implements JsonbAdapter<UserRating, Integer> {

    @Override
    public Integer adaptToJson(UserRating rating) {
        return rating.rating;
    }

    @Override
    public UserRating adaptFromJson(Integer integer) {
        throw new UnsupportedOperationException();
    }
}
