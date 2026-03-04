package com.taitl.ex.examples.night_city.data;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;

import java.util.*;

public class CityTestData
{
    public static Location LOCATION_PARK = new Location("Park");
    public static Location LOCATION_GARDEN = new Location("Garden");
    public static Location LOCATION_STREET = new Location("Street");
    public static Location LOCATION_HOUSE = new Location("House");

    public static Address ADDRESS_EAST_ST = new Address("12 East St.");
    public static Address ADDRESS_WEST_ST = new Address("210 West St.");
    public static Address ADDRESS_SOUTH_ST = new Address("78 South St.");
    public static Address ADDRESS_NORTH_ST = new Address("4 North St.");

    public static Cat GREY_CAT = new Cat("Grey", "Park");
    public static Cat YELLOW_CAT = new Cat("Yellow", "Park");
    public static Cat BLACK_CAT = new Cat("Black", "Garden");
    public static Cat ORANGE_CAT = new Cat("Orange", "Garden");

    public static House GREEN_HOUSE = new House("Green", ADDRESS_EAST_ST);
    public static House YELLOW_HOUSE = new House("Yellow", ADDRESS_WEST_ST);
    public static House RED_HOUSE = new House("Red", ADDRESS_SOUTH_ST);
    public static House ORANGE_HOUSE = new House("Orange", ADDRESS_NORTH_ST);

    public static Dwelling<Cat, Rugs> RUG_PILE = new Dwelling<>("Grey", LOCATION_PARK.location());
    public static Dwelling<Cat, Cardboard> CAT_HOUSE = new Dwelling<>("Green", LOCATION_HOUSE.location());
    public static Dwelling<Mouse, Wood> TRASH_CAN = new Dwelling<>("Red", LOCATION_STREET.location());
    public static Dwelling<Mouse, Steel> STOVE_PIPE = new Dwelling<>("Black", LOCATION_HOUSE.location());

    public static Mouse GREY_MOUSE = new Mouse("Grey", LOCATION_STREET.location());
    public static Mouse BLUE_MOUSE = new Mouse("Blue", LOCATION_HOUSE.location());
    public static Mouse RED_MOUSE = new Mouse("Red", LOCATION_GARDEN.location());
    public static Mouse ORANGE_MOUSE = new Mouse("Orange", LOCATION_PARK.location());

    public static Set<Cat> CATS;
    public static Set<Mouse> MICE;
    public static Set<House> HOUSES;
    public static Set<Dwelling<?, ?>> DWELLINGS;

    static
    {
        CATS = Set.of(GREY_CAT, YELLOW_CAT, BLACK_CAT, ORANGE_CAT);
        MICE = new HashSet<>();
        MICE.add(GREY_MOUSE);
        MICE.add(BLUE_MOUSE);
        MICE.add(RED_MOUSE);
        MICE.add(ORANGE_MOUSE);
        HOUSES = Set.of(GREEN_HOUSE, YELLOW_HOUSE, RED_HOUSE, ORANGE_HOUSE);
        DWELLINGS = Set.of(RUG_PILE, CAT_HOUSE, TRASH_CAN, STOVE_PIPE);
    }
}
