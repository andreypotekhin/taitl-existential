package com.taitl.existential.test_data;

import com.taitl.existential.model.Cat;
import com.taitl.existential.model.Location;

public class TestData
{
    public static Location LOCATION_PARK = new Location("Park");
    public static Location LOCATION_GARDEN = new Location("Garden");

    public static Cat GREY_CAT = new Cat("Grey", "Park");
    public static Cat YELLOW_CAT = new Cat("Yellow", "Park");
    public static Cat BLACK_CAT = new Cat("Black", "Garden");
    public static Cat ORANGE_CAT = new Cat("Orange", "Garden");
}
